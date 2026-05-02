package com.rambo.ramcryptr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val PICK_ENCODE_FILE = 201
    private val PICK_DECODE_FILE = 202

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔥 Overlay permission
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        // 🔥 Start bubble service
        startService(Intent(this, BubbleService::class.java))

        handleIncomingIntent(intent)

        val input = findViewById<EditText>(R.id.editText)
        val encodeBtn = findViewById<Button>(R.id.btnEncode)
        val decodeBtn = findViewById<Button>(R.id.btnDecode)

        // TEXT ENCODE
        encodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            input.setText(TextCrypto.encrypt(text, "ramcryptr_secret"))
        }

        // TEXT DECODE (CRASH SAFE)
        decodeBtn.setOnClickListener {

            val text = input.text.toString()

            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!text.startsWith("AES256::")) {
                Toast.makeText(this, "Paglu 😏 ye text encoded nahi hai", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                input.setText(TextCrypto.decrypt(text, "ramcryptr_secret"))
            } catch (e: Exception) {
                Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
            }
        }

        // FILE PICKER
        encodeBtn.setOnLongClickListener {
            pickFile(PICK_ENCODE_FILE)
            true
        }

        decodeBtn.setOnLongClickListener {
            pickFile(PICK_DECODE_FILE)
            true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        when (intent.action) {

            Intent.ACTION_SEND -> {
                val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                uri?.let { showEncodeDialog(it) }
            }

            Intent.ACTION_VIEW -> {
                val uri = intent.data
                uri?.let { showDecodeDialog(it) }
            }
        }
    }

    private fun showEncodeDialog(uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Encode File")
            .setMessage("Do you want to encode this file?")
            .setPositiveButton("Encode") { _, _ ->
                val i = Intent(this, FileEncryptActivity::class.java)
                i.setData(uri)
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(i)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDecodeDialog(uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Decode File")
            .setMessage("Encoded file detected. Decode it?")
            .setPositiveButton("Decode") { _, _ ->
                val i = Intent(this, FileDecryptActivity::class.java)
                i.setData(uri)
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(i)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun pickFile(code: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK || data == null) return

        val uri = data.data ?: return

        when (requestCode) {
            PICK_ENCODE_FILE -> showEncodeDialog(uri)
            PICK_DECODE_FILE -> showDecodeDialog(uri)
        }
    }
}
