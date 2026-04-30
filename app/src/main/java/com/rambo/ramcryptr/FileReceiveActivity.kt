package com.rambo.ramcryptr

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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

    private fun getExt(uri: Uri): String {
        val mime = contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mime)
        return ext ?: "dat"
    }

    private fun getMimeFromExt(ext: String): String {
        val mime = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(ext)
        return mime ?: "*/*"
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

    // ---------------- ENCODE ----------------

    private fun showEncodePrompt(file: File, uri: Uri) {

        AlertDialog.Builder(this)
            .setTitle("Encode File")
            .setMessage("Do you want to encode this file?")
            .setNegativeButton("No, thanks") { _, _ ->
                file.delete()
                finish()
            }
            .setPositiveButton("Encode") { _, _ ->
                encodeFile(file, uri)
            }
            .setCancelable(false)
            .show()
    }

    private fun encodeFile(file: File, uri: Uri) {

        try {
            val ext = getExt(uri)
            val mime = contentResolver.getType(uri) ?: "*/*"

            val outFile = File(
                cacheDir,
                "encoded_${System.currentTimeMillis()}.ram.bin"
            )

            FileCryptoManager.encryptFile(
                file,
                outFile,
                ext,
                mime
            )

            file.delete()

            showEncodeResult(outFile)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Encode failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showEncodeResult(file: File) {

        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage("File encoded successfully")
            .setPositiveButton("Share") { _, _ ->

                val send = Intent(Intent.ACTION_SEND)
                send.type = "*/*"

                val uri = FileProvider.getUriForFile(
                    this,
                    packageName + ".provider",
                    file
                )

                send.putExtra(Intent.EXTRA_STREAM, uri)
                send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivity(Intent.createChooser(send, "Share"))
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // ---------------- DECODE ----------------

    private fun showDecodePrompt(file: File) {

        AlertDialog.Builder(this)
            .setTitle("Encrypted File Found")
            .setMessage("Do you want to decode this file?")
            .setNegativeButton("No, thanks") { _, _ ->
                file.delete()
                finish()
            }
            .setPositiveButton("Decode") { _, _ ->
                decodeFile(file)
            }
            .setCancelable(false)
            .show()
    }

    private fun decodeFile(file: File) {

        try {
            val tempOut = File(cacheDir, "out_tmp")

            val (ext, _) =
                FileCryptoManager.decryptFile(file, tempOut)

            val finalFile = File(
                cacheDir,
                "decoded_${System.currentTimeMillis()}.$ext"
            )

            FileInputStream(tempOut).use { inp ->
                FileOutputStream(finalFile).use { out ->
                    inp.copyTo(out)
                }
            }

            tempOut.delete()
            file.delete()

            val mime = getMimeFromExt(ext)

            val view = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(
                this,
                packageName + ".provider",
                finalFile
            )

            view.setDataAndType(uri, mime)
            view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(Intent.createChooser(view, "Open file"))

            finish()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
