package com.devux.finflow.main.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.data.model.BudgetUiModel
import com.devux.finflow.databinding.ItemHomeBudgetBinding
import com.devux.finflow.utils.CurrencyUtils

class BudgetHomeAdapter : ListAdapter<BudgetUiModel, BudgetHomeAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemHomeBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BudgetUiModel) {
            val context = binding.root.context

            // 1. Tên
            binding.tvCategoryName.text = item.categoryName

            // 2. Progress Bar Tròn
            binding.progressBar.progress = item.progress
            // Đổi màu vòng tròn theo trạng thái (Xanh/Vàng/Đỏ)
            val color = ContextCompat.getColor(context, item.statusColor)
            binding.progressBar.setIndicatorColor(color)

            // 3. Số % ở giữa
            binding.tvPercent.text = "${item.progress}%"
            // (Tùy chọn) Đổi màu số % theo màu progress
            // binding.tvPercent.setTextColor(color)

            // 4. Số tiền còn lại
            val remainingStr = CurrencyUtils.formatCurrency(Math.abs(item.remainingAmount))
            if (item.remainingAmount >= 0) {
                binding.tvRemaining.text = "Còn $remainingStr"
                binding.tvRemaining.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.tab_indicator_text
                    )
                ) // Màu xám thường
            } else {
                binding.tvRemaining.text = "Lố $remainingStr"
                binding.tvRemaining.setTextColor(color) // Màu đỏ cảnh báo
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeBudgetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<BudgetUiModel>() {
        override fun areItemsTheSame(oldItem: BudgetUiModel, newItem: BudgetUiModel) =
            oldItem.categoryId == newItem.categoryId

        override fun areContentsTheSame(oldItem: BudgetUiModel, newItem: BudgetUiModel) =
            oldItem == newItem
    }
}