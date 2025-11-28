package com.devux.finflow.utils

import android.util.Log
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.TransactionType
import java.util.regex.Pattern

object BankTransactionParser {

    private const val TAG = "BankTransactionParser"

    fun parse(notificationText: String): TransactionEntity? {
        try {
            val normalizedText = notificationText
                .replace("\n", " ")
                .replace("\\s+".toRegex(), " ")
                .trim()

            Log.d(TAG, "🔍 Parsing: $normalizedText")

            // Thử các mẫu phân tích cú pháp theo thứ tự
            return tryParsePattern1(normalizedText)
                ?: tryParsePattern2(normalizedText)
                ?: tryParsePattern3(normalizedText)
                ?: tryParseSimpleAmount(normalizedText)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Parse error: ${e.message}")
            return null
        }
    }

    private fun tryParsePattern1(text: String): TransactionEntity? {
        // Regex cho mẫu: TK xxx ... GD: -100,000VND ... ND: ...
        val regex = Pattern.compile(
            """TK\s*(\S+).*?GD:\s*([+-]?[0-9.,]+)\s*VND.*?SD:\s*[0-9.,]+\s*VND.*?(?:DEN|NOI DUNG|ND):\s*(.+?)(?:\||$)""",
            Pattern.CASE_INSENSITIVE or Pattern.DOTALL
        )

        val matcher = regex.matcher(text)
        if (!matcher.find()) {
            Log.d(TAG, "Pattern 1 not matched")
            return null
        }

        val accountId = matcher.group(1)?.trim() ?: return null
        val amountRaw = matcher.group(2)?.replace(",", "")?.replace(".", "")
        val description = matcher.group(3)?.trim()
        val amount = amountRaw?.toDoubleOrNull() ?: return null

        val isExpense = text.contains("GD: -") || text.contains("GD:-")
        val type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME

        Log.d(TAG, "✅ Pattern 1 matched")

        return TransactionEntity(
            amount = kotlin.math.abs(amount), // Luôn là số dương
            type = type,
            date = System.currentTimeMillis(),
            note = description,
            categoryId = null, // Không xác định được từ SMS
            accountId = accountId, // Tài khoản của giao dịch
            toAccountId = null // Không xác định được từ SMS
        )
    }

    private fun tryParsePattern2(text: String): TransactionEntity? {
        // Regex cho mẫu: GD: +500,000VND ... Tai: ... ND: ...
        val regex = Pattern.compile(
            """GD:\s*([+-]?[0-9.,]+)\s*VND.*?(?:Tai|DEN):\s*([^.]+).*?(?:ND|Noi dung):\s*(.+?)(?:\||$)""",
            Pattern.CASE_INSENSITIVE or Pattern.DOTALL
        )

        val matcher = regex.matcher(text)
        if (!matcher.find()) {
            Log.d(TAG, "Pattern 2 not matched")
            return null
        }

        val amountRaw = matcher.group(1)?.replace(",", "")?.replace(".", "")
        // accountId có thể là STK hoặc tên người gửi/nhận, không nhất quán
        val accountInfo = matcher.group(2)?.trim()
        val description = matcher.group(3)?.trim()

        val amount = amountRaw?.toDoubleOrNull() ?: return null
        val isExpense = text.contains("GD: -") || text.contains("GD:-")
        val type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME

        Log.d(TAG, "✅ Pattern 2 matched")

        return TransactionEntity(
            amount = kotlin.math.abs(amount),
            type = type,
            date = System.currentTimeMillis(),
            // Ghép thông tin tài khoản vào ghi chú vì không chắc chắn đó là accountId
            note = "Từ/Đến: $accountInfo. ND: $description",
            categoryId = null,
            // Để một giá trị tạm thời vì mẫu này không có số tài khoản rõ ràng
            accountId = "Chưa xác định",
            toAccountId = null
        )
    }

    private fun tryParsePattern3(text: String): TransactionEntity? {
        // Regex cho mẫu: TK xxx ... Giao dich: -20,000 ... So du: ...
        val regex = Pattern.compile(
            """(?:TK|Tai khoan)\s*(\S+).*?(?:Giao dich|GD)\s*([+-]?[0-9.,]+).*?(?:So du|SD)\s*([0-9.,]+)""",
            Pattern.CASE_INSENSITIVE
        )

        val matcher = regex.matcher(text)
        if (!matcher.find()) {
            Log.d(TAG, "Pattern 3 not matched")
            return null
        }

        val accountId = matcher.group(1)?.trim() ?: return null
        val amountRaw = matcher.group(2)?.replace(",", "")?.replace(".", "")
        val amount = amountRaw?.toDoubleOrNull() ?: return null

        val isExpense = text.contains("-") && amount > 0
        val type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME

        val description = findNoteInText(text) ?: text.take(100)

        Log.d(TAG, "✅ Pattern 3 matched")

        return TransactionEntity(
            amount = kotlin.math.abs(amount),
            type = type,
            date = System.currentTimeMillis(),
            note = description,
            categoryId = null,
            accountId = accountId,
            toAccountId = null
        )
    }

    private fun tryParseSimpleAmount(text: String): TransactionEntity? {
        val regex = Pattern.compile(
            """([+-]?[0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{2})?)\s*(?:VND|đ|d)""",
            Pattern.CASE_INSENSITIVE
        )

        val matcher = regex.matcher(text)
        if (!matcher.find()) {
            Log.d(TAG, "❌ No pattern matched")
            return null
        }

        val amountRaw = matcher.group(1)?.replace(",", "")?.replace(".", "")
        val amount = amountRaw?.toDoubleOrNull() ?: return null

        // Xác định loại giao dịch dựa trên từ khóa
        val isExpense = text.contains("chi", ignoreCase = true) ||
                text.contains("thanh toan", ignoreCase = true) ||
                text.contains("chuyen", ignoreCase = true) ||
                text.contains("-")
        val type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME

        val description = findNoteInText(text) ?: text.take(100)

        Log.d(TAG, "⚠️ Pattern 4 (simple) matched - may be inaccurate")

        return TransactionEntity(
            amount = kotlin.math.abs(amount),
            type = type,
            date = System.currentTimeMillis(),
            note = description,
            categoryId = null,
            accountId = "Chưa xác định", // Không thể xác định tài khoản từ mẫu này
            toAccountId = null
        )
    }

    // Hàm helper để tìm ghi chú trong văn bản, không thay đổi
    private fun findNoteInText(text: String): String? {
        val regex = Pattern.compile(
            """(?:DEN|NOI DUNG|ND|DIEN GIAI|GHI CHU):\s*(.+?)(?:\||$)""",
            Pattern.CASE_INSENSITIVE or Pattern.DOTALL
        )
        val matcher = regex.matcher(text)
        return if (matcher.find()) {
            matcher.group(1)?.trim()
        } else {
            null
        }
    }
}
