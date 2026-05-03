package com.rambo.ramcryptr

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val extras = sbn.notification.extras ?: return

        val text = extras.getCharSequence("android.text")?.toString() ?: return
        val title = extras.getString("android.title") ?: "Unknown"
        val app = sbn.packageName.substringAfterLast(".")

        // ❌ Ignore useless notifications
        if (text.isBlank() || text == "New message") return

        // 📁 FILE detection FIRST (important)
        if (text.endsWith(".enc") || text.endsWith(".bin")) {
            NotificationHelper.showIncomingFile(this, title, app)
            return
        }

        // 🔐 TEXT detection
        if (text.startsWith("AES256::")) {
            NotificationHelper.showIncomingText(this, title, app, text)
            return
        }
    }
}
