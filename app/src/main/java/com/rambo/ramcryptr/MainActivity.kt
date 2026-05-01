package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

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
        encodeBtn.text = "Encode"

        val decodeBtn = Button(this)
        decodeBtn.text = "Decode"

        val vaultBtn = Button(this)
        vaultBtn.text = "Secured 🔐 Repository"

        // ===== NORMAL CLICK (text)
        encodeBtn.setOnClickListener {
            Toast.makeText(this, "Text encode flow", Toast.LENGTH_SHORT).show()
        }

        decodeBtn.setOnClickListener {
            Toast.makeText(this, "Text decode flow", Toast.LENGTH_SHORT).show()
        }

        // ===== LONG PRESS → FILE PICKER
        encodeBtn.setOnLongClickListener {
            openFilePicker(PICK_ENCODE)
            true
        }

        decodeBtn.setOnLongClickListener {
            openFilePicker(PICK_DECODE)
            true
        }

        // ===== VAULT BUTTON (open system file manager)
        vaultBtn.setOnClickListener {
            try {
                val baseDir = File(getExternalFilesDir(null), "Secured_Repository")

                val uri = Uri.parse(baseDir.absolutePath)

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "*/*")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(this, "Open manually from file manager", Toast.LENGTH_LONG).show()
            }
        }

        layout.addView(editText)
        layout.addView(encodeBtn)
        layout.addView(decodeBtn)
        layout.addView(vaultBtn)

        setContentView(layout)
    }

    private fun openFilePicker(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data?.data == null) return

        val uri = data.data!!

        if (requestCode == PICK_ENCODE) {
            // 🔥 SAME AS SHARE → ENCODE FLOW
            val intent = Intent(this, FileReceiveActivity::class.java)
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "*/*"
            startActivity(intent)
        }

        if (requestCode == PICK_DECODE) {
            // 🔥 SAME AS CLICK FILE → DECODE FLOW
            val intent = Intent(this, FileReceiveActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.data = uri
            startActivity(intent)
        }
    }
}
