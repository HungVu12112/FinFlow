package com.devux.finflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,           // Tên mục tiêu (Mua xe, Mua nhà...)
    val targetAmount: Double,   // Số tiền cần đạt
    val currentAmount: Double,  // Số tiền hiện có
    val deadline: Long,         // Hạn chót (timestamp)
    val color: String           // Mã màu Hex (#FF0000)
) : Serializable