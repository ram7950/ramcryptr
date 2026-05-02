package com.rambo.ramcryptr

import android.app.Activity
import android.os.Bundle

class QuickDecodeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 SAFE POPUP OPEN
        QuickDecodeDialog.showWithPrefill(this, "")

        // Activity बंद हो जाए (dialog रहेगा)
        finish()
    }
}
