package com.devux.finflow.main.view.transaction

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.TransactionType
import com.devux.finflow.databinding.FragmentAddTransactionBinding
import com.devux.finflow.helper.LocaleManager
import com.devux.finflow.main.view.adapter.CategoryAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddTransactionFragment : BaseFragment<FragmentAddTransactionBinding>(
    FragmentAddTransactionBinding::inflate
) {
    private var currentTabPosition = 0
    private var selectedCategoryId: String? = null
    private var selectedDate: Long = System.currentTimeMillis()

    private val currentAccountId: String = "default_account_id_123"

    private val viewModel: AddTransactionViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    private var fullCategoryList: List<CategoryEntity> = emptyList()

    override fun initView() {
        initCategoryAdapter()
        binding.tvDate.text = LocaleManager.formatDate(selectedDate)
        viewModel.getAllCategories()
    }

    override fun setAction() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnConfirm.setOnClickListener {
            checkAndSaveTransaction()
        }
        binding.tvDate.setOnClickListener {
            showCalendar()
        }
        setupTabLayout()
    }

    override fun setObserve() {
        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            if (categories != null) {
                fullCategoryList = categories
                filterCategoriesByTab(currentTabPosition)
            }
        })
    }

    private fun setupTabLayout() {
        val colorRed = ContextCompat.getColor(requireContext(), R.color.orange_900)
        val colorGreen = ContextCompat.getColor(requireContext(), R.color.green_900)
        categoryAdapter.setIconTintColor(colorRed)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabPosition = tab?.position ?: 0
                val indicatorDrawable = binding.tabLayout.tabSelectedIndicator
                if (indicatorDrawable != null) {
                    val wrappedDrawable = DrawableCompat.wrap(indicatorDrawable).mutate()

                    if (currentTabPosition == 0) { // EXPENSE
                        DrawableCompat.setTint(wrappedDrawable, colorRed)
                        binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(colorRed)
                        binding.btnConfirm.text = "Nhập khoản Tiền chi"
                        binding.tvAmountLabel.text = "Tiền chi"
                        categoryAdapter.setIconTintColor(colorRed)
                    } else { // INCOME
                        DrawableCompat.setTint(wrappedDrawable, colorGreen)
                        binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(colorGreen)
                        binding.btnConfirm.text = "Nhập khoản Thu nhập"
                        binding.tvAmountLabel.text = "Tiền thu"
                        categoryAdapter.setIconTintColor(colorGreen)
                    }
                }

                filterCategoriesByTab(currentTabPosition)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // Hàm lọc danh mục dựa trên Tab (Chi tiêu hoặc Thu nhập)
    private fun filterCategoriesByTab(position: Int) {
        val targetType = if (position == 0) TransactionType.EXPENSE else TransactionType.INCOME

        // Lọc list từ fullCategoryList
        val filteredList = fullCategoryList.filter { it.type == targetType }

        // Update Adapter
        categoryAdapter.submitList(filteredList)

        // Reset lựa chọn cũ để tránh lỗi logic
        selectedCategoryId = null
        categoryAdapter.clearSelection()
    }

    private fun initCategoryAdapter() {
        categoryAdapter = CategoryAdapter { category ->
            if (category == null) {
                findNavController().navigate(R.id.action_addTransactionFragment_to_categoryManagementFragment)
            } else {
                // Lưu ID danh mục đã chọn (Convert Long sang String nếu Entity dùng String)
                selectedCategoryId = category.id.toString()
            }
        }
        binding.rvCategories.adapter = categoryAdapter
    }

    private fun showCalendar() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Chọn ngày")
            .setSelection(selectedDate) // Set ngày hiện tại đang chọn
            .build()

        datePicker.show(parentFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            // MaterialDatePicker trả về UTC, cần xử lý timezone nếu cần thiết
            selectedDate = selection
            binding.tvDate.text = LocaleManager.formatDate(selection)
        }
    }

    private fun checkAndSaveTransaction() {
        // 1. Lấy số tiền
        val amountText = binding.etAmount.text.toString()
        if (amountText.isEmpty()) {
            showToast("Vui lòng nhập số tiền")
            return
        }
        val money = amountText.toDouble()
        if (money <= 0) {
            showToast("Số tiền phải lớn hơn 0")
            return
        }

        // 2. Kiểm tra danh mục
        if (selectedCategoryId == null) {
            showToast("Vui lòng chọn danh mục")
            return
        }

        // 3. Lấy Ghi chú
        val note = binding.etNote.text.toString().trim()

        // 4. Xác định loại giao dịch từ Tab
        val type = if (currentTabPosition == 0) TransactionType.EXPENSE else TransactionType.INCOME

        // 5. Tạo Entity
        val transaction = TransactionEntity(
            amount = money,
            type = type,
            date = selectedDate,
            note = note.ifEmpty { null },
            categoryId = selectedCategoryId,
            accountId = currentAccountId, // ID tài khoản ví
            toAccountId = null // Null vì đây là thu/chi, không phải chuyển khoản
        )

        viewModel.addTransaction(transaction)
        showToast("Đã thêm giao dịch!")
        findNavController().popBackStack()
    }

    // Hàm tiện ích hiển thị Toast
    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT)
            .show()
    }
}