package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class DecodeProcessTextActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""

        if (text.isBlank()) {
            finish()
            return
        }

        val result = try {
            if (text.startsWith("AES256::")) {
                TextCrypto.decrypt(text, "ramcryptr_secret")
            } else {
                "Paglu 😏 ye encrypted nahi hai"
            }
        } catch (e: Exception) {
            "Decode failed"
        }

        val intent = Intent()
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, result)
        setResult(Activity.RESULT_OK, intent)

        finish()
    }
}
