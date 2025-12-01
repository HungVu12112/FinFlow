package com.devux.finflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_transactions")
data class GoalTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val goalId: Long,           // Khóa ngoại: Thuộc về mục tiêu nào
    val amount: Double,         // Số tiền nạp/rút
    val type: Int,              // 1: Nạp (Deposit), -1: Rút (Withdraw)
    val date: Long = System.currentTimeMillis()
)