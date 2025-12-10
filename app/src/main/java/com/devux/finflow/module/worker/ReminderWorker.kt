package com.devux.finflow.module.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devux.finflow.R
import com.devux.finflow.utils.NotificationContent

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val type = inputData.getString("TYPE") ?: "EVENING"
        triggerNotification(type)
        return Result.success()
    }

    private fun triggerNotification(type: String) {
        val channelId = "daily_reminder_channel"
        val notificationId = if (type == "MORNING") 101 else 102
        // 1. Lấy nội dung ngẫu nhiên
        val (title, content) = if (type == "MORNING") {
            NotificationContent.getMorningMessage()
        } else {
            NotificationContent.getEveningMessage()
        }

        // 2. Tạo Deep Link: Bấm vào -> Mở thẳng màn AddTransactionFragment
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph) // ID của nav_graph.xml
            .setDestination(R.id.addTransactionFragment) // ID của màn hình thêm
            .createPendingIntent()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 3. Tạo Channel (Bắt buộc cho Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở ghi chép",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Nhắc nhở hàng ngày để quản lý chi tiêu"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 4. Xây dựng thông báo
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Thay bằng icon app của bạn (VD: ic_wallet)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Gán hành động khi click
            .setAutoCancel(true) // Bấm xong tự biến mất

        // 5. Hiển thị (Cần try-catch để tránh crash nếu mất quyền)
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}