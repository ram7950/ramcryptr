package com.rambo.ramcryptr

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
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

    private fun getFileName(uri: Uri): String {
        var name: String? = null

        val cursor: Cursor? =
            contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && it.moveToFirst()) {
                name = it.getString(index)
            }
        }

        return name ?: (uri.lastPathSegment ?: "file")
    }

    private fun isRamEncrypted(file: File): Boolean {
        return try {
            val fis = FileInputStream(file)
            val buffer = ByteArray(50)
            val bytesRead = fis.read(buffer)
            fis.close()

            if (bytesRead <= 0) false
            else {
                val text = String(buffer, 0, bytesRead)
                text.startsWith("RAMCRYPT_V2|")
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun handle(uri: Uri) {
        try {
            val input = contentResolver.openInputStream(uri) ?: return

            val tempIn = File(cacheDir, "in_${System.currentTimeMillis()}")
            FileOutputStream(tempIn).use { input.copyTo(it) }

            val fileName = getFileName(uri)

            val isEncrypted =
                fileName.endsWith(".ram.bin") || isRamEncrypted(tempIn)

            if (isEncrypted) {
                showDecodePrompt(tempIn)
            } else {
                showEncodePrompt(tempIn, uri)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    // ---------------- ENCODE PROMPT ----------------

    private fun showEncodePrompt(file: File, uri: Uri) {

        AlertDialog.Builder(this)
            .setTitle("Encode File")
            .setMessage("Do you want to encode this file?")
            .setNegativeButton("No, thanks") { _, _ ->
                file.delete()
                finish()
            }
            .setPositiveButton("Encode") { _, _ ->
                // NEXT STEP में actual encode करेंगे
                // अभी सिर्फ confirm test
                file.delete()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // ---------------- DECODE PROMPT ----------------

    private fun showDecodePrompt(file: File) {

        AlertDialog.Builder(this)
            .setTitle("Encrypted File Found")
            .setMessage("Do you want to decode this file?")
            .setNegativeButton("No, thanks") { _, _ ->
                file.delete()
                finish()
            }
            .setPositiveButton("Decode") { _, _ ->
                // NEXT STEP में actual decode करेंगे
                file.delete()
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
