package com.devux.finflow.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devux.finflow.data.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    // Thêm hoặc Cập nhật ngân sách (Nếu trùng ID thì đè lên)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    // Lấy danh sách ngân sách của một tháng cụ thể
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>>

    // Lấy ngân sách của 1 danh mục cụ thể trong tháng (Để check nhanh lúc nhập giao dịch)
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year LIMIT 1")
    suspend fun getBudgetByCategory(categoryId: Long, month: Int, year: Int): BudgetEntity?

    // Xóa ngân sách
    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudget(id: Long)

}