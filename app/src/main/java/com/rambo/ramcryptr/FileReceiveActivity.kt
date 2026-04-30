package com.rambo.ramcryptr

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileReceiveActivity : AppCompatActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)

val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

if (uri == null) {
finish()
return
}

handleIncomingFile(uri)
}

private fun handleIncomingFile(uri: Uri) {

val tempFile = File(cacheDir, "incoming_file")

contentResolver.openInputStream(uri)?.use { input ->
tempFile.outputStream().use { output ->
input.copyTo(output)
}
}

if (isEncrypted(tempFile)) {
showDecodeDialog(tempFile)
} else {
showEncodeDialog(tempFile)
}
}

private fun isEncrypted(file: File): Boolean {
return try {
val header = ByteArray(11)
file.inputStream().use {
it.read(header)
}
String(header).contains("RAMCRYPT_V1")
} catch (e: Exception) {
false
}
}

private fun showDecodeDialog(file: File) {

AlertDialog.Builder(this)
.setTitle("🔐 Encrypted File Detected")
.setMessage("Do you want to decode this file?")
.setPositiveButton("Decode") { _, _ ->
decodeFile(file)
}
.setNegativeButton("No Thanks") { _, _ ->
finish()
}
.show()
}

private fun showEncodeDialog(file: File) {

AlertDialog.Builder(this)
.setTitle("📦 Normal File Detected")
.setMessage("Do you want to encode this file?")
.setPositiveButton("Encode") { _, _ ->
encodeFile(file)
}
.setNegativeButton("No Thanks") { _, _ ->
finish()
}
.show()
}

private fun encodeFile(input: File) {

val outFile = File(cacheDir, "shared_encoded.ram.bin")

try {

FileCryptoManager.encryptFile(input, outFile)

val send = Intent(Intent.ACTION_SEND)
send.type = "*/*"

val uri = androidx.core.content.FileProvider.getUriForFile(
this,
packageName + ".provider",
outFile
)

send.putExtra(Intent.EXTRA_STREAM, uri)
send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

startActivity(Intent.createChooser(send, "Share encrypted file"))

finish()

} catch (e: Exception) {

Toast.makeText(this, "Encode failed", Toast.LENGTH_SHORT).show()
finish()

}
}

private fun decodeFile(input: File) {

val outFile = File(cacheDir, "shared_decoded")

try {

FileCryptoManager.decryptFile(input, outFile)

val uri = androidx.core.content.FileProvider.getUriForFile(
this,
packageName + ".provider",
outFile
)

val open = Intent(Intent.ACTION_VIEW)
open.setDataAndType(uri, "*/*")
open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

startActivity(open)

finish()

} catch (e: Exception) {

Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
finish()

}
}

}
