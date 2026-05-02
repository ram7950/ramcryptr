package com.rambo.ramcryptr

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.content.ClipboardManager

class QuickDecodeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Intent से आया data
        val incoming = intent.getStringExtra("data") ?: ""

        // 🔹 Clipboard fallback
        val finalText = if (incoming.isNotEmpty()) {
            incoming
        } else {
            getClipboardText()
        }

        // 🔥 Dialog open (safe call)
        try {
            QuickDecodeDialog.showWithPrefill(this, finalText)
        } catch (e: Exception) {
            // crash safe fallback
        }

        // Activity UI दिखानी नहीं है
        finish()
    }

    // ---------------- CLIPBOARD ----------------

    private fun getClipboardText(): String {
        return try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip

            if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).text?.toString() ?: ""
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
}
