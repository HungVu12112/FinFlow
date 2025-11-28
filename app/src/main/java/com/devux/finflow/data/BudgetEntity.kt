package com.devux.finflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val categoryId: Long,
    val month: Int,
    val year: Int,
    val amount: Double
)
