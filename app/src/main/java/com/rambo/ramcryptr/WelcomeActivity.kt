package com.rambo.ramcryptr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    private var latestMatrixBitmap:
        android.graphics.Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        val btnInitiate =
            findViewById<Button>(R.id.btnInitiate)

        val btnPatchIn =
            findViewById<Button>(R.id.btnPatchIn)

        btnInitiate.setOnClickListener {

            TnetDialogs.showInitiateCommnDialog(

                activity = this,

                latestBitmapProvider = {
                    latestMatrixBitmap
                },

                latestBitmapUpdater = {
                    latestMatrixBitmap = it
                }
            )
        }

        btnPatchIn.setOnClickListener {

            TnetDialogs.showPatchInDialog(

                activity = this,

                onScanClick = {

                    android.widget.Toast.makeText(
                        this,
                        "Scan support coming next",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                },

                onImportClick = {

                    val intent =
                        Intent(Intent.ACTION_GET_CONTENT)

                    intent.type = "image/png"

                    startActivity(
                        Intent.createChooser(
                            intent,
                            "IMPORT KEY MATRIX"
                        )
                    )
                }
            )
        }
    }
}
