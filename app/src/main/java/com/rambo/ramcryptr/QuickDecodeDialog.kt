package com.rambo.ramcryptr

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.widget.*

object QuickDecodeDialog {

    fun showWithPrefill(context: Context, prefill: String) {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.quick_decode_dialog, null)

        val input = view.findViewById<EditText>(R.id.etInput)
        val output = view.findViewById<TextView>(R.id.tvOutput)
        val btnPaste = view.findViewById<Button>(R.id.btnPaste)
        val btnDecode = view.findViewById<Button>(R.id.btnDecode)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()

        // 🔥 PREFILL
        if (prefill.startsWith("AES256::")) {
            input.setText(prefill)
        }

        // 🔹 PASTE
        btnPaste.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
            input.setText(text)
        }

        // 🔹 DECODE
        btnDecode.setOnClickListener {

            val text = input.text.toString()

            if (!text.startsWith("AES256::")) {
                output.text = "Paglu 😏 ye encrypted nahi hai"
                return@setOnClickListener
            }

            try {
                val decoded = TextCrypto.decrypt(text, "ramcryptr_secret")
                output.text = decoded
                input.setText("")
            } catch (e: Exception) {
                output.text = "Decode failed"
            }
        }

        // 🔹 CLOSE
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
