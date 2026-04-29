package com.rambo.ramcryptr

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.rambo.ramcryptr.encoding.AES256Encoder

class MainActivity :
AppCompatActivity() {

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

encodeButton
.setOnClickListener{

val plain=
inputText.text
.toString()
.trim()

if(
plain.isNotEmpty()
){

val enc=
AES256Encoder.encryptText(
plain,
key
)

inputText.setText(
enc
)

}

}

decodeButton
.setOnClickListener{

try{

val encrypted=
inputText.text
.toString()
.trim()
.replace(
"\\s".toRegex(),
""
)

val dec=
AES256Encoder.decryptText(
encrypted,
key
)

inputText.setText(
dec
)

}catch(
e:Exception
){

inputText.setError(
"Invalid encrypted text"
)

}

}

}

}
