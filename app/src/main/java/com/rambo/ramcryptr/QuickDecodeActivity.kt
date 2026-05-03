package com.rambo.ramcryptr

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuickDecodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_decode)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)

        val text = intent.getStringExtra("text") ?: ""
        val sender = intent.getStringExtra("sender") ?: "Unknown"
        val platform = intent.getStringExtra("platform") ?: "App"

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
