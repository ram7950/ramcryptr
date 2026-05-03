package com.rambo.ramcryptr

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build

object NotificationHelper {

    private const val CHANNEL_ID = "ramcryptr_channel"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "RAMcryptr",
                NotificationManager.IMPORTANCE_HIGH
            )
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    // 📌 FIXED
    fun showPersistent(context: Context) {
        val intent = Intent(context, MiniToolActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pending = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle("RAMcryptr")
            .setContentText("Tap to open mini tool")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentIntent(pending)
            .setOngoing(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1, notification)
    }

    // 🔐 TEXT MESSAGE
    fun showIncomingText(context: Context, sender: String, platform: String, text: String) {

        val intent = Intent(context, QuickDecodeActivity::class.java)
        intent.putExtra("text", text)
        intent.putExtra("sender", sender)
        intent.putExtra("platform", platform)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val decodePending = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val ignoreIntent = Intent(context, NotificationDismissReceiver::class.java)
        val ignorePending = PendingIntent.getBroadcast(
            context,
            200,
            ignoreIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle("🔐 Encrypted message")
            .setContentText("From: $sender ($platform)")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(decodePending)
            .addAction(android.R.drawable.ic_menu_view, "Decode", decodePending)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Ignore", ignorePending)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }

    // 📁 FILE MESSAGE
    fun showIncomingFile(context: Context, sender: String, platform: String) {

        val launchIntent = context.packageManager.getLaunchIntentForPackage(
            when (platform.lowercase()) {
                "whatsapp" -> "com.whatsapp"
                "telegram" -> "org.telegram.messenger"
                else -> context.packageName
            }
        )

        launchIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val pending = PendingIntent.getActivity(
            context,
            (System.currentTimeMillis() + 1).toInt(),
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle("📁 Encrypted file received")
            .setContentText("Tap to open $platform")
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
