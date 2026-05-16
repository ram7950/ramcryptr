package com.rambo.ramcryptr

import android.app.AlertDialog
import android.content.Intent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

object TnetDialogs {

    fun showPatchInDialog(
        activity: AppCompatActivity
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

            Toast.makeText(
                activity,
                "Scan flow coming next",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnImport.setOnClickListener {

            val intent =
                Intent(Intent.ACTION_GET_CONTENT)

            intent.type = "image/png"

            activity.startActivity(
                Intent.createChooser(
                    intent,
                    "IMPORT KEY MATRIX"
                )
            )
        }

        dialog.show()
    }
}
