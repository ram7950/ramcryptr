package com.rambo.ramcryptr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val MASTER_KEY = "ramcryptr_secret"

    private val PICK_ENCODE_FILE = 201
    private val PICK_DECODE_FILE = 202

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<EditText>(R.id.editText)
        val encodeBtn = findViewById<Button>(R.id.btnEncode)
        val decodeBtn = findViewById<Button>(R.id.btnDecode)

        // 🔐 TEXT ENCODE
        encodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val encrypted = TextCrypto.encrypt(text, MASTER_KEY)
                input.setText(encrypted)
                Toast.makeText(this, "Encoded", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Encode failed", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔓 TEXT DECODE
        decodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val decrypted = TextCrypto.decrypt(text, MASTER_KEY)
                input.setText(decrypted)
                Toast.makeText(this, "Decoded", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
            }
        }

        // 📂 LONG PRESS → FILE PICKER (ENCODE)
        encodeBtn.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_ENCODE_FILE)
            true   // 🔥 MUST
        }

        // 📂 LONG PRESS → FILE PICKER (DECODE)
        decodeBtn.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_DECODE_FILE)
            true   // 🔥 MUST
        }
    }

    // 📂 FILE PICK RESULT
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK || data == null) return

        val uri = data.data ?: return

        when (requestCode) {

            PICK_ENCODE_FILE -> {
                Toast.makeText(this, "File selected for encode", Toast.LENGTH_SHORT).show()
                // 👉 यहाँ existing file encode logic call करना है
            }

            PICK_DECODE_FILE -> {
                Toast.makeText(this, "File selected for decode", Toast.LENGTH_SHORT).show()
                // 👉 यहाँ existing file decode logic call करना है
            }
        }
    }
}
