package com.devux.finflow.core.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.data.GoalTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Long): GoalEntity?

    @Insert
    suspend fun insertGoalTransaction(transaction: GoalTransactionEntity)

    // Lấy lịch sử của 1 mục tiêu cụ thể, sắp xếp mới nhất lên đầu
    @Query("SELECT * FROM goal_transactions WHERE goalId = :goalId ORDER BY date DESC")
    fun getGoalTransactions(goalId: Long): Flow<List<GoalTransactionEntity>>
}