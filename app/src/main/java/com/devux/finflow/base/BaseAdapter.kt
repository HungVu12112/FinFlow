package com.tta.futurenest.view.base

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import java.util.concurrent.Executors

abstract class BaseAdapter<T, VB : ViewBinding, VH : BaseViewHolder<T, VB>>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(
    AsyncDifferConfig.Builder(diffCallback)
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBindData(getItem(position))
    }
}
