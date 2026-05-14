package com.rambo.ramcryptr

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MiniToolActivity : AppCompatActivity() {

    private val recHandler = Handler(Looper.getMainLooper())
    private var recSeconds = 0

    private val PICK_ENCODE_FILE = 501
    private val PICK_DECODE_FILE = 502

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mini_tool)

        val input = findViewById<EditText>(R.id.etInput)
        val output = findViewById<TextView>(R.id.tvOutput)
        val btnEncode = findViewById<Button>(R.id.btnEncode)
        val btnDecode = findViewById<Button>(R.id.btnDecode)
        val btnVoice = findViewById<Button>(R.id.btnVoice)
        val tvRecordingStatus =
            findViewById<TextView>(R.id.tvRecordingStatus)

        // Clipboard auto paste
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
        if (!text.isNullOrEmpty()) input.setText(text)

        // Encode click
        btnEncode.setOnClickListener {
            val t = input.text.toString()
            if (t.isEmpty()) return@setOnClickListener
            output.text = TextCrypto.encrypt(t, CryptoMasterProvider.getMaster(this))
        }

        // Decode click
        btnDecode.setOnClickListener {
            val t = input.text.toString()
            if (!t.startsWith("AES256::")) {
                output.text = "Paglu 😏 ye text encoded nahi hai"
                return@setOnClickListener
            }
            try {
                output.text = TextCrypto.decrypt(t, CryptoMasterProvider.getMaster(this))
            } catch (e: Exception) {
                output.text = "Decode failed"
            }
        }

        // 🔥 LONG PRESS FIX
        btnEncode.setOnLongClickListener {
            pickFile(PICK_ENCODE_FILE)
            true
        }

        btnDecode.setOnLongClickListener {
            pickFile(PICK_DECODE_FILE)
            true
        }


        btnVoice.setOnTouchListener { _, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    btnVoice.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#00FF66")
                        )
                    )

                    tvRecordingStatus.alpha = 1f

                    recSeconds = 0

                    recHandler.post(object : Runnable {

                        override fun run() {

                            val mins = recSeconds / 60
                            val secs = recSeconds % 60

                            tvRecordingStatus.text =
                                String.format(
                                    "🔴 REC %02d:%02d",
                                    mins,
                                    secs
                                )

                            recSeconds++

                            recHandler.postDelayed(
                                this,
                                1000
                            )
                        }
                    })

                    Toast.makeText(
                        this,
                        "Recording started",
                        Toast.LENGTH_SHORT
                    ).show()

                    true
                }

                MotionEvent.ACTION_UP -> {

                    btnVoice.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#001F3F")
                        )
                    )

                    tvRecordingStatus.alpha = 0f

                    recHandler.removeCallbacksAndMessages(null)

                    Toast.makeText(
                        this,
                        "Recording stopped",
                        Toast.LENGTH_SHORT
                    ).show()

                    true
                }

                else -> false
            }
        }
    }

    private fun pickFile(code: Int) {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "*/*"
        startActivityForResult(i, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) return

        val uri = data.data ?: return

        when (requestCode) {
            PICK_ENCODE_FILE -> {
                val i = Intent(this, FileEncryptActivity::class.java)
                i.data = uri
                startActivity(i)
            }
            PICK_DECODE_FILE -> {
                val i = Intent(this, FileDecryptActivity::class.java)
                i.data = uri
                startActivity(i)
            }
        }
    }
}
