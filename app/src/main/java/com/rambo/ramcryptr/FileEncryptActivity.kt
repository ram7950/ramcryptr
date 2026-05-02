package com.rambo.ramcryptr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileEncryptActivity : AppCompatActivity() {

    private val PICK_FILE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            ?: intent.data

        if (uri != null) {
            encryptUri(uri)
        } else {
            pickFile()
        }
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            encryptUri(uri)
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

            val outFile = File(cacheDir, "enc_${System.currentTimeMillis()}.ram")

            val ext = uri.lastPathSegment?.substringAfterLast('.', "tmp") ?: "tmp"
            val mime = contentResolver.getType(uri) ?: "*/*"

            FileCryptoManager.encryptFile(input, outFile, ext, mime)

            val send = Intent(Intent.ACTION_SEND)
            send.type = "*/*"

            val fileUri = androidx.core.content.FileProvider.getUriForFile(
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
