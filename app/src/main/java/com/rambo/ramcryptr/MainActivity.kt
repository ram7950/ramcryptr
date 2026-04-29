package com.rambo.ramcryptr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.rambo.ramcryptr.encoding.AES256Encoder

class MainActivity :
AppCompatActivity(){

private lateinit var inputText:EditText
private lateinit var encodeButton:Button
private lateinit var decodeButton:Button

private val key=
"12345678901234567890123456789012"

override fun onCreate(
savedInstanceState:Bundle?
){
super.onCreate(
savedInstanceState
)

setContentView(
R.layout.activity_main
)

inputText=
findViewById(
R.id.inputBox
)

encodeButton=
findViewById(
R.id.btnEncode
)

decodeButton=
findViewById(
R.id.btnDecode
)

startService(
Intent(
this,
ClipboardMonitorService::class.java
)
)

encodeButton.setOnClickListener{

try{

val txt=
inputText.text
.toString()

if(
txt.isNotBlank()
){

val enc=
AES256Encoder.encryptText(
txt,
key
)

inputText.setText(
enc
)

}

}catch(
e:Exception
){
e.printStackTrace()
}

}

decodeButton.setOnClickListener{

try{

val txt=
inputText.text
.toString()
.trim()
.replace(
"\n",
""
)

val clean=
if(
txt.startsWith(
"🔄CON🔄"
)
)
{
txt.removePrefix(
"🔄CON🔄"
)
}else{
txt
}

if(
clean.startsWith(
"AES256::"
)
){

val dec=
AES256Encoder.decryptText(
clean,
key
)

inputText.setText(
dec
)

}

}catch(
e:Exception
){
e.printStackTrace()
}

}

/* LONG PRESS = FILE CRYPTO */

encodeButton.setOnLongClickListener{

startActivity(
Intent(
this,
FileEncryptActivity::class.java
)
)

true
}

decodeButton.setOnLongClickListener{

startActivity(
Intent(
this,
FileDecryptActivity::class.java
)
)

true
}

}

}
