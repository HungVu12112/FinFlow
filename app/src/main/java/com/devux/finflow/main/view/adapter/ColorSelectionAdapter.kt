package com.devux.finflow.main.view.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.databinding.ItemColorSelectionGridBinding

class ColorSelectionAdapter(
    private val colorResIds: List<Int>, // Nhận danh sách ID (R.color.xxx)
    private val onColorClick: (Int) -> Unit // Trả về ID khi click
) : RecyclerView.Adapter<ColorSelectionAdapter.ViewHolder>() {

    private var selectedColorRes: Int = 0

    fun setSelectedColor(colorRes: Int) {
        val oldPosition = colorResIds.indexOf(selectedColorRes)
        val newPosition = colorResIds.indexOf(colorRes)

        selectedColorRes = colorRes

        if (oldPosition != -1) notifyItemChanged(oldPosition)
        if (newPosition != -1) notifyItemChanged(newPosition)
    }

    inner class ViewHolder(private val binding: ItemColorSelectionGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(colorResId: Int) {
            // Lấy mã màu thực tế từ ID
            val colorInt = ContextCompat.getColor(binding.root.context, colorResId)

            // Tô màu
            binding.viewColor.backgroundTintList = ColorStateList.valueOf(colorInt)

            // Hiển thị viền nếu được chọn
            binding.viewColor.isSelected = (colorResId == selectedColorRes)

            binding.root.setOnClickListener {
                onColorClick(colorResId)
                setSelectedColor(colorResId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemColorSelectionGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(colorResIds[position])
    }

    override fun getItemCount() = colorResIds.size
}