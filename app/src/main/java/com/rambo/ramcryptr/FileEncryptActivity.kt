package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileEncryptActivity :
AppCompatActivity(){

private val PICK_FILE=101

override fun onCreate(
savedInstanceState:Bundle?
){
super.onCreate(
savedInstanceState
)

pickFile()

}

private fun pickFile(){

val intent=
Intent(
Intent.ACTION_GET_CONTENT
)

intent.type="*/*"

startActivityForResult(
Intent.createChooser(
intent,
"Select File"
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

val uri=
data?.data ?: return

encryptUri(
uri
)

}

}

private fun encryptUri(
uri:Uri
){

try{

val input=
File(
cacheDir,
"temp_input"
)

contentResolver
.openInputStream(uri)
?.use{
ins->
input.outputStream()
.use{
outs->
ins.copyTo(
outs
)
}
}

val outFile=
File(
cacheDir,
"encrypted_file.ram.bin"
)

FileCryptoManager
.encryptFile(
input,
outFile
)

Toast.makeText(
this,
"File encrypted",
Toast.LENGTH_LONG
).show()

val send=
Intent(
Intent.ACTION_SEND
)

send.type="*/*"

val fileUri=
androidx.core.content.FileProvider
.getUriForFile(
this,
packageName+
".provider",
outFile
)

send.putExtra(
Intent.EXTRA_STREAM,
fileUri
)

send.addFlags(
Intent.FLAG_GRANT_READ_URI_PERMISSION
)

startActivity(
Intent.createChooser(
send,
"Share encrypted file"
)
)

finish()

}catch(
e:Exception
){

Toast.makeText(
this,
"Encrypt failed",
Toast.LENGTH_LONG
).show()

finish()

}

}

}
