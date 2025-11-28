package com.devux.finflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val amount: Double, // Luôn là số dương
    val type: TransactionType,
    val date: Long,
    val note: String?,

    // Khóa ngoại
    val categoryId: String?, // Có thể null nếu là TRANSFER
    val accountId: String, // Tài khoản nguồn (nếu EXPENSE/TRANSFER)
    // Tài khoản đích (nếu INCOME)

    // --- (PHẦN MỚI) ---
    // Chỉ dùng khi type = TRANSFER
    val toAccountId: String? = null // Tài khoản đích của lệnh chuyển
)