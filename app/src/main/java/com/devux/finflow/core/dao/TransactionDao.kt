package com.devux.finflow.core.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.TransactionType // <-- Quan trọng: Import Enum của bạn
import com.devux.finflow.data.model.CategoryExpenseTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // Thêm giao dịch
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    // Cập nhật giao dịch
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    // Xóa giao dịch
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // Lấy tất cả giao dịch (mới nhất trước)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>

    /**
     * Sửa đổi: Lọc theo `categoryId` kiểu `String`.
     */
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: String): Flow<List<TransactionEntity>>

    /**
     * Sửa đổi:
     * - Tính tổng trên trường `amount`.
     * - So sánh `type` với `TransactionType` thay vì `String`.
     * - Loại bỏ các giao dịch có type là TRANSFER.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND type != 'TRANSFER'")
    fun getTotalAmountByType(type: TransactionType): Flow<Double?>

    // Lấy theo khoảng ngày (timestamp millis)
    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<TransactionEntity>>

    // Thêm: Lấy giao dịch theo accountId
    @Query("SELECT * FROM transactions WHERE accountId = :accountId OR toAccountId = :accountId ORDER BY date DESC")
    fun getTransactionsByAccountId(accountId: String): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT categoryId, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate 
        GROUP BY categoryId
    """
    )
    fun getMonthlyExpenseByCategory(
        startDate: Long,
        endDate: Long
    ): Flow<List<CategoryExpenseTuple>>

    // 1. Lấy danh sách giao dịch trong khoảng thời gian
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    // 2. Tính tổng thu/chi trong khoảng thời gian (để cập nhật Card Income/Outcome)
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    fun getTotalAmountByDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>

    @Query(
        """
        SELECT categoryId, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate 
        GROUP BY categoryId
    """
    )
    fun getMonthlyExpenses(startDate: Long, endDate: Long): Flow<List<CategoryExpenseTuple>>
}

