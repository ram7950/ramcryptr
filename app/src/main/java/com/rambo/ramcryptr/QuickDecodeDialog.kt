package com.rambo.ramcryptr

import android.app.AlertDialog
import android.content.Context
import android.content.ClipboardManager
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

object QuickDecodeDialog {

    fun show(context: Context) {

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

        // 🔹 PASTE BUTTON
        btnPaste.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
            input.setText(text)
        }

        // 🔹 DECODE BUTTON
        btnDecode.setOnClickListener {

            val text = input.text.toString()

            if (!text.startsWith("AES256::")) {
                output.text = "Paglu 😏 ye encrypted nahi hai"
                return@setOnClickListener
            }

            try {
                val decoded = TextCrypto.decrypt(text, "ramcryptr_secret")
                output.text = decoded
                input.setText("")   // clear for next use
            } catch (e: Exception) {
                output.text = "Decode failed"
            }
        }

        // 🔹 CLOSE BUTTON
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // 🔥 AUTO-FILL (smart UX)
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""

            if (text.startsWith("AES256::")) {
                input.setText(text)
            }
        } catch (_: Exception) {}

        dialog.show()
    }
}
