package com.devux.finflow.data.model

data class BudgetUiModel(
    val categoryId: Long,
    val categoryName: String,
    val amountLimit: Double,    // Hạn mức
    val amountSpent: Double,    // Đã tiêu
    val remainingAmount: Double,// Còn lại
    val progress: Int,          // % đã tiêu (0-100)
    val statusColor: Int,       // Màu cảnh báo (Xanh/Vàng/Đỏ)
    val statusText: String      // "An toàn", "Cảnh báo", "Vượt mức"
)