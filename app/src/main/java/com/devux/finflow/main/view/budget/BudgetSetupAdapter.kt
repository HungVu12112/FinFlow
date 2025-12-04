package com.devux.finflow.main.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.R
import com.devux.finflow.data.model.CategoryBudgetState
import com.devux.finflow.databinding.ItemBudgetSetupBinding
import com.devux.finflow.utils.CurrencyUtils

class BudgetSetupAdapter(
    private val onItemClick: (CategoryBudgetState) -> Unit
) : ListAdapter<CategoryBudgetState, BudgetSetupAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemBudgetSetupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryBudgetState) {
            val context = binding.root.context

            // 1. Tên
            binding.tvCategoryName.text = item.categoryName

            // 2. Icon (Lấy từ tên file)
            val iconResId = context.resources.getIdentifier(item.categoryIcon, "drawable", context.packageName)
            if (iconResId != 0) {
                binding.imgCategoryIcon.setImageResource(iconResId)
            } else {
                binding.imgCategoryIcon.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // 3. Số tiền hạn mức
            if (item.currentLimit > 0) {
                binding.tvAmount.text = CurrencyUtils.formatCurrency(item.currentLimit) + " đ"
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.orange_900)) // Màu nổi bật
                binding.tvAmount.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                binding.tvAmount.text = "Chạm để thiết lập"
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.gray))
                binding.tvAmount.setTypeface(null, android.graphics.Typeface.NORMAL)
            }

            // 4. Click
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBudgetSetupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryBudgetState>() {
        override fun areItemsTheSame(oldItem: CategoryBudgetState, newItem: CategoryBudgetState) =
            oldItem.categoryId == newItem.categoryId

        override fun areContentsTheSame(oldItem: CategoryBudgetState, newItem: CategoryBudgetState) =
            oldItem == newItem
    }
}