package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileDecryptActivity :
AppCompatActivity(){

private val PICK_FILE=202

override fun onCreate(
savedInstanceState:Bundle?
){
super.onCreate(
savedInstanceState
)

pickFile()

}

private fun pickFile(){

val i=
Intent(
Intent.ACTION_GET_CONTENT
)

i.type="*/*"

startActivityForResult(
Intent.createChooser(
i,
"Select Encrypted File"
),
PICK_FILE
)

}

override fun onActivityResult(
requestCode:Int,
resultCode:Int,
data:Intent?
){

super.onActivityResult(
requestCode,
resultCode,
data
)

if(
requestCode==PICK_FILE &&
resultCode==Activity.RESULT_OK
){

data?.data?.let{
decryptUri(it)
}

}

}

private fun decryptUri(
uri:Uri
){

try{

val enc=
File(
cacheDir,
"temp_encrypted.ram"
)

contentResolver
.openInputStream(uri)
?.use{
ins->
enc.outputStream()
.use{
outs->
ins.copyTo(
outs
)
}
}

val dec=
File(
cacheDir,
"preview_file"
)

FileCryptoManager
.decryptFile(
enc,
dec
)

Toast.makeText(
this,
"Preview ready",
Toast.LENGTH_LONG
).show()

val open=
Intent(
Intent.ACTION_VIEW
)

val fileUri=
androidx.core.content
.FileProvider
.getUriForFile(
this,
packageName+
".provider",
dec
)

open.setDataAndType(
fileUri,
"*/*"
)

open.addFlags(
Intent.FLAG_GRANT_READ_URI_PERMISSION
)

startActivity(open)

finish()

}catch(
e:Exception
){

Toast.makeText(
this,
"Decrypt failed",
Toast.LENGTH_LONG
).show()

finish()

}

}

}
