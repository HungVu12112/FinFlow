package com.devux.finflow.main.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devux.finflow.R
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.TransactionType
import com.devux.finflow.databinding.ItemTransactionBinding
import com.devux.finflow.utils.CurrencyUtils

class TransactionAdapter :
    ListAdapter<TransactionEntity, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {
    private var categoryMap: Map<String, CategoryEntity> = emptyMap()
    fun setCategories(categories: List<CategoryEntity>) {
        this.categoryMap = categories.associateBy { it.id.toString() }
        notifyDataSetChanged()
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionEntity) = with(binding) {
            val context = root.context
            val colorRed = ContextCompat.getColor(context, R.color.orange_900)
            val colorGreen = ContextCompat.getColor(context, R.color.green_900)
            // 1. Lấy Category từ Map trước
            val category = categoryMap[item.categoryId]
            val categoryName = category?.name ?: "Chưa phân loại"

            // 2. Xác định Màu sắc và Dấu (+/-) dựa trên Type
            val (textColorRes, prefix) = when (item.type) {
                TransactionType.EXPENSE -> {
                    binding.imgTransactionIcon.setColorFilter(colorRed)
                    Pair(R.color.red_400, "- ")
                }

                TransactionType.INCOME -> {
                    binding.imgTransactionIcon.setColorFilter(colorGreen)
                    Pair(R.color.green_400, "+ ")
                }

                TransactionType.TRANSFER -> Pair(R.color.blue_400, "")
            }

            // 3. Xử lý ICON hiển thị
            if (item.type == TransactionType.TRANSFER) {
                // Nếu là Chuyển khoản: Dùng icon cố định
                imgTransactionIcon.setImageResource(R.drawable.ic_transaction)
            } else {
                // Nếu là Thu/Chi: Dùng icon của Danh mục
                val iconName = category?.icon

                // Tìm ID ảnh từ tên file (String -> Int)
                val resId = if (!iconName.isNullOrEmpty()) {
                    context.resources.getIdentifier(iconName, "drawable", context.packageName)
                } else 0

                if (resId != 0) {
                    // Tìm thấy icon danh mục -> Hiển thị
                    imgTransactionIcon.setImageResource(resId)
                } else {
                    // Không tìm thấy (hoặc category null) -> Hiển thị icon mặc định
                    val fallbackIcon = if (item.type == TransactionType.EXPENSE)
                        R.drawable.ic_baseline_arrow_upward_24
                    else
                        R.drawable.ic_baseline_arrow_downward_24

                    imgTransactionIcon.setImageResource(fallbackIcon)
                }
            }

            // 4. Gán dữ liệu Text
            tvTransactionSubtitle.text = categoryName
            tvTransactionAmount.setTextColor(ContextCompat.getColor(context, textColorRes))
            tvTransactionAmount.text = "$prefix${CurrencyUtils.formatCurrency(item.amount)} đ"
            tvTransactionTitle.text = item.note ?: "Không có ghi chú nào"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(
            oldItem: TransactionEntity,
            newItem: TransactionEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransactionEntity,
            newItem: TransactionEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}