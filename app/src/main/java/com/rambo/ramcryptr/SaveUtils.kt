package com.rambo.ramcryptr

import android.content.Context
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object SaveUtils {

    fun saveSecure(context: Context, sourceFile: File, ext: String) {

        try {
            // 🔹 Base folder
            val baseDir = File("/storage/emulated/0/Secured_Repository")

            if (!baseDir.exists()) baseDir.mkdirs()

            // 🔹 .nomedia
            val noMedia = File(baseDir, ".nomedia")
            if (!noMedia.exists()) noMedia.createNewFile()

            // 🔹 Date folder
            val dateFormat = SimpleDateFormat("dd_MMM_yyyy", Locale.ENGLISH)
            val dateFolderName = dateFormat.format(Date())

            val dateDir = File(baseDir, dateFolderName)
            if (!dateDir.exists()) dateDir.mkdirs()

            // 🔹 Category detect
            val category = when (ext.lowercase()) {
                "jpg", "jpeg", "png", "webp" -> "Images"
                "mp4", "mkv", "avi" -> "Videos"
                "pdf", "doc", "docx" -> "Documents"
                else -> "Others"
            }

            val categoryDir = File(dateDir, category)
            if (!categoryDir.exists()) categoryDir.mkdirs()

            // 🔹 Final file
            val outFile = File(
                categoryDir,
                "file_${System.currentTimeMillis()}.$ext"
            )

            // 🔹 Copy
            FileInputStream(sourceFile).use { inp ->
                FileOutputStream(outFile).use { out ->
                    inp.copyTo(out)
                }
            }

            Toast.makeText(
                context,
                "Saved in Secured Repository",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
        }
    }
}
