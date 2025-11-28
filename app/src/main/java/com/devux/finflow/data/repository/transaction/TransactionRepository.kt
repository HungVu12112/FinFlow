package com.devux.finflow.data.repository.transaction

import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insertTransaction(transaction: TransactionEntity): Long
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(transaction: TransactionEntity)

    fun getAllTransactions(): Flow<List<TransactionEntity>>
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>
    fun getTransactionsByCategory(categoryId: String): Flow<List<TransactionEntity>>
    fun getTotalAmountByType(type: TransactionType): Flow<Double?>
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<TransactionEntity>>
    //Lấy danh sách giao dịch theo khoảng thời gian
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    //Lấy tổng tiền (Thu hoặc Chi) theo khoảng thời gian
    fun getTotalAmountByDateRange(type: TransactionType, startDate: Long, endDate: Long): Flow<Double?>
}
