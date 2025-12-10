package com.devux.finflow.utils

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.devux.finflow.module.worker.ReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    // Hàm gọi chung để kích hoạt tất cả
    fun scheduleAllReminders(context: Context) {
        // 1. Lên lịch buổi sáng (08:00)
        scheduleWork(context, 7, 0, "MORNING", "FinFlowMorningWork")

        // 2. Lên lịch buổi tối (20:00)
        scheduleWork(context, 20, 0, "EVENING", "FinFlowEveningWork")
    }

    private fun scheduleWork(
        context: Context,
        hour: Int,
        minute: Int,
        type: String,
        uniqueWorkName: String
    ) {
        val workManager = WorkManager.getInstance(context)

        // Tạo dữ liệu đầu vào để Worker biết là Sáng hay Tối
        val data = Data.Builder()
            .putString("TYPE", type)
            .build()

        // Tính toán delay (Giống hệt code cũ)
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        val initialDelay = target.timeInMillis - now.timeInMillis

        // Tạo Request
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(data) // <--- QUAN TRỌNG: Truyền data vào đây
            .addTag("daily_reminder")
            .build()

        // Enqueue
        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName, // Tên định danh riêng biệt
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}