package com.devux.finflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey val id: Int = 1,
    val name: String, // Ví dụ: "Vietcombank" hoặc "Mục tiêu: Mua iPhone"
    val initialBalance: Double,
    val icon: String?,
    val currency: String = "VND",

    val type: AccountType = AccountType.REGULAR, // Phân loại tài khoản

    // Các trường này CHỈ dùng khi type = SAVINGS_GOAL
    val targetAmount: Double? = null, // Số tiền mục tiêu cần đạt
    val deadline: Long? = null // Ngày hết hạn mục tiêu (timestamp, tùy chọn)
)
