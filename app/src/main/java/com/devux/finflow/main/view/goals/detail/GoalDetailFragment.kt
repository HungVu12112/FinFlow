package com.devux.finflow.main.view.goals.detail

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.databinding.DialogEnterAmountBinding
import com.devux.finflow.databinding.FragmentGoalDetailBinding
import com.devux.finflow.utils.CurrencyUtils
import com.devux.finflow.utils.NumberTextWatcher
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    override fun initView() {
        // Nếu bạn truyền object GoalEntity
        if (args.goal != null) {
            currentGoal = args.goal
            bindData(currentGoal!!)
        }
        // Nếu bạn truyền ID (khuyên dùng ID để load dữ liệu mới nhất từ DB)
        // viewModel.loadGoal(args.goalId)
    }

    override fun setAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // Mở màn hình AddGoalFragment ở chế độ Edit
                    // val action = GoalDetailFragmentDirections.actionToEdit(currentGoal)
                    // findNavController().navigate(action)
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
            // Bước tiếp theo: Hiện Dialog nạp tiền
            Toast.makeText(requireContext(), "Tính năng Nạp tiền (Bước 2)", Toast.LENGTH_SHORT)
                .show()
        }

        binding.btnWithdraw.setOnClickListener {
            Toast.makeText(requireContext(), "Tính năng Rút tiền", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setObserve() {
        // Nếu bạn load bằng ID từ ViewModel
        viewModel.goal.observe(viewLifecycleOwner) { goal ->
            currentGoal = goal
            bindData(goal)
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

        // 4. Gợi ý
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

        // Inflate layout cho Dialog
        val dialogBinding = DialogEnterAmountBinding.inflate(LayoutInflater.from(requireContext()))

        // Format tiền khi nhập
        dialogBinding.etAmount.addTextChangedListener(NumberTextWatcher(dialogBinding.etAmount))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setNegativeButton("Hủy", null)
            .setPositiveButton(positiveBtn) { _, _ ->
                // Xử lý khi bấm nút Xác nhận
                val amountStr = dialogBinding.etAmount.text.toString().replace(".", "")
                val amount = amountStr.toDoubleOrNull() ?: 0.0

                if (amount > 0) {
                    viewModel.adjustAmount(amount, isDeposit)
                    Toast.makeText(requireContext(), "Thành công!", Toast.LENGTH_SHORT).show()
                }
            }
            .create()

        dialog.show()
    }
}