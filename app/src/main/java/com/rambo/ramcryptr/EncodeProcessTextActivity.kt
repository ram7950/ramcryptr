package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rambo.ramcryptr.encoding.AES256Encoder

class EncodeProcessTextActivity :
AppCompatActivity() {

private val key=
"12345678901234567890123456789012"

override fun onCreate(
savedInstanceState:Bundle?
){
super.onCreate(
savedInstanceState
)

val selected=
intent.getCharSequenceExtra(
Intent.EXTRA_PROCESS_TEXT
)?.toString() ?: ""

if(
selected.startsWith(
"AES256::"
)
){

setResult(
Activity.RESULT_OK,
Intent().putExtra(
Intent.EXTRA_PROCESS_TEXT,
selected
)
)

finish()
return
}

val encoded=
AES256Encoder.encryptText(
selected,
key
)

setResult(
Activity.RESULT_OK,
Intent().putExtra(
Intent.EXTRA_PROCESS_TEXT,
encoded
)
)

finish()

}

}
