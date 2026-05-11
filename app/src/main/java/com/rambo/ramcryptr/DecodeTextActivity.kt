package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class DecodeTextActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""

        if (text.isEmpty()) {
            finish()
            return
        }

        val result = TextCrypto.decrypt(text, CryptoMasterProvider.getMaster(this))

        val out = Intent()
        out.putExtra(Intent.EXTRA_PROCESS_TEXT, result)
        setResult(RESULT_OK, out)

        finish()
    }
}
