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
            showEncodeDialog(tempFile, uri)
        }
    }

    private fun isEncrypted(file: File): Boolean {
        return try {
            val header = ByteArray(12)
            file.inputStream().use {
                it.read(header)
            }
            String(header).contains("RAMCRYPT")
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

    private fun showEncodeDialog(file: File, uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("📦 Normal File Detected")
            .setMessage("Do you want to encode this file?")
            .setPositiveButton("Encode") { _, _ ->
                encodeFile(file, uri)
            }
            .setNegativeButton("No Thanks") { _, _ ->
                finish()
            }
            .show()
    }

    private fun encodeFile(input: File, uri: Uri) {

        val outFile = File(cacheDir, "shared_${System.currentTimeMillis()}.ram.bin")

        try {

            val ext = uri.lastPathSegment
                ?.substringAfterLast('.', "tmp") ?: "tmp"

            val mime = contentResolver.getType(uri) ?: "*/*"

            FileCryptoManager.encryptFile(
                input,
                outFile,
                ext,
                mime
            )

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
            Toast.makeText(this, "Encode failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

        try {

            val ext = uri.lastPathSegment
                ?.substringAfterLast('.', "tmp") ?: "tmp"

            val mime = contentResolver.getType(uri) ?: "*/*"

            FileCryptoManager.encryptFile(
                input,
                outFile,
                ext,
                mime
            )

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

            Toast.makeText(this, "Encode failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun decodeFile(input: File) {

        val outFile = File(cacheDir, "decoded_temp")

        try {

            val result = FileCryptoManager.decryptFile(input, outFile)

            val ext = result.first
            val mime = result.second

            val finalFile = File(
                cacheDir,
                "dec_${System.currentTimeMillis()}.$ext"
            )

            outFile.renameTo(finalFile)

            val uri = androidx.core.content.FileProvider.getUriForFile(
                this,
                packageName + ".provider",
                finalFile
            )

            val open = Intent(Intent.ACTION_VIEW)
            open.setDataAndType(uri, mime)
            open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(open)

            finish()

        } catch (e: Exception) {

            Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
