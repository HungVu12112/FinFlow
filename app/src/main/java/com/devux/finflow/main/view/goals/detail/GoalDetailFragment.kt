package com.devux.finflow.main.view.goals.detail

import android.graphics.Color
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.comon.TransactionResult
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.databinding.FragmentGoalDetailBinding
import com.devux.finflow.utils.CurrencyUtils
import com.devux.finflow.utils.NumberTextWatcher
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class GoalDetailFragment :
    BaseFragment<FragmentGoalDetailBinding>(FragmentGoalDetailBinding::inflate) {

    private val viewModel: GoalDetailViewModel by viewModels()

    // Sử dụng Safe Args để nhận dữ liệu (GoalEntity hoặc ID)
    // Bạn cần thêm argument "goalId" (Long) hoặc "goal" (GoalEntity) trong nav_graph
    private val args: GoalDetailFragmentArgs by navArgs()

    private var currentGoal: GoalEntity? = null
    private val historyAdapter = GoalHistoryAdapter()

    override fun initView() {
        // Nếu bạn truyền object GoalEntity
        if (args.goal != null) {
            currentGoal = args.goal
            bindData(currentGoal!!)
            viewModel.setInitialGoal(args.goal!!)
        }
        // Nếu bạn truyền ID (khuyên dùng ID để load dữ liệu mới nhất từ DB)
        args.goal?.let { viewModel.loadGoal(it.id) }
        binding.rvHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false // Để cuộn mượt trong ScrollView
        }
    }

    override fun setAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // Kiểm tra null để an toàn
                    if (currentGoal != null) {
                        // Truyền currentGoal sang màn hình Add để nó tự điền dữ liệu cũ vào
                        val action = GoalDetailFragmentDirections
                            .actionGoalDetailFragmentToAddGoalFragment(goal = currentGoal)
                        findNavController().navigate(action)
                    }
                    true
                }

                R.id.action_delete -> {
                    showDeleteConfirmDialog()
                    true
                }

                else -> false
            }
        }

        binding.btnDeposit.setOnClickListener {
            showAdjustAmountDialog(isDeposit = true)
        }

        binding.btnWithdraw.setOnClickListener {
            showAdjustAmountDialog(isDeposit = false)
        }
    }

    override fun setObserve() {
        // Nếu bạn load bằng ID từ ViewModel
        viewModel.goal.observe(viewLifecycleOwner) { goal ->
            currentGoal = goal
            bindData(goal)
        }
        viewModel.history.observe(viewLifecycleOwner) { list ->
            historyAdapter.submitList(list)
        }
    }

    private fun bindData(goal: GoalEntity) {
        binding.toolbar.title = goal.name

        // 1. Màu sắc
        try {
            val color = Color.parseColor(goal.color)
            binding.cardGoalInfo.setCardBackgroundColor(color)
            // Có thể chỉnh màu StatusBar hoặc Toolbar theo màu thẻ nếu muốn
        } catch (e: Exception) {
            binding.cardGoalInfo.setCardBackgroundColor(Color.parseColor("#FF9800"))
        }

        // 2. Số tiền & Tiến độ
        binding.tvCurrentAmount.text = CurrencyUtils.formatCurrency(goal.currentAmount) + " đ"
        binding.tvTargetAmount.text = CurrencyUtils.formatCurrency(goal.targetAmount) + " đ"

        val progress =
            if (goal.targetAmount > 0) ((goal.currentAmount / goal.targetAmount) * 100).toInt() else 0
        binding.tvPercent.text = "$progress%"
        binding.progressBar.progress = progress

        // 3. Thời gian
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val deadlineDate = Date(goal.deadline)
        val today = Date()
        val diff = goal.deadline - System.currentTimeMillis()
        val daysLeft = if (diff > 0) diff / (1000 * 60 * 60 * 24) else 0

        binding.tvDeadline.text =
            "Hạn chót: ${dateFormat.format(deadlineDate)} (Còn $daysLeft ngày)"

        // 4. Gợi ýz
        binding.tvSuggestion.text = viewModel.getSmartSuggestion(goal)
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa mục tiêu?")
            .setMessage("Bạn có chắc muốn xóa kế hoạch này không?")
            .setPositiveButton("Xóa") { _, _ ->
                currentGoal?.let { viewModel.deleteGoal(it) }
                findNavController().popBackStack()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showAdjustAmountDialog(isDeposit: Boolean) {
        val title = if (isDeposit) "Nạp thêm tiền" else "Rút bớt tiền"
        val positiveBtn = if (isDeposit) "Nạp tiền" else "Rút tiền"

        val dialogView = layoutInflater.inflate(R.layout.dialog_enter_amount, null)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val tilAmount =
            dialogView.findViewById<TextInputLayout>(R.id.tilAmount) // Cần ID của TextInputLayout trong XML

        etAmount.addTextChangedListener(NumberTextWatcher(etAmount))

        // Tạo dialog nhưng chưa show ngay để override nút Positive
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(positiveBtn, null) // Set null ở đây để override bên dưới
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()

        // Override nút Positive để ngăn Dialog đóng nếu có lỗi
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val amountStr = etAmount.text.toString()

            // Gọi ViewModel kiểm tra
            val result = viewModel.validateAndAdjustAmount(amountStr, isDeposit)

            when (result) {
                TransactionResult.SUCCESS -> {
                    Toast.makeText(requireContext(), "Giao dịch thành công!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss() // Chỉ đóng khi thành công
                }

                TransactionResult.EMPTY_INPUT -> {
                    tilAmount.error = "Vui lòng nhập số tiền"
                }

                TransactionResult.INVALID_AMOUNT -> {
                    tilAmount.error = "Số tiền phải lớn hơn 0"
                }

                TransactionResult.INSUFFICIENT_FUNDS -> {
                    // Hiển thị lỗi rõ ràng
                    Toast.makeText(
                        requireContext(),
                        "Số dư hiện tại không đủ để rút!",
                        Toast.LENGTH_LONG
                    ).show()
                    tilAmount.error = "Vượt quá số dư"
                }
            }
        }
    }
}