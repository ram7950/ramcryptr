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

        // 🧹 notification remove
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()

        val mode = intent.getStringExtra("mode") ?: "text"
        val sender = intent.getStringExtra("sender") ?: "Unknown"
        val platform = intent.getStringExtra("platform") ?: "App"
        val text = intent.getStringExtra("text") ?: ""

        // 📁 FILE MODE
        if (mode == "file") {
            tvTitle.text = "📁 Encrypted file received\nFrom: $sender ($platform)"
            tvMessage.text = "Open chat and decode file manually"
            return
        }

        // 🔐 TEXT MODE
        tvTitle.text = "📥 Received encrypted message\nFrom: $sender ($platform)"

        if (text.startsWith("AES256::")) {
            try {
                val decoded = TextCrypto.decrypt(text, "ramcryptr_secret")
                tvMessage.text = decoded
            } catch (e: Exception) {
                tvMessage.text = "Decode failed"
            }
        } else {
            tvMessage.text = "Invalid encrypted message"
        }
    }
}
