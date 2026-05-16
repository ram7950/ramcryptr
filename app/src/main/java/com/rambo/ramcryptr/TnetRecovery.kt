package com.rambo.ramcryptr

import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

object TnetRecovery {

    fun recoverFromMatrixUri(
        activity: AppCompatActivity,
        uri: Uri,
        onChannelPatched: () -> Unit = {}
    ) {

        try {

            val stream =
                activity.contentResolver
                    .openInputStream(uri)

            val bitmap =
                android.graphics.BitmapFactory
                    .decodeStream(stream)

            stream?.close()

            val matrix =
                MatrixBitmapDecoder
                    .decode(bitmap)

            val recoveredChannel =
                MatrixRecoveryPipeline
                    .recoverChannel(matrix)

            if (recoveredChannel != null) {

                AlertDialog.Builder(activity)

                    .setTitle(
                        "FOUND CHANNEL"
                    )

                    .setMessage(
                        "Found channel: " +
                        recoveredChannel.channelName +
                        "\n\nWanna patch in?"
                    )

                    .setNegativeButton(
                        "NO THANKS",
                        null
                    )

                    .setPositiveButton(
                        "PATCH IN"
                    ) { _, _ ->

                        ChannelStorage.saveChannel(
                            activity,
                            recoveredChannel
                        )

                        onChannelPatched()

                        Toast.makeText(
                            activity,
                            "Channel patched in",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    .show()

            } else {

                Toast.makeText(
                    activity,
                    "Channel recovery failed",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (_: Exception) {

            Toast.makeText(
                activity,
                "Matrix decode failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
