package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileEncryptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        if (uri != null) {
            encryptUri(uri)
        } else {
            finish()
        }
    }

    private fun encryptUri(uri: Uri) {
        try {

            val input = File(cacheDir, "temp_input")

            contentResolver.openInputStream(uri)?.use { ins ->
                input.outputStream().use { outs ->
                    ins.copyTo(outs)
                }
            }

            // ✅ FINAL EXTENSION FIX
            val outFile = File(
                cacheDir,
                "enc_${System.currentTimeMillis()}.ram.bin"
            )

            val ext = uri.lastPathSegment?.substringAfterLast('.', "tmp") ?: "tmp"
            val mime = contentResolver.getType(uri) ?: "*/*"

            FileCryptoManager.encryptFile(input, outFile, ext, mime)

            val send = Intent(Intent.ACTION_SEND)
            send.type = "*/*"

            val fileUri =
                androidx.core.content.FileProvider.getUriForFile(
                    this,
                    packageName + ".provider",
                    outFile
                )

            send.putExtra(Intent.EXTRA_STREAM, fileUri)
            send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(Intent.createChooser(send, "Share encrypted file"))

            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Encrypt failed: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
