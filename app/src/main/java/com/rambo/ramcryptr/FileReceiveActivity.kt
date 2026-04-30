package com.rambo.ramcryptr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class FileReceiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = when {
            intent?.action == Intent.ACTION_SEND ->
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

            intent?.action == Intent.ACTION_VIEW ->
                intent.data

            else -> null
        }

        if (uri != null) handle(uri) else finish()
    }

    private fun getExt(uri: Uri): String {
        val mime = contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
        return ext ?: "dat"
    }

    private fun handle(uri: Uri) {
        try {
            val input = contentResolver.openInputStream(uri) ?: return

            val tempIn = File(cacheDir, "in_${System.currentTimeMillis()}")
            FileOutputStream(tempIn).use { input.copyTo(it) }

            val isEncrypted = uri.toString().endsWith(".ram.bin")

            if (isEncrypted) {
                val tempOut = File(cacheDir, "out_tmp")

                val (ext, mime) =
                    FileCryptoManager.decryptFile(tempIn, tempOut)

                val finalFile = File(
                    cacheDir,
                    "decoded_${System.currentTimeMillis()}.$ext"
                )

                tempOut.renameTo(finalFile)

                val view = Intent(Intent.ACTION_VIEW)
                val fileUri = FileProvider.getUriForFile(
                    this,
                    packageName + ".provider",
                    finalFile
                )

                view.setDataAndType(fileUri, mime)
                view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivity(view)
                finish()

            } else {
                val ext = getExt(uri)
                val mime = contentResolver.getType(uri) ?: "*/*"

                val outFile = File(
                    cacheDir,
                    "encoded_${System.currentTimeMillis()}.ram.bin"
                )

                FileCryptoManager.encryptFile(
                    tempIn,
                    outFile,
                    ext,
                    mime
                )

                val send = Intent(Intent.ACTION_SEND)
                send.type = "*/*"

                val fileUri = FileProvider.getUriForFile(
                    this,
                    packageName + ".provider",
                    outFile
                )

                send.putExtra(Intent.EXTRA_STREAM, fileUri)
                send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivity(Intent.createChooser(send, "Share"))
                finish()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }
}
