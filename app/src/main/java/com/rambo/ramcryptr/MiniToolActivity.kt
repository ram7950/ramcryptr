package com.rambo.ramcryptr

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MiniToolActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mini_tool)

        val input = findViewById<EditText>(R.id.etInput)
        val output = findViewById<TextView>(R.id.tvOutput)
        val btnEncode = findViewById<Button>(R.id.btnEncode)
        val btnDecode = findViewById<Button>(R.id.btnDecode)

        // 🔥 Clipboard auto paste
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            if (!text.isNullOrEmpty()) {
                input.setText(text)
            }
        }

        // 🔐 Encode
        btnEncode.setOnClickListener {
            val text = input.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val encoded = TextCrypto.encrypt(text, "ramcryptr_secret")
            output.text = encoded
        }

        // 🔓 Decode
        btnDecode.setOnClickListener {
            val text = input.text.toString()

            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!text.startsWith("AES256::")) {
                output.text = "Paglu 😏 ye text encoded nahi hai"
                return@setOnClickListener
            }

            try {
                val decoded = TextCrypto.decrypt(text, "ramcryptr_secret")
                output.text = decoded
            } catch (e: Exception) {
                output.text = "Decode failed"
            }
        }
    }
}
