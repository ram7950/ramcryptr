package com.rambo.ramcryptr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // 🔐 TODO: बाद में user-input / keystore में ले जाना
    private val MASTER_KEY = "ramcryptr_secret"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<EditText>(R.id.editText)
        val encodeBtn = findViewById<Button>(R.id.btnEncode)
        val decodeBtn = findViewById<Button>(R.id.btnDecode)
        val repoBtn = findViewById<Button>(R.id.btnRepo)

        // 🔐 ENCODE
        encodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val encrypted = TextCrypto.encrypt(text, MASTER_KEY)
                input.setText(encrypted)
                copyToClipboard(encrypted)
                Toast.makeText(this, "Encoded + Copied", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Encode failed", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔓 DECODE
        decodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!text.startsWith("AES256::")) {
                Toast.makeText(this, "Invalid encrypted text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val decrypted = TextCrypto.decrypt(text, MASTER_KEY)
                input.setText(decrypted)
                copyToClipboard(decrypted)
                Toast.makeText(this, "Decoded + Copied", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
            }
        }

        // 📁 REPOSITORY (SAF picker)
        repoBtn.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Cannot open folder", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 📋 Clipboard helper
    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ramcryptr", text)
        clipboard.setPrimaryClip(clip)
    }
}
