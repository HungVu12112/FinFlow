package com.devux.finflow.main.view.analytics

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.R
import com.devux.finflow.data.model.CategoryStatModel
import com.devux.finflow.databinding.ItemAnalyticsCategoryBinding
import com.devux.finflow.utils.CurrencyUtils

class AnalyticsAdapter :
    ListAdapter<CategoryStatModel, AnalyticsAdapter.ViewHolder>(DiffCallback()) {
    private var currentIconTint: Int = 0

    fun setIconTintColor(color: Int) {
        this.currentIconTint = color
        notifyDataSetChanged() // Load lại toàn bộ list để áp dụng màu mới
    }

    inner class ViewHolder(private val binding: ItemAnalyticsCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryStatModel) {
            val context = binding.root.context

            // 1. Hiển thị thông tin cơ bản
            binding.tvName.text = item.categoryName
            binding.tvAmount.text = CurrencyUtils.formatCurrency(item.amount)

            // Format phần trăm (1 chữ số thập phân)
            binding.tvPercent.text = String.format("%.1f%%", item.percentage)

            // 2. Xử lý Progress Bar
            // Set giá trị (0-100)
            binding.progressBar.progress = item.percentage.toInt()

            // Đổi màu Progress Bar theo màu của Category (đồng bộ với PieChart)
            binding.progressBar.progressTintList = ColorStateList.valueOf(item.color)

            // 3. Đổi màu thanh đánh dấu bên trái
            binding.viewColorIndicator.setBackgroundColor(item.color)

            // 4. Load Icon từ tên file (String) -> Drawable Resource ID
            // Ví dụ: "ic_food" -> R.drawable.ic_food
            val iconResId = context.resources.getIdentifier(
                item.icon,
                "drawable",
                context.packageName
            )

            if (iconResId != 0) {
                binding.imgIcon.setImageResource(iconResId)
            } else {
                // Icon mặc định nếu không tìm thấy
                binding.imgIcon.setImageResource(R.drawable.ic_launcher_foreground)
            }
            binding.imgIcon.setColorFilter(currentIconTint)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnalyticsCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryStatModel>() {
        override fun areItemsTheSame(
            oldItem: CategoryStatModel,
            newItem: CategoryStatModel
        ): Boolean {
            // So sánh dựa trên tên hoặc logic unique khác
            return oldItem.categoryName == newItem.categoryName
        }

        override fun areContentsTheSame(
            oldItem: CategoryStatModel,
            newItem: CategoryStatModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}