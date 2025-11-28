package com.devux.finflow.main.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.databinding.ItemIconSelectionBinding


class IconSelectionAdapter(
    private val icons: List<Int>, // <-- ĐỔI THÀNH LIST INT
    private val onIconClick: (Int) -> Unit // <-- Callback trả về Int
) : RecyclerView.Adapter<IconSelectionAdapter.ViewHolder>() {

    private var selectedIconRes: Int = 0

    fun setSelectedIcon(iconRes: Int) {
        val oldPosition = icons.indexOf(selectedIconRes)
        val newPosition = icons.indexOf(iconRes)

        selectedIconRes = iconRes

        if (oldPosition != -1) notifyItemChanged(oldPosition)
        if (newPosition != -1) notifyItemChanged(newPosition)
    }

    inner class ViewHolder(private val binding: ItemIconSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(iconRes: Int) {
            // Set ảnh trực tiếp từ ID, không cần getIdentifier nữa -> NHANH HƠN
            binding.imgIcon.setImageResource(iconRes)

            // Kiểm tra trạng thái chọn
            binding.categoryContainer.isSelected = (iconRes == selectedIconRes)

            binding.root.setOnClickListener {
                onIconClick(iconRes)
                setSelectedIcon(iconRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIconSelectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(icons[position])
    }

    override fun getItemCount() = icons.size
}