package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class EncodeProcessTextActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""

        if (text.isBlank()) {
            finish()
            return
        }

        val result = try {
            TextCrypto.encrypt(text, CryptoMasterProvider.getMaster(this))
        } catch (e: Exception) {
            text
        }

        val intent = Intent()
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, result)
        setResult(Activity.RESULT_OK, intent)

        finish()
    }
}
