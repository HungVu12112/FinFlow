package com.devux.finflow.data.repository.transaction

import com.devux.finflow.core.dao.TransactionDao
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return dao.insertTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return dao.getAllTransactions()
    }

    override fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>> {
        return dao.getTransactionsByType(type)
    }

    override fun getTransactionsByCategory(categoryId: String): Flow<List<TransactionEntity>> {
        return dao.getTransactionsByCategory(categoryId)
    }

    override fun getTotalAmountByType(type: TransactionType): Flow<Double?> {
        return dao.getTotalAmountByType(type)
    }

    override fun getTransactionsBetween(
        start: Long,
        end: Long
    ): Flow<List<TransactionEntity>> {
        return dao.getTransactionsBetween(start, end)
    }

    override fun getTransactionsByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>> {
        return dao.getTransactionsByDateRange(startDate, endDate)
    }

    override fun getTotalAmountByDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> {
        return dao.getTotalAmountByDateRange(type, startDate, endDate)
    }
}
