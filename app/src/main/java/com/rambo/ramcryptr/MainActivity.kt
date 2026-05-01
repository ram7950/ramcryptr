package com.rambo.ramcryptr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

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

            val result = TextCrypto.encrypt(text, "ramcryptr_secret")
            input.setText(result)
        }

        // 🔓 TEXT DECODE
        decodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = TextCrypto.decrypt(text, "ramcryptr_secret")
            input.setText(result)
        }

        // 📂 LONG PRESS ENCODE → same as share
        encodeBtn.setOnLongClickListener {
            pickFile(PICK_ENCODE_FILE)
            true
        }

        // 📂 LONG PRESS DECODE → same as open
        decodeBtn.setOnLongClickListener {
            pickFile(PICK_DECODE_FILE)
            true
        }
    }

    private fun pickFile(code: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK || data == null) return

        val uri: Uri = data.data ?: return

        when (requestCode) {

            // 🔐 ENCODE → simulate share
            PICK_ENCODE_FILE -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.setPackage(packageName)
                startActivity(intent)
            }

            // 🔓 DECODE → simulate open file
            PICK_DECODE_FILE -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "*/*")
                intent.setPackage(packageName)
                startActivity(intent)
            }
        }
    }
}
