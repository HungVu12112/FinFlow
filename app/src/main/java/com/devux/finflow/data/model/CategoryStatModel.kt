package com.devux.finflow.data.model

data class CategoryStatModel(
    val categoryName: String,
    val icon: String,
    val amount: Double,
    val percentage: Float, // % chiếm dụng (0-100)
    val color: Int // Màu sắc hiển thị trên biểu đồ
)