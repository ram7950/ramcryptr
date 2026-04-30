package com.rambo.ramcryptr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class FileReceiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = intent?.getParcelableExtra(Intent.EXTRA_STREAM)

        if (uri != null) {
            handleFile(uri)
        } else {
            finish()
        }
    }

    private fun handleFile(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val tempInput = File(cacheDir, "input.tmp")

            FileOutputStream(tempInput).use { output ->
                inputStream.copyTo(output)
            }

            val ext = uri.lastPathSegment
                ?.substringAfterLast('.', "tmp") ?: "tmp"

            val mime = contentResolver.getType(uri) ?: "*/*"

            val outFile = File(cacheDir, "encoded_${System.currentTimeMillis()}.bin")

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

        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }
}
