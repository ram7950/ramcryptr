package com.rambo.ramcryptr

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder

class ClipboardMonitorService :
Service() {

private lateinit var clipboard:
ClipboardManager

private val listener=
ClipboardManager
.OnPrimaryClipChangedListener {

checkClipboard()

}

override fun onCreate(){
super.onCreate()

clipboard=
getSystemService(
Context.CLIPBOARD_SERVICE
)
as ClipboardManager

clipboard
.addPrimaryClipChangedListener(
listener
)

}

private fun checkClipboard(){

if(
!clipboard.hasPrimaryClip()
){
return
}

val clip:
ClipData=
clipboard.primaryClip ?: return

if(
clip.itemCount<1
){
return
}

val copied=
clip.getItemAt(0)
.coerceToText(this)
.toString()
.trim()

if(
copied.startsWith(
"AES256::"
)
){

val i=
Intent(
this,
ClipboardDecodePromptActivity::class.java
)

i.addFlags(
Intent.FLAG_ACTIVITY_NEW_TASK
)

i.putExtra(
"enc_text",
copied
)

startActivity(i)

}

}

override fun onDestroy(){
clipboard
.removePrimaryClipChangedListener(
listener
)
super.onDestroy()
}

override fun onBind(
intent:Intent?
):IBinder?{
return null
}

}
