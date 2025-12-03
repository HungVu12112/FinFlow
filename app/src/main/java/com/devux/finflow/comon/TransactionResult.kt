package com.devux.finflow.comon

enum class TransactionResult {
    SUCCESS,
    EMPTY_INPUT,
    INVALID_AMOUNT, // <= 0
    INSUFFICIENT_FUNDS // Rút quá số dư
}