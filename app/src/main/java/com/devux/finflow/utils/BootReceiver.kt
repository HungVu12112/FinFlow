package com.devux.finflow.utils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted - restarting service")

            // Khởi động lại NotificationListenerService
            val serviceIntent = Intent("android.service.notification.NotificationListenerService")
            serviceIntent.setPackage(context.packageName)
            context.startService(serviceIntent)
        }
    }
}
