package com.rambo.ramcryptr

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val extras = sbn.notification.extras ?: return

        val text = extras.getCharSequence("android.text")?.toString() ?: return
        val sender = extras.getString("android.title") ?: "Unknown"
        val app = sbn.packageName.substringAfterLast(".")

        if (text == "New message") return

        if (text.startsWith("AES256::")) {
            NotificationHelper.showIncoming(this, sender, app, text)
        }
    }
}
