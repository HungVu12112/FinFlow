package com.devux.finflow.main.view.goals

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.databinding.ItemSavingGoalBinding
import com.devux.finflow.utils.CurrencyUtils
import java.util.concurrent.TimeUnit


class GoalAdapter(
    private val onGoalClick: (GoalEntity) -> Unit
) : ListAdapter<GoalEntity, GoalAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemSavingGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GoalEntity) {
            // 1. Set Tên
            binding.tvGoalName.text = item.name

            // 2. Set Số tiền
            val currentFormatted = CurrencyUtils.formatCurrency(item.currentAmount)
            val targetFormatted = CurrencyUtils.formatCurrency(item.targetAmount)

            binding.tvCurrentAmount.text = currentFormatted
            binding.tvTargetAmount.text = " / $targetFormatted"

            // 3. Tính toán Progress & Ngày còn lại (Logic đơn giản để ở Adapter cũng được)
            val progress =
                if (item.targetAmount > 0) ((item.currentAmount / item.targetAmount) * 100).toInt() else 0
            binding.progressBar.progress = progress
            binding.tvPercent.text = "$progress%"

            val diff = item.deadline - System.currentTimeMillis()
            val daysLeft = TimeUnit.MILLISECONDS.toDays(diff)

            if (daysLeft < 0) {
                binding.tvDaysLeft.text = "Đã hết hạn"
            } else {
                binding.tvDaysLeft.text = "$daysLeft ngày còn lại"
            }

            // 4. Set Màu Nền Card (Parse từ mã Hex)
            try {
                val color = Color.parseColor(item.color)
                binding.cardContainer.setCardBackgroundColor(color)
            } catch (e: Exception) {
                // Màu mặc định nếu mã màu lỗi
                binding.cardContainer.setCardBackgroundColor(Color.parseColor("#FF9800"))
            }

            // 5. Sự kiện click
            binding.root.setOnClickListener { onGoalClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavingGoalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<GoalEntity>() {
        override fun areItemsTheSame(oldItem: GoalEntity, newItem: GoalEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GoalEntity, newItem: GoalEntity) =
            oldItem == newItem
    }
}