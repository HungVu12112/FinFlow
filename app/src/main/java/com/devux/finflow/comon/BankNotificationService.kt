package com.devux.finflow.comon

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.repository.transaction.TransactionRepository
import com.devux.finflow.utils.BankTransactionParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BankNotificationService : NotificationListenerService() {

    private val TAG = "BankService"

    @Inject
    lateinit var repository: TransactionRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras
        // ✅ KIỂM TRA VISIBILITY - XỬ LÝ THÔNG BÁO NHẠY CẢM
        val visibility = notification.visibility
        Log.d(TAG, "📊 Notification visibility: $visibility (0=PRIVATE, 1=PUBLIC, -1=SECRET)")

        if (visibility == Notification.VISIBILITY_PRIVATE ||
            visibility == Notification.VISIBILITY_SECRET
        ) {

            // Thử lấy public version nếu có
            val publicNotification = notification.publicVersion
            if (publicNotification != null) {
                Log.d(TAG, "🔓 Using public version of notification")
                processNotificationContent(publicNotification.extras, packageName)
                return
            }

            Log.w(TAG, "⚠️ Notification is marked as sensitive/private, trying all methods...")
        }

        // ✅ ĐỌC TỪ NHIỀU NGUỒN
        processNotificationContent(extras, packageName)
    }

    private fun processNotificationContent(extras: android.os.Bundle, packageName: String) {
        // Lấy tất cả các trường có thể có
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: text
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString() ?: ""
        val infoText = extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString() ?: ""
        val summaryText = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString() ?: ""

        // Thử lấy từ text lines (cho notification dài)
        val textLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        val linesText = textLines?.joinToString("\n") { it?.toString() ?: "" } ?: ""

        // Kết hợp tất cả
        val fullText = buildString {
            if (title.isNotEmpty()) appendLine(title)
            if (bigText.isNotEmpty()) appendLine(bigText)
            if (subText.isNotEmpty()) appendLine(subText)
            if (infoText.isNotEmpty()) appendLine(infoText)
            if (summaryText.isNotEmpty()) appendLine(summaryText)
            if (linesText.isNotEmpty()) appendLine(linesText)
        }.trim()

        Log.d(TAG, "📩 New notification from: $packageName")
        Log.d(TAG, "📄 Full content length: ${fullText.length} chars")

        // ✅ KIỂM TRA NỘI DUNG BỊ ẨN
        if (fullText.isEmpty() ||
            fullText.contains("ẩn nội dung", ignoreCase = true) ||
            fullText.contains("hidden content", ignoreCase = true) ||
            fullText.contains("sensitive", ignoreCase = true)
        ) {

            Log.w(TAG, "❌ Could not read notification content - marked as sensitive")
            Log.w(
                TAG,
                "💡 User needs to: Settings → Notifications → Show all content on lock screen"
            )
            logAllExtras(extras)
            return
        }

        Log.d(TAG, "📝 Content: $fullText")

        // Chỉ xử lý nếu có từ khóa gợi ý thông báo ngân hàng
        if (isBankNotification(fullText)) {
            val transaction = BankTransactionParser.parse(fullText)
            if (transaction != null) {
                Log.d(TAG, "✅ Parsed transaction: $transaction")
                // 👉 Lưu transaction vào DB hoặc xử lý
                saveTransaction(transaction)
            } else {
                Log.w(TAG, "⚠️ Could not parse bank message")
                Log.w(TAG, "Raw text: $fullText")
            }
        } else {
            Log.d(TAG, "ℹ️ Not a bank notification, skipping...")
        }
    }

    private fun isBankNotification(text: String): Boolean {
        val keywords = listOf(
            "biến động số dư",
            "TK",
            "VND",
            "GD:",
            "SD:",
            "ND:",
            "chuyển khoản",
            "rút tiền",
            "thanh toán",
            "nạp tiền",
            "giao dịch"
        )
        return keywords.any { text.contains(it, ignoreCase = true) }
    }

    private fun logAllExtras(extras: android.os.Bundle) {
        Log.d(TAG, "========== ALL NOTIFICATION EXTRAS ==========")
        for (key in extras.keySet()) {
            try {
                val value = extras.get(key)
                Log.d(TAG, "  $key: $value (${value?.javaClass?.simpleName})")
            } catch (e: Exception) {
                Log.e(TAG, "  Error reading key $key: ${e.message}")
            }
        }
        Log.d(TAG, "============================================")
    }

    private fun saveTransaction(transaction: TransactionEntity) {
        serviceScope.launch {
            try {
                repository.insertTransaction(transaction)
                Log.d(TAG, "✅ Transaction saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to save transaction: ${e.message}")
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "🔔 Bank Notification Listener Connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "🚫 Bank Notification Listener Disconnected")

        // Có thể request reconnect
        requestRebind(android.content.ComponentName(this, BankNotificationService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}