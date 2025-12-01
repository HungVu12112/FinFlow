package com.devux.finflow.data.repository.goal

import com.devux.finflow.core.dao.GoalDao
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.data.GoalTransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override suspend fun insertGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    override suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }

    override suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
    }

    override fun getAllGoals(): Flow<List<GoalEntity>> {
        return goalDao.getAllGoals()
    }

    override suspend fun getGoalById(id: Long): GoalEntity? {
        return goalDao.getGoalById(id)
    }

    override suspend fun addGoalTransaction(transaction: GoalTransactionEntity) {
        goalDao.insertGoalTransaction(transaction)
    }

    override fun getGoalHistory(goalId: Long): Flow<List<GoalTransactionEntity>> {
        return goalDao.getGoalTransactions(goalId)
    }
}