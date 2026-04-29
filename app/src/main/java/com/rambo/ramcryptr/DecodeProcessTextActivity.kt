package com.rambo.ramcryptr

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rambo.ramcryptr.encoding.AES256Encoder

class DecodeProcessTextActivity :
AppCompatActivity(){

private val key=
"12345678901234567890123456789012"

override fun onCreate(
savedInstanceState:Bundle?
){
super.onCreate(
savedInstanceState
)

val text=
intent
.getCharSequenceExtra(
Intent.EXTRA_PROCESS_TEXT
)
?.toString()
?.trim()
?.replace(
"\\s".toRegex(),
""
)
?: ""

if(
!text.startsWith(
"AES256::"
)
&&
!text.startsWith(
"🔄CON🔄"
)
){

AlertDialog.Builder(
this
)
.setTitle(
"😄"
)
.setMessage(
"Are Paglu,\nDecode nahi Encode dabao"
)
.setPositiveButton(
"OK"
){_,_->
finish()
}
.show()

return

}

try{

val clean=
text.removePrefix(
"🔄CON🔄"
)

val decoded=
AES256Encoder.decryptText(
clean,
key
)

setResult(
Activity.RESULT_OK,
Intent().putExtra(
Intent.EXTRA_PROCESS_TEXT,
decoded
)
)

finish()

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

}
