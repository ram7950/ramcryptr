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
        if (uri != null) handleFile(uri) else finish()
    }

    private fun handleFile(uri: Uri) {
        try {
            val name = (uri.lastPathSegment ?: "").lowercase()

            // 🔥 Only .ram.bin = decode
            val isEncrypted = name.endsWith(".ram.bin")

            if (isEncrypted) {
                // 🔓 DECODE FLOW
                val inputStream = contentResolver.openInputStream(uri) ?: return
                val tempInput = File(cacheDir, "enc_input.ram.bin")

                FileOutputStream(tempInput).use { output ->
                    inputStream.copyTo(output)
                }

                val tempOutput = File(cacheDir, "decoded_temp")

                val result = FileCryptoManager.decryptFile(tempInput, tempOutput)
                val ext = result.first
                val mime = result.second

                val finalFile = File(
                    cacheDir,
                    "decoded_${System.currentTimeMillis()}.$ext"
                )

                tempOutput.renameTo(finalFile)

                val intent = Intent(Intent.ACTION_VIEW)
                val fileUri = FileProvider.getUriForFile(
                    this,
                    packageName + ".provider",
                    finalFile
                )

                intent.setDataAndType(fileUri, mime)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivity(intent)
                finish()

            } else {
                // 🔐 ENCODE FLOW
                val inputStream = contentResolver.openInputStream(uri) ?: return
                val tempInput = File(cacheDir, "input.tmp")

                FileOutputStream(tempInput).use { output ->
                    inputStream.copyTo(output)
                }

                val ext = name.substringAfterLast('.', "tmp")

                val mime = contentResolver.getType(uri)
                    ?: MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(ext)
                    ?: "*/*"

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
