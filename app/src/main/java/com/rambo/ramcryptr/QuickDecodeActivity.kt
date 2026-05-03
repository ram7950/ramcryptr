package com.rambo.ramcryptr

import android.app.NotificationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuickDecodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_quick_decode)

            val tvTitle = findViewById<TextView>(R.id.tvTitle)
            val tvMessage = findViewById<TextView>(R.id.tvMessage)

            // 🧹 clear notifications
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.cancelAll()

            val text = intent.getStringExtra("text") ?: ""
            val sender = intent.getStringExtra("sender") ?: "Unknown"
            val platform = intent.getStringExtra("platform") ?: "App"

            // ❗ SAFE CHECK
            if (!text.startsWith("AES256::")) {
                tvTitle.text = "⚠ Invalid encrypted message"
                tvMessage.text = "No valid data received"
                return
            }

            val decoded = try {
                TextCrypto.decrypt(text, "ramcryptr_secret")
            } catch (e: Exception) {
                "❌ Decode failed"
            }

            tvTitle.text = "📥 Received encrypted message\nFrom: $sender ($platform)"
            tvMessage.text = decoded

        } catch (e: Exception) {
            e.printStackTrace()
            finish() // 🔥 crash रोकने के लिए
        }
    }
}
