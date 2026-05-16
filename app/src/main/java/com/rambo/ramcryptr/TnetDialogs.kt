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


    fun showInitiateCommnDialog(
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

        val etChannelName =
            EditText(activity).apply {

                hint =
                    "Enter Channel Name"
            }

        val tvChannelId =
            TextView(activity).apply {

                text =
                    "Channel ID not generated"
            }

        val btnGenerate =
            Button(activity).apply {

                text =
                    "Generate Channel ID"
            }

        val btnSecure =
            Button(activity).apply {

                text =
                    "SECURE 🔐"
            }

        var generatedId = ""

        btnGenerate.setOnClickListener {

            generatedId =
                java.util.UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .uppercase()

            tvChannelId.text =
                "Channel ID: $generatedId"
        }

        btnSecure.setOnClickListener {

            val name =
                etChannelName.text.toString()

            if (
                name.isBlank() ||
                generatedId.isBlank()
            ) {

                Toast.makeText(
                    activity,
                    "Generate channel properly",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val joinSecret =
                CryptoKeyManager
                    .generateJoinSecret()

            val channel = Channel(
                channelName = name,
                channelId = generatedId,
                joinSecret = joinSecret
            )

            ChannelStorage.saveChannel(
                activity,
                channel
            )

            ChannelManager.setActiveChannel(
                activity,
                channel
            )

            Toast.makeText(
                activity,
                "Secure channel created",
                Toast.LENGTH_SHORT
            ).show()
        }

        layout.addView(etChannelName)
        layout.addView(btnGenerate)
        layout.addView(tvChannelId)
        layout.addView(btnSecure)

        AlertDialog.Builder(activity)
            .setTitle("INITIATE COMMN")
            .setView(layout)
            .show()
    }


}
