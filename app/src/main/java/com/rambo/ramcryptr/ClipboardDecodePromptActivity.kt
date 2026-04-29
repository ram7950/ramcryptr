package com.rambo.ramcryptr

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.rambo.ramcryptr.encoding.AES256Encoder

class ClipboardDecodePromptActivity :
Activity(){

private val key=
"12345678901234567890123456789012"

override fun onCreate(
savedInstanceState:Bundle?
){
super.onCreate(
savedInstanceState
)

val encrypted=
intent.getStringExtra(
"enc_text"
) ?: ""

AlertDialog.Builder(
this
)
.setTitle(
"Encrypted Data Found"
)
.setMessage(
"Do wanna decode data?"
)
.setNegativeButton(
"No Thanks"
){_,_->
finish()
}
.setPositiveButton(
"Decode"
){_,_->

try{

val decoded=
AES256Encoder.decryptText(
encrypted,
key
)

val dialog=
AlertDialog.Builder(
this
)
.setTitle(
"Decoded Message"
)
.setMessage(
decoded
)
.create()

dialog.show()

Handler(
Looper.getMainLooper()
)
.postDelayed({

if(
dialog.isShowing
){
dialog.dismiss()
}

finish()

},20000)

}catch(
e:Exception
){

AlertDialog.Builder(
this
)
.setTitle(
"Decode Error"
)
.setMessage(
"Invalid encrypted text"
)
.setPositiveButton(
"OK"
){_,_->
finish()
}
.show()

}

}
.show()

}

}
