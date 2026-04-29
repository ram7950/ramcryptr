package com.rambo.ramcryptr

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.Intent
import com.rambo.ramcryptr.encoding.AES256Encoder

class ShareDecodeActivity :
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

if(
!sharedText.startsWith(
"AES256::"
)
){
AlertDialog.Builder(
this
)
.setTitle(
"Decode Error"
)
.setMessage(
"Shared text is not encrypted"
)
.setPositiveButton(
"OK"
){_,_->
finish()
}
.show()

return
}

AlertDialog.Builder(
this
)
.setTitle(
"Decode shared text?"
)
.setMessage(
"Encrypted data detected"
)
.setNegativeButton(
"Cancel"
){_,_->
finish()
}
.setPositiveButton(
"Decode"
){_,_->

try{

val dec=
AES256Encoder.decryptText(
sharedText,
key
)

val d=
AlertDialog.Builder(
this
)
.setTitle(
"Decoded Message"
)
.setMessage(
dec
)
.create()

d.show()

Handler(
Looper.getMainLooper()
)
.postDelayed({

if(
d.isShowing
){
d.dismiss()
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
