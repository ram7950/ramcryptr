package com.rambo.ramcryptr

import android.app.AlertDialog
import android.content.Intent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

object TnetDialogs {

    fun showPatchInDialog(
        activity: AppCompatActivity,
        onScanClick: () -> Unit,
        onImportClick: () -> Unit
    ) {

        val layout =
            LinearLayout(activity).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    40,
                    30,
                    40,
                    10
                )
            }

        val btnScan =
            Button(activity).apply {

                text =
                    "SCAN KEY MATRIX"
            }

        val btnImport =
            Button(activity).apply {

                text =
                    "IMPORT KEY MATRIX"
            }

        layout.addView(btnScan)
        layout.addView(btnImport)

        val dialog =
            AlertDialog.Builder(activity)

                .setTitle(
                    "PATCH IN"
                )

                .setView(layout)

                .create()

        btnScan.setOnClickListener {
            onScanClick()
        }

        btnImport.setOnClickListener {
            onImportClick()
        }

        dialog.show()
    }
}
