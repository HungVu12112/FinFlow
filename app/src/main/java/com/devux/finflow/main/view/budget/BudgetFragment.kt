package com.devux.finflow.main.view.budget

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.data.model.CategoryBudgetState
import com.devux.finflow.databinding.FragmentBudgetBinding
import com.devux.finflow.main.view.adapter.BudgetSetupAdapter
import com.devux.finflow.utils.CurrencyUtils
import com.devux.finflow.utils.NumberTextWatcher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BudgetFragment : BaseFragment<FragmentBudgetBinding>(FragmentBudgetBinding::inflate) {

    private val viewModel: BudgetViewModel by viewModels()
    private lateinit var adapter: BudgetSetupAdapter

    override fun initView() {
        setupRecyclerView()
        // Load danh sách ngay khi vào
        viewModel.loadBudgets()
    }

    override fun setAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setObserve() {
        viewModel.budgetList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    private fun setupRecyclerView() {
        adapter = BudgetSetupAdapter { item ->
            showSetLimitDialog(item)
        }

        binding.rvBudgets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BudgetFragment.adapter
            // Thêm đường kẻ ngang
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    // HIỂN THỊ DIALOG NHẬP SỐ TIỀN
    private fun showSetLimitDialog(item: CategoryBudgetState) {
        // Tận dụng lại layout dialog nhập tiền của GoalDetailFragment
        // Hoặc tạo layout mới nếu muốn tiêu đề rõ ràng hơn
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_enter_amount, null)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val tilAmount =
            dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilAmount)

        // Điền số cũ nếu có
        if (item.currentLimit > 0) {
            etAmount.setText(CurrencyUtils.formatCurrencyNoSymbol(item.currentLimit))
        }

        // Auto format 1.000.000
        etAmount.addTextChangedListener(NumberTextWatcher(etAmount))

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Ngân sách cho ${item.categoryName}")
            .setView(dialogView)
            .setPositiveButton("Lưu", null) // Override bên dưới
            .setNegativeButton("Hủy", null)
            // Thêm nút Xóa ngân sách nếu đã thiết lập
            .setNeutralButton(if (item.currentLimit > 0) "Xóa hạn mức" else null) { _, _ ->
                viewModel.saveBudget(item.categoryId, 0.0) // 0.0 coi như xóa
                Toast.makeText(requireContext(), "Đã xóa hạn mức", Toast.LENGTH_SHORT).show()
            }
            .create()

        dialog.show()

        // Xử lý nút Lưu (Validate)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val amountStr = etAmount.text.toString().replace(".", "")
            val amount = amountStr.toDoubleOrNull() ?: 0.0

            if (amount < 0) { // Cho phép nhập 0 để xóa, nhưng tốt nhất là dùng nút Xóa riêng
                tilAmount.error = "Số tiền không hợp lệ"
            } else {
                // Gọi ViewModel lưu vào DB
                viewModel.saveBudget(item.categoryId, amount)
                Toast.makeText(requireContext(), "Đã lưu ngân sách", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }
}