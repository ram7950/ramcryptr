package com.rambo.ramcryptr

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

        if (name.isNullOrEmpty()) {
            name = uri.lastPathSegment
        }

        return name ?: "file"
    }

    private fun isRamEncrypted(file: File): Boolean {
        return try {
            val fis = FileInputStream(file)
            val buffer = ByteArray(50)
            val bytesRead = fis.read(buffer)
            fis.close()

            if (bytesRead <= 0) {
                false
            } else {
                val text = String(buffer, 0, bytesRead)
                text.startsWith("RAMCRYPT_V2|")
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun getMimeFromExt(ext: String): String {
        val mime = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(ext)
        return mime ?: "*/*"
    }

    private fun getExt(uri: Uri): String {
        val mime = contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mime)

        return ext ?: "dat"
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

                val tempOut = File(cacheDir, "out_tmp")

                val (ext, _) =
                    FileCryptoManager.decryptFile(tempIn, tempOut)

                val mime = getMimeFromExt(ext)

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
                tempIn.delete()

                val view = Intent(Intent.ACTION_VIEW)
                val fileUri = FileProvider.getUriForFile(
                    this,
                    packageName + ".provider",
                    finalFile
                )

                view.setDataAndType(fileUri, mime)
                view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                if (view.resolveActivity(packageManager) != null) {
                    startActivity(view)
                } else {
                    Toast.makeText(
                        this,
                        "No app found to open file",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                finish()

            } else {

                val ext = getExt(uri)
                val mime = getMimeFromExt(ext)

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

                tempIn.delete()

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
