package com.devux.finflow.data.model

data class CategoryBudgetState(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val currentLimit: Double, // 0 nếu chưa thiết lập
    val budgetId: Long = 0L // ID của bản ghi Budget (nếu có để update)
)