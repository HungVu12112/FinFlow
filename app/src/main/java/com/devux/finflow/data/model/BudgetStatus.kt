package com.devux.finflow.data.model

data class BudgetStatus(
    val categoryId: Long,
    val categoryName: String, // Tên danh mục (lấy từ bảng Category)
    val categoryIcon: String, // Icon danh mục
    val budgetAmount: Double, // Số tiền ngân sách đã đặt
    val spentAmount: Double,  // Số tiền thực tế đã chi
    val remainingAmount: Double, // Số tiền còn lại (budget - spent)
    val progress: Int // Phần trăm đã chi (0 - 100%)
)
