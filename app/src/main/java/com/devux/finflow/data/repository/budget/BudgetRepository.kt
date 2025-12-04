package com.devux.finflow.data.repository.budget

import com.devux.finflow.data.BudgetEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun insertBudget(budget: BudgetEntity)
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>>
    suspend fun getBudgetByCategory(categoryId: Long, month: Int, year: Int): BudgetEntity?
    suspend fun deleteBudget(id: Long)
}