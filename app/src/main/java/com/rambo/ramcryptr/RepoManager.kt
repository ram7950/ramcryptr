package com.rambo.ramcryptr

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object RepoManager {

    private const val ROOT = "Secured_Repository"

    fun getRoot(): File {
        val root = File(Environment.getExternalStorageDirectory(), ROOT)
        if (!root.exists()) root.mkdirs()

        val noMedia = File(root, ".nomedia")
        if (!noMedia.exists()) noMedia.createNewFile()

        return root
    }

    fun getTodayFolder(): File {
        val root = getRoot()

        val date = SimpleDateFormat("dd_MMM_yyyy", Locale.getDefault()).format(Date())
        val dateFolder = File(root, date)

        if (!dateFolder.exists()) dateFolder.mkdirs()

        val categories = listOf("Images", "Videos", "Documents", "Others")
        for (c in categories) {
            val f = File(dateFolder, c)
            if (!f.exists()) f.mkdirs()
        }

        return dateFolder
    }
}
