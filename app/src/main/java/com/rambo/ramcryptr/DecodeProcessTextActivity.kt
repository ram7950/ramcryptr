package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class DecodeProcessTextActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""

        if (text.isBlank()) {
            finish()
            return
        }

        // ✅ NORMAL TEXT → NO REPLACE, ONLY TOAST
        if (!text.startsWith("AES256::")) {
            Toast.makeText(this, "Paglu 😏 encode nahi decode dabao", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val result = try {
            TextCrypto.decrypt(text, "ramcryptr_secret")
        } catch (e: Exception) {
            "Decode failed"
        }

        val intent = Intent()
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, result)
        setResult(Activity.RESULT_OK, intent)

        finish()
    }
}
