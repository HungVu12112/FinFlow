package com.devux.finflow.main.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.R
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.databinding.ItemCategoryGridBinding

class CategoryAdapter(
    private val onCategoryClick: (CategoryEntity?) -> Unit // Null nếu click vào nút "Chỉnh sửa"
) : ListAdapter<CategoryEntity, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition = RecyclerView.NO_POSITION

    // Định nghĩa 2 loại view: NORMAL (danh mục) và EDIT (nút chỉnh sửa)
    private val VIEW_TYPE_NORMAL = 1
    private val VIEW_TYPE_EDIT = 2

    private var currentIconTint: Int = 0

    fun setIconTintColor(color: Int) {
        this.currentIconTint = color
        notifyDataSetChanged() // Load lại toàn bộ list để áp dụng màu mới
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryEntity?, position: Int, isEditButton: Boolean) {
            val context = binding.root.context

            if (isEditButton) {
                // --- CẤU HÌNH NÚT CHỈNH SỬA ---
                binding.tvCategoryName.text = "Chỉnh sửa"
                binding.imgCategoryIcon.setImageResource(R.drawable.ic_edit)

                // Nút chỉnh sửa không bao giờ có trạng thái "selected"
                binding.categoryContainer.setBackgroundResource(R.drawable.bg_category_item)
                binding.imgCategoryIcon.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.gray
                    )
                )

                binding.root.setOnClickListener {
                    onCategoryClick(null) // Gửi null để báo hiệu là nút Edit
                }
            } else {
                // --- CẤU HÌNH ITEM DANH MỤC THƯỜNG ---
                item?.let { category ->
                    binding.tvCategoryName.text = category.name

                    // Lấy icon từ tên file
                    val iconResId = context.resources.getIdentifier(
                        category.icon,
                        "drawable",
                        context.packageName
                    )
                    if (iconResId != 0) {
                        binding.imgCategoryIcon.setImageResource(iconResId)
                    } else {
                        binding.imgCategoryIcon.setImageResource(R.drawable.ic_launcher_foreground)
                    }

                    // Xử lý chọn item
                    if (position == selectedPosition) {
                        if (currentIconTint == ContextCompat.getColor(context, R.color.orange_900)) {
                            binding.categoryContainer.setBackgroundResource(R.drawable.bg_category_item_selected)
                        } else {
                            binding.categoryContainer.setBackgroundResource(R.drawable.bg_category_item_selected_2)
                        }
                    } else {
                        binding.categoryContainer.setBackgroundResource(R.drawable.bg_category_item)
                    }

                    binding.root.setOnClickListener {
                        val previous = selectedPosition
                        selectedPosition = adapterPosition
                        notifyItemChanged(previous)
                        notifyItemChanged(selectedPosition)
                        onCategoryClick(category)
                    }
                    binding.imgCategoryIcon.setColorFilter(currentIconTint)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_EDIT) {
            // Nếu là item cuối cùng -> Bind nút Edit
            holder.bind(null, position, true)
        } else {
            // Nếu là item thường -> Bind dữ liệu danh mục
            holder.bind(getItem(position), position, false)
        }
    }

    // QUAN TRỌNG: Số lượng item = danh sách thật + 1 nút Edit
    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    // QUAN TRỌNG: Xác định loại view dựa vào vị trí
    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            VIEW_TYPE_EDIT // Item cuối cùng
        } else {
            VIEW_TYPE_NORMAL // Các item còn lại
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryEntity>() {
        override fun areItemsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity): Boolean =
            oldItem == newItem
    }

    fun clearSelection() {
        val previous = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        notifyItemChanged(previous)
    }
}