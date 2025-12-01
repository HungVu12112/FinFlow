package com.devux.finflow.data.repository.goal

import com.devux.finflow.data.GoalEntity
import com.devux.finflow.data.GoalTransactionEntity
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun insertGoal(goal: GoalEntity)
    suspend fun updateGoal(goal: GoalEntity)
    suspend fun deleteGoal(goal: GoalEntity)
    fun getAllGoals(): Flow<List<GoalEntity>>
    suspend fun getGoalById(id: Long): GoalEntity?
    suspend fun addGoalTransaction(transaction: GoalTransactionEntity)
    fun getGoalHistory(goalId: Long): Flow<List<GoalTransactionEntity>>
}