package com.rambo.ramcryptr

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val prefs = getSharedPreferences("ramcryptr_prefs", MODE_PRIVATE)

        val isEnabled = prefs.getBoolean("smart_decode", false)

        // 🔴 अगर switch OFF है → कुछ मत करो
        if (!isEnabled) return

        val extras = sbn.notification.extras

        val text = extras.getCharSequence("android.text")?.toString() ?: return

        // ❌ Ignore useless notifications
        if (text == "New message") return

        // 🔥 Detect encoded text
        if (text.startsWith("AES256::")) {

            val sender = extras.getString("android.title") ?: "Unknown"
            val app = sbn.packageName.substringAfterLast(".")

            NotificationHelper.showIncoming(this, sender, app)
        }
    }
}
