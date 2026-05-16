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
        activity: AppCompatActivity,
        latestBitmapProvider: () -> android.graphics.Bitmap?,
        latestBitmapUpdater: (android.graphics.Bitmap) -> Unit
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

        val btnCreateMatrix =
            Button(activity).apply {

                text =
                    "CREATE KEY MATRIX"

                visibility =
                    android.view.View.GONE
            }

        val btnShareMatrix =
            Button(activity).apply {

                text =
                    "SHARE MATRIX"

                visibility =
                    android.view.View.GONE
            }

        val ivMatrixPreview =
            ImageView(activity).apply {

                visibility =
                    android.view.View.GONE

                adjustViewBounds = true

                setBackgroundColor(
                    android.graphics.Color.BLACK
                )

                setPadding(
                    20,
                    40,
                    20,
                    40
                )
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

            btnCreateMatrix.visibility =
                android.view.View.VISIBLE

            Toast.makeText(
                activity,
                "Secure channel created",
                Toast.LENGTH_SHORT
            ).show()
        }

        layout.addView(etChannelName)
        layout.addView(btnGenerate)
        layout.addView(tvChannelId)
        btnCreateMatrix.setOnClickListener {

            val matrixSeed =
                etChannelName.text.toString() +
                generatedId

            val matrixBuilder =
                StringBuilder()

            val entropyRandom =
                java.util.Random(
                    (
                        generatedId.hashCode()
                            .toLong() shl 32
                    ) xor
                    matrixSeed.hashCode()
                        .toLong()
                )

            val gridSize = 64

            val fixedSize =
                gridSize * gridSize

            var payloadIndex = 0

            for (i in 0 until fixedSize) {

                val row =
                    i / gridSize

                val col =
                    i % gridSize

                val isFinder =
                    (
                        (row < 8 && col < 8) ||
                        (row < 8 && col > 55) ||
                        (row > 55 && col < 8)
                    )

                val isReserved =
                    (
                        row == 32 ||
                        col == 32
                    )

                if (isFinder) {

                    matrixBuilder.append("▓")

                } else if (isReserved) {

                    matrixBuilder.append("▒")

                } else {

                    val activeChannel =
                        ChannelManager
                            .getActiveChannel()

                    val encodedPayload =
                        if (activeChannel != null) {

                            MatrixPayloadCodec
                                .encodeChannel(
                                    activeChannel
                                )

                        } else {

                            matrixSeed
                        }

                    val payloadBits =
                        MatrixBitstream
                            .stringToBits(
                                encodedPayload
                            )

                    val bit =
                        if (
                            payloadIndex <
                            payloadBits.length
                        ) {

                            payloadBits[
                                payloadIndex++
                            ]

                        } else {

                            if (
                                entropyRandom.nextBoolean()
                            ) {
                                '1'
                            } else {
                                '0'
                            }
                        }

                    if (bit == '1') {

                        matrixBuilder.append("▓")

                    } else {

                        matrixBuilder.append("░")
                    }
                }

                if ((i + 1) % gridSize == 0) {

                    matrixBuilder.append("\n")
                }
            }

            val finalMatrix =
                matrixBuilder.toString()

            val bitmap =
                MatrixBitmapGenerator.generate(
                    finalMatrix
                )

            latestBitmapUpdater(bitmap)

            ivMatrixPreview.setImageBitmap(
                bitmap
            )

            ivMatrixPreview.visibility =
                android.view.View.VISIBLE

            btnShareMatrix.visibility =
                android.view.View.VISIBLE

            Toast.makeText(
                activity,
                "Fixed tactical matrix generated",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnShareMatrix.setOnClickListener {

            try {

                val bitmap =
                    latestBitmapProvider()
                        ?: return@setOnClickListener

                val file =
                    java.io.File(
                        activity.cacheDir,
                        "tactical_matrix.png"
                    )

                val fos =
                    java.io.FileOutputStream(file)

                bitmap.compress(
                    android.graphics.Bitmap
                        .CompressFormat.PNG,
                    100,
                    fos
                )

                fos.flush()
                fos.close()

                val uri =
                    androidx.core.content.FileProvider
                        .getUriForFile(
                            activity,
                            activity.packageName +
                            ".provider",
                            file
                        )

                val intent =
                    Intent(
                        Intent.ACTION_SEND
                    ).apply {

                        type = "image/png"

                        putExtra(
                            Intent.EXTRA_STREAM,
                            uri
                        )

                        addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

                activity.startActivity(
                    Intent.createChooser(
                        intent,
                        "SHARE TACTICAL MATRIX"
                    )
                )

            } catch (e: Exception) {

                Toast.makeText(
                    activity,
                    "Matrix share failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        layout.addView(btnSecure)
        layout.addView(btnCreateMatrix)
        layout.addView(btnShareMatrix)
        layout.addView(ivMatrixPreview)

        AlertDialog.Builder(activity)
            .setTitle("INITIATE COMMN")
            .setView(layout)
            .show()
    }


}
