package com.devux.finflow.main.view.goals.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.R
import com.devux.finflow.data.GoalTransactionEntity
import com.devux.finflow.databinding.ItemGoalHistoryBinding
import com.devux.finflow.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoalHistoryAdapter : ListAdapter<GoalTransactionEntity, GoalHistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemGoalHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GoalTransactionEntity) {
            val context = binding.root.context

            // Format ngày giờ: 14:30 - 10/11/2025
            val dateFormatter = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
            binding.tvDate.text = dateFormatter.format(Date(item.date))

            val amountText = CurrencyUtils.formatCurrency(item.amount)

            if (item.type == 1) {
                // === NẠP TIỀN (DEPOSIT) ===
                binding.tvType.text = "Nạp tiền"
                binding.tvAmount.text = "+ $amountText đ"

                // Màu Xanh
                val colorGreen = ContextCompat.getColor(context, R.color.green_500)
                binding.tvAmount.setTextColor(colorGreen)
                binding.imgTypeIcon.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
                binding.imgTypeIcon.setColorFilter(colorGreen)

            } else {
                // === RÚT TIỀN (WITHDRAW) ===
                binding.tvType.text = "Rút tiền"
                binding.tvAmount.text = "- $amountText đ"

                // Màu Đỏ
                val colorRed = ContextCompat.getColor(context, R.color.red_500)
                binding.tvAmount.setTextColor(colorRed)
                binding.imgTypeIcon.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
                binding.imgTypeIcon.setColorFilter(colorRed)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<GoalTransactionEntity>() {
        override fun areItemsTheSame(oldItem: GoalTransactionEntity, newItem: GoalTransactionEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: GoalTransactionEntity, newItem: GoalTransactionEntity) = oldItem == newItem
    }
}