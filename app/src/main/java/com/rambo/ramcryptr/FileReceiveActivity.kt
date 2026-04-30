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

        val uri: Uri? = intent?.getParcelableExtra(Intent.EXTRA_STREAM)

        if (uri != null) {
            processFile(uri)
        } else {
            finish()
        }
    }

    private fun getExtension(uri: Uri): String {
        val mime = contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
        return ext ?: "dat"
    }

    private fun processFile(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return

            val tempInput = File(cacheDir, "input_${System.currentTimeMillis()}")
            FileOutputStream(tempInput).use {
                inputStream.copyTo(it)
            }

            val isEncrypted = uri.toString().endsWith(".ram.bin")

            if (isEncrypted) {

                // 🔓 DECODE
                val tempOutput = File(cacheDir, "decoded_temp")

                val result = FileCryptoManager.decryptFile(tempInput, tempOutput)

                val finalFile = File(
                    cacheDir,
                    "decoded_${System.currentTimeMillis()}.${result.first}"
                )

                tempOutput.renameTo(finalFile)

                val intent = Intent(Intent.ACTION_VIEW)
                val fileUri = FileProvider.getUriForFile(
                    this,
                    packageName + ".provider",
                    finalFile
                )

                intent.setDataAndType(fileUri, result.second)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivity(intent)
                finish()

            } else {

                // 🔐 ENCODE
                val ext = getExtension(uri)   // ✅ FIX
                val mime = contentResolver.getType(uri) ?: "*/*"

                val outFile = File(
                    cacheDir,
                    "encoded_${System.currentTimeMillis()}.ram.bin"
                )

                FileCryptoManager.encryptFile(
                    tempInput,
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

                startActivity(Intent.createChooser(send, "Share via"))
                finish()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }
}
