package com.rambo.ramcryptr

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

        handleIntent(intent)

        val input = findViewById<EditText>(R.id.editText)
        val encodeBtn = findViewById<Button>(R.id.btnEncode)
        val decodeBtn = findViewById<Button>(R.id.btnDecode)

        encodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            input.setText(TextCrypto.encrypt(text, "ramcryptr_secret"))
        }

        decodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            input.setText(TextCrypto.decrypt(text, "ramcryptr_secret"))
        }

        encodeBtn.setOnLongClickListener { pickFile(PICK_ENCODE_FILE); true }
        decodeBtn.setOnLongClickListener { pickFile(PICK_DECODE_FILE); true }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
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
            .setPositiveButton("Encode") { _: DialogInterface, _: Int ->
                encodeFile(uri)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDecodeDialog(uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Decode File")
            .setMessage("Encoded file detected. Decode it?")
            .setPositiveButton("Decode") { _: DialogInterface, _: Int ->
                decodeFile(uri)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun encodeFile(uri: Uri) {
        try {
            FileProcessor.encodeFile(this, uri)
            Toast.makeText(this, "File encoded", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Encode failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decodeFile(uri: Uri) {
        try {
            FileProcessor.decodeFile(this, uri)
            Toast.makeText(this, "File decoded", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
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
        val uri = data.data ?: return

        when (requestCode) {
            PICK_ENCODE_FILE -> showEncodeDialog(uri)
            PICK_DECODE_FILE -> showDecodeDialog(uri)
        }
    }
}
