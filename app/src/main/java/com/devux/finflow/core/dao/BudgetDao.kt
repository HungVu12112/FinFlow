package com.devux.finflow.core.dao

import androidx.room.Dao
import androidx.room.Query
import com.devux.finflow.data.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    // Lấy danh sách ngân sách của tháng/năm cụ thể
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>>
}