package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class EncodeTextActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""

        if (text.isEmpty()) {
            finish()
            return
        }

        val result = TextCrypto.encrypt(text, "ramcryptr_secret")

        val out = Intent()
        out.putExtra(Intent.EXTRA_PROCESS_TEXT, result)
        setResult(RESULT_OK, out)

        finish()
    }
}
