package com.rambo.ramcryptr

import android.content.Context
import java.io.File

object SecureVaultManager {

fun getVaultBase(context:Context):File{
val base=File(context.filesDir,"vault")
if(!base.exists()) base.mkdirs()

val noMedia=File(base,".nomedia")
if(!noMedia.exists()) noMedia.createNewFile()

return base
}

fun getCategoryFolder(context:Context,mime:String):File{

val base=getVaultBase(context)

val folderName=when{
mime.startsWith("image")->"images"
mime.startsWith("video")->"videos"
mime.contains("pdf")->"docs"
mime.startsWith("text")->"docs"
else->"others"
}

val folder=File(base,folderName)
if(!folder.exists()) folder.mkdirs()

return folder
}

fun saveFile(context:Context,source:File,mime:String):File{

val folder=getCategoryFolder(context,mime)

val dest=File(
folder,
"file_${System.currentTimeMillis()}"
)

source.copyTo(dest,true)

return dest
}

}
