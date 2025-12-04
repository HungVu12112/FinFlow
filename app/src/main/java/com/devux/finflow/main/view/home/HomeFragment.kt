package com.devux.finflow.main.view.home

import com.devux.finflow.main.view.adapter.TransactionAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.databinding.FragmentHomeBinding
import com.devux.finflow.helper.PreferencesHelper
import com.devux.finflow.main.view.adapter.BudgetHomeAdapter
import com.devux.finflow.utils.CurrencyUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var transactionAdapter: TransactionAdapter
    private val budgetHomeAdapter = BudgetHomeAdapter()
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationListenerPermission(requireContext())
        requestPostNotificationPermission(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {
        setupEarningsList()
        setupSavingsList()
        setupTransactionsList()
        setupTimeFilterTabs()
    }

    override fun setAction() {
        binding.rvHomeBudget.apply {
            adapter = budgetHomeAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        binding.btnSetupBudget.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_budgetFragment)
        }
        binding.tvEmptyBudget.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_budgetFragment)
        }
    }

    override fun setObserve() {
        viewModel.currentTransactions.observe(viewLifecycleOwner) { list ->
            // Update RecyclerView
            transactionAdapter.submitList(list)
        }
        viewModel.allCategories.observe(this) { categories ->
            if (categories != null) {
                transactionAdapter.setCategories(categories)
            }
        }
        viewModel.homeBudgets.observe(viewLifecycleOwner) { list ->
            binding.layoutBudgetWidget.visibility = View.VISIBLE

            if (list.isNullOrEmpty()) {
                binding.rvHomeBudget.visibility = View.GONE
                binding.tvEmptyBudget.visibility = View.VISIBLE
            } else {
                binding.rvHomeBudget.visibility = View.VISIBLE
                binding.tvEmptyBudget.visibility = View.GONE

                budgetHomeAdapter.submitList(list)
            }
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvIncomeAmount.text = CurrencyUtils.formatCurrency(income)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvOutcomeAmount.text = CurrencyUtils.formatCurrency(expense)
        }
        viewModel.totalBalance.observe(viewLifecycleOwner){ balance ->
            binding.tvTotalBalanceAmount.text = CurrencyUtils.formatCurrency(balance)
        }
    }

    fun checkNotificationListenerPermission(context: Context): Boolean {
        val enabledListeners = android.provider.Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        val packageName = context.packageName
        return enabledListeners != null && enabledListeners.contains(packageName)
    }

    fun requestNotificationListenerPermission(context: Context) {
        if (!checkNotificationListenerPermission(context)) {
            android.app.AlertDialog.Builder(context)
                .setTitle("Cấp quyền đọc thông báo")
                .setMessage("Ứng dụng cần quyền để đọc thông báo ngân hàng. Vui lòng bật trong cài đặt.")
                .setPositiveButton("Mở cài đặt") { _, _ ->
                    context.startActivity(
                        android.content.Intent(
                            android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                        )
                    )
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    fun requestPostNotificationPermission(activity: androidx.fragment.app.FragmentActivity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            when {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    Log.d("Permission", "✅ POST_NOTIFICATIONS permission granted")
                }

                activity.shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    android.app.AlertDialog.Builder(activity)
                        .setTitle("Cấp quyền gửi thông báo")
                        .setMessage("Ứng dụng cần quyền gửi thông báo để bạn có thể nhận thông tin về giao dịch ngân hàng.")
                        .setPositiveButton("Cấp quyền") { _, _ ->
                            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
                            androidx.core.app.ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                1001
                            )
                        }
                        .setNegativeButton("Hủy", null)
                        .show()
                }

                else -> {
                    androidx.core.app.ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                }
            }
        }
    }

    private fun setupEarningsList() {
        binding.rvEarnings.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSavingsList() {
        binding.rvSavings.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupTransactionsList() {
        transactionAdapter = TransactionAdapter()
        viewModel.allTransaction.observe(this) { transactionList ->
            if (transactionList != null) {
                transactionAdapter.submitList(transactionList)
            }
        }

        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
            isNestedScrollingEnabled = false
        }
    }
    private fun setupTimeFilterTabs() {
        // Chọn tab "Tháng" mặc định (vị trí 1)
        val defaultTab = binding.tabTimeFilter.getTabAt(1)
        defaultTab?.select()

        binding.tabTimeFilter.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.filterByDay()   // Ngày
                    1 -> viewModel.filterByMonth() // Tháng
                    2 -> viewModel.filterByYear()  // Năm
                    3 -> showDateRangePicker()     // Tùy chỉnh
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun showDateRangePicker() {
        // Dùng MaterialDatePicker để chọn khoảng thời gian
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Chọn khoảng thời gian")
            .setTheme(R.style.CustomMaterialCalendarTheme)
            .build()

        dateRangePicker.show(childFragmentManager, "DATE_RANGE_PICKER")

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second
            viewModel.filterCustom(startDate, endDate)
        }
    }
}
