package com.rambo.ramcryptr

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileDecryptActivity :
AppCompatActivity(){

private val PICK_FILE=202

override fun onCreate(savedInstanceState:Bundle?){
super.onCreate(savedInstanceState)
pickFile()
}

private fun pickFile(){

val i=Intent(Intent.ACTION_GET_CONTENT)
i.type="*/*"

startActivityForResult(
Intent.createChooser(i,"Select Encrypted File"),
PICK_FILE
)

}

override fun onActivityResult(
requestCode:Int,
resultCode:Int,
data:Intent?
){

super.onActivityResult(requestCode,resultCode,data)

if(requestCode==PICK_FILE && resultCode==Activity.RESULT_OK){
data?.data?.let{
decryptUri(it)
}
}

}

private fun decryptUri(uri:Uri){

try{

val enc=File(cacheDir,"temp_encrypted")

contentResolver.openInputStream(uri)?.use{ins->
enc.outputStream().use{outs->
ins.copyTo(outs)
}
}

val tempDec=File(cacheDir,"temp_decoded")

val result = FileCryptoManager.decryptFile(enc,tempDec)

val ext=result.first
val mime=result.second

val finalFile=File(
cacheDir,
"dec_${System.currentTimeMillis()}.$ext"
)

tempDec.renameTo(finalFile)

showPreviewOptions(finalFile,mime)

}catch(e:Exception){

Toast.makeText(this,"Decode failed",Toast.LENGTH_LONG).show()
finish()

}

}

private fun showPreviewOptions(file:File,mime:String){

val fileUri = androidx.core.content.FileProvider.getUriForFile(
this,
packageName+".provider",
file
)

val dialog=AlertDialog.Builder(this)
.setTitle("Preview Ready")
.setMessage("What do you want to do?")
.setPositiveButton("Open"){_,_->

val open=Intent(Intent.ACTION_VIEW)
open.setDataAndType(fileUri,mime)
open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

startActivity(open)

}
.setNeutralButton("Save"){_,_->

val saved=SecureVaultManager.saveFile(this,file,mime)

Toast.makeText(
this,
"Saved to vault",
Toast.LENGTH_LONG
).show()

}
.setNegativeButton("Dismiss"){_,_->

finish()

}
.create()

dialog.show()

}

}
