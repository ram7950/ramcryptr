package com.rambo.ramcryptr

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val PICK_ENCODE = 1001
    private val PICK_DECODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 60, 40, 40)

        val editText = EditText(this)
        editText.hint = "Enter text here"

        val encodeBtn = Button(this)
        encodeBtn.text = "ENCODE"

        val decodeBtn = Button(this)
        decodeBtn.text = "DECODE"

        val vaultBtn = Button(this)
        vaultBtn.text = "SECURED 🔐 REPOSITORY"

        // ===== TEXT ENCODE (basic restore)
        encodeBtn.setOnClickListener {
            val input = editText.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encoded = input.reversed() // simple logic (replace later with real)
            editText.setText(encoded)

            copyToClipboard(encoded)
            Toast.makeText(this, "Encoded + Copied", Toast.LENGTH_SHORT).show()
        }

        // ===== TEXT DECODE
        decodeBtn.setOnClickListener {
            val input = editText.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val decoded = input.reversed()
            editText.setText(decoded)

            copyToClipboard(decoded)
            Toast.makeText(this, "Decoded + Copied", Toast.LENGTH_SHORT).show()
        }

        // ===== LONG PRESS → FILE PICKER
        encodeBtn.setOnLongClickListener {
            openPicker(PICK_ENCODE)
            true
        }

        decodeBtn.setOnLongClickListener {
            openPicker(PICK_DECODE)
            true
        }

        // ===== VAULT BUTTON (FIXED)
        vaultBtn.setOnClickListener {
            Toast.makeText(
                this,
                "Open manually:\nAndroid/data/com.rambo.ramcryptr/files/Secured_Repository",
                Toast.LENGTH_LONG
            ).show()
        }

        layout.addView(editText)
        layout.addView(encodeBtn)
        layout.addView(decodeBtn)
        layout.addView(vaultBtn)

        setContentView(layout)
    }

    private fun openPicker(code: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(Intent.createChooser(intent, "Select File"), code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data?.data == null) return

        val uri = data.data!!

        if (requestCode == PICK_ENCODE) {
            val intent = Intent(this, FileReceiveActivity::class.java)
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "*/*"
            startActivity(intent)
        }

        if (requestCode == PICK_DECODE) {
            val intent = Intent(this, FileReceiveActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("text", text))
    }
}
