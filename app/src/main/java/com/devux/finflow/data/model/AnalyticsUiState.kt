package com.devux.finflow.data.model

data class AnalyticsUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val expenseList: List<CategoryStatModel> = emptyList()
)