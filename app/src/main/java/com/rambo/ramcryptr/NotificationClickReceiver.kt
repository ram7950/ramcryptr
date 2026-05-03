package com.rambo.ramcryptr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationClickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val i = Intent(context, QuickDecodeActivity::class.java).apply {
            putExtras(intent.extras ?: return)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        context.startActivity(i)
    }
}
