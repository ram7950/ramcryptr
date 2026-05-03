package com.rambo.ramcryptr

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationService : NotificationListenerService() {

    private val TAG = "RAMcryptr_NLS"

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Listener connected ✔")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val prefs = getSharedPreferences("ramcryptr_prefs", MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("smart_decode", true) // default true for Phase-1 testing

        if (!isEnabled) {
            Log.d(TAG, "Smart decode OFF → skip")
            return
        }

        val extras = sbn.notification.extras ?: return

        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val title = extras.getString("android.title") ?: "Unknown"
        val app = sbn.packageName.substringAfterLast(".")

        Log.d(TAG, "Notif from=$app, title=$title, text=$text")

        // ❌ useless notifications ignore
        if (text.isBlank() || text == "New message") {
            Log.d(TAG, "Ignored: blank/new message")
            return
        }

        // 🔥 detect encoded
        if (text.startsWith("AES256::")) {
            Log.d(TAG, "Encoded detected ✔")

            NotificationHelper.showIncoming(this, title, app)
        } else {
            Log.d(TAG, "Not encoded")
        }
    }
}
