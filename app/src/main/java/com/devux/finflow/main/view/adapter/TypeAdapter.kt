package com.devux.finflow.main.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.devux.finflow.databinding.LayoutCategoryItemBinding
import com.tta.futurenest.view.base.BaseAdapter
import com.tta.futurenest.view.base.BaseViewHolder

class TypeAdapter(
    private val onClick: (String) -> Unit
) : BaseAdapter<String, LayoutCategoryItemBinding, TypeAdapter.TypeViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val binding = LayoutCategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TypeViewHolder(binding, onClick)
    }

    inner class TypeViewHolder(
        private val binding: LayoutCategoryItemBinding,
        click: (String) -> Unit
    ) : BaseViewHolder<String, LayoutCategoryItemBinding>(binding, click) {

        override fun onBindData(data: String) {
            super.onBindData(data)
            binding.tvName.text = data
        }
    }
}
