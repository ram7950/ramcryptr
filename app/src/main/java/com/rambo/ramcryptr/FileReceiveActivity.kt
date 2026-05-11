package com.rambo.ramcryptr

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
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

    private fun handle(uri: Uri) {

        try {
            val input = contentResolver.openInputStream(uri) ?: return

            val tempIn = File(cacheDir, "in_${System.currentTimeMillis()}")
            FileOutputStream(tempIn).use { input.copyTo(it) }

            val name = uri.lastPathSegment ?: ""

            val isEncrypted =
                name.endsWith(".ram.bin") || tempIn.readText().startsWith("RAMCRYPT_V2|")

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
            .setPositiveButton("Encode") { _, _ ->
                encodeFile(file, uri)
            }
            .setNegativeButton("Cancel") { _, _ ->
                file.delete()
                finish()
            }
            .show()
    }

    private fun encodeFile(file: File, uri: Uri) {
        try {
            val mime = contentResolver.getType(uri) ?: "*/*"
            val ext = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mime) ?: "dat"

            val outFile = File(
                cacheDir,
                "encoded_${System.currentTimeMillis()}.ram.bin"
            )

            FileCryptoManager.encryptFile(
                    file,
                    outFile,
                    ext,
                    mime,
                    CryptoMasterProvider.getMaster(this)
                )
            file.delete()

            val send = Intent(Intent.ACTION_SEND)
            send.type = "*/*"

            val fileUri = FileProvider.getUriForFile(
                this,
                packageName + ".provider",
                outFile
            )

            send.putExtra(Intent.EXTRA_STREAM, fileUri)
            send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(Intent.createChooser(send, "Share encoded file"))
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Encode failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // ---------------- DECODE ----------------

    private fun showDecodePrompt(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Encrypted File Found")
            .setMessage("Do you want to decode?")
            .setPositiveButton("Decode") { _, _ ->
                decodeFile(file)
            }
            .setNegativeButton("Cancel") { _, _ ->
                file.delete()
                finish()
            }
            .show()
    }

    private fun decodeFile(file: File) {
        try {
            val tempOut = File(cacheDir, "out_tmp")

            val (ext, mime) =
                FileCryptoManager.decryptFile(
                    file,
                    tempOut,
                    CryptoMasterProvider.getMaster(this)
                )

            val finalFile = File(
                cacheDir,
                "decoded_${System.currentTimeMillis()}.$ext"
            )

            tempOut.copyTo(finalFile, overwrite = true)

            tempOut.delete()
            file.delete()

            // 🔥 SAVE OPTION ADDED
            AlertDialog.Builder(this)
                .setTitle("Decoded Successfully")
                .setMessage("What do you want to do?")
                .setPositiveButton("Open") { _, _ ->
                    openFile(finalFile, mime)
                }
                .setNeutralButton("Save Securely") { _, _ ->
                    SaveUtils.saveSecure(this, finalFile, ext)
                    finish()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    finish()
                }
                .show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun openFile(file: File, mime: String) {

        val intent = Intent(Intent.ACTION_VIEW)

        val uri = FileProvider.getUriForFile(
            this,
            packageName + ".provider",
            file
        )

        intent.setDataAndType(uri, mime)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(Intent.createChooser(intent, "Open with"))
        finish()
    }
}
