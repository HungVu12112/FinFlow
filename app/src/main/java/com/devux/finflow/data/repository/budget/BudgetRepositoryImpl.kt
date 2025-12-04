package com.devux.finflow.data.repository.budget

import com.devux.finflow.core.dao.BudgetDao
import com.devux.finflow.data.BudgetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }

    override fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForMonth(month, year)
    }

    override suspend fun getBudgetByCategory(
        categoryId: Long,
        month: Int,
        year: Int
    ): BudgetEntity? {
        return budgetDao.getBudgetByCategory(categoryId, month, year)
    }

    override suspend fun deleteBudget(id: Long) {
        budgetDao.deleteBudget(id)
    }
}