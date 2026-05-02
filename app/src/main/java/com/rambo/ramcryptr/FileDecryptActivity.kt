package com.rambo.ramcryptr

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileDecryptActivity : AppCompatActivity() {

    private val PICK_FILE = 202

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            ?: intent.data

        if (uri != null) {
            decryptUri(uri)
        } else {
            pickFile()
        }
    }

    private fun pickFile() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "*/*"
        startActivityForResult(i, PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            decryptUri(uri)
        }
    }

    private fun decryptUri(uri: Uri) {
        try {

            val enc = File(cacheDir, "temp_enc")

            contentResolver.openInputStream(uri)?.use { ins ->
                enc.outputStream().use { outs ->
                    ins.copyTo(outs)
                }
            }

            val tempDec = File(cacheDir, "temp_dec")

            val result = FileCryptoManager.decryptFile(enc, tempDec)

            val ext = result.first
            val mime = result.second

            val finalFile = File(cacheDir, "dec_${System.currentTimeMillis()}.$ext")
            tempDec.renameTo(finalFile)

            val fileUri = androidx.core.content.FileProvider.getUriForFile(
                this,
                packageName + ".provider",
                finalFile
            )

            val open = Intent(Intent.ACTION_VIEW)
            open.setDataAndType(fileUri, mime)
            open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(open)
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Decode failed: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
