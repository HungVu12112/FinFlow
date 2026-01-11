package com.devux.finflow.main.view.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.R
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.databinding.ItemCategoryManageBinding // Binding từ item_category_manage.xml

class CategoryManageAdapter(
    private val onCategoryClick: (CategoryEntity) -> Unit
) : ListAdapter<CategoryEntity, CategoryManageAdapter.ViewHolder>(DiffCallback()) {
    private var currentIconTint: Int = 0

    fun setIconTintColor(color: Int) {
        this.currentIconTint = color
        notifyDataSetChanged() // Load lại toàn bộ list để áp dụng màu mới
    }
    inner class ViewHolder(private val binding: ItemCategoryManageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryEntity) {
            val context = binding.root.context

            binding.tvCategoryName.text = item.name

            // Lấy icon từ tên string
            val iconResId =
                context.resources.getIdentifier(item.icon, "drawable", context.packageName)
            if (iconResId != 0) {
                binding.imgCategoryIcon.setImageResource(iconResId)
            } else {
                binding.imgCategoryIcon.setImageResource(R.drawable.ic_launcher_foreground)
            }
            binding.imgDragHandle.setOnClickListener {
                onCategoryClick(item)
            }
            binding.imgCategoryIcon.setColorFilter(currentIconTint)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryManageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryEntity>() {
        override fun areItemsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity) =
            oldItem == newItem
    }
}