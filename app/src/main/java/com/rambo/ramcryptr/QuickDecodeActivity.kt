package com.rambo.ramcryptr

import android.app.NotificationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuickDecodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_decode)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)

        // 🧹 remove notification
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()

        val text = intent.getStringExtra("text")
        val sender = intent.getStringExtra("sender") ?: "Unknown"
        val platform = intent.getStringExtra("platform") ?: "App"

        // 🔒 SAFETY CHECK (main crash fix)
        if (text.isNullOrEmpty() || !text.startsWith("AES256::")) {
            tvTitle.text = "⚠ Invalid encrypted message"
            tvMessage.text = "Unable to decode"
            return
        }

        // 🔐 decode
        val decoded = try {
            TextCrypto.decrypt(text, "ramcryptr_secret")
        } catch (e: Exception) {
            "Decode failed"
        }

        tvTitle.text = "📥 Received encrypted message\nFrom: $sender ($platform)"
        tvMessage.text = decoded
    }
}
