package com.rambo.ramcryptr

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.rambo.ramcryptr.encoding.AES256Encoder

class ShareEncodeActivity :
Activity(){

private val key=
"12345678901234567890123456789012"

override fun onCreate(
savedInstanceState:Bundle?
){
super.onCreate(
savedInstanceState
)

val sharedText=
intent.getStringExtra(
Intent.EXTRA_TEXT
)?.trim() ?: ""

if(
sharedText.isBlank()
){
finish()
return
}

AlertDialog.Builder(
this
)
.setTitle(
"Encode this text?"
)
.setMessage(
sharedText
)
.setNegativeButton(
"Cancel"
){_,_->
finish()
}
.setPositiveButton(
"Encode"
){_,_->

val enc=
AES256Encoder.encryptText(
sharedText,
key
)

showResult(
enc
)

}
.show()

}

private fun showResult(
enc:String
){

AlertDialog.Builder(
this
)
.setTitle(
"Encoded Message"
)
.setMessage(
enc
)
.setNeutralButton(
"Copy"
){_,_->

val cb=
getSystemService(
Context.CLIPBOARD_SERVICE
)
as ClipboardManager

cb.setPrimaryClip(
ClipData.newPlainText(
"encoded",
enc
)
)

finish()

}
.setNegativeButton(
"Close"
){_,_->
finish()
}
.setPositiveButton(
"Share"
){_,_->

val share=
Intent(
Intent.ACTION_SEND
)

share.type=
"text/plain"

share.putExtra(
Intent.EXTRA_TEXT,
enc
)

startActivity(
Intent.createChooser(
share,
"Share encrypted text"
)
)

finish()

}
.show()

}

}
