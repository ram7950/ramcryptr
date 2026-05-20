package com.rambo.ramcryptr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class WelcomeActivity : AppCompatActivity() {

    private lateinit var introPlayer: ExoPlayer

    private var latestMatrixBitmap:
        android.graphics.Bitmap? = null


    private val importMatrixLauncher =
        registerForActivityResult(
            androidx.activity.result.contract
                .ActivityResultContracts
                .GetContent()
        ) { uri ->

            if (uri != null) {

                TnetRecovery.recoverFromMatrixUri(
                    activity = this,
                    uri = uri,

                    onSuccess = {

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            )
                        )

                        finish()
                    }
                )
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        val bgVideo =
            findViewById<PlayerView>(R.id.bgVideo)

        val playerView =
            findViewById<PlayerView>(R.id.introVideo)

        introPlayer =
            ExoPlayer.Builder(this).build()

        bgVideo.player = introPlayer

        playerView.player = introPlayer

        val mediaItem =
            MediaItem.fromUri(
                "android.resource://$packageName/${R.raw.intro}"
            )

        introPlayer.setMediaItem(mediaItem)

        introPlayer.prepare()

        introPlayer.play()

        introPlayer.addListener(
            object : androidx.media3.common.Player.Listener {

                override fun onPlaybackStateChanged(
                    playbackState: Int
                ) {

                    if (
                        playbackState ==
                        androidx.media3.common.Player.STATE_ENDED
                    ) {

                        findViewById<android.view.View>(
                            R.id.introVideo
                        ).animate()
                            .alpha(0f)
                            .setDuration(500)
                            .start()

                        findViewById<android.view.View>(
                            R.id.bgVideo
                        ).animate()
                            .alpha(0f)
                            .setDuration(500)
                            .start()

                        findViewById<android.view.View>(
                            R.id.welcomeContent
                        ).animate()
                            .alpha(1f)
                            .setDuration(500)
                            .start()
                    }
                }
            }
        )

        val welcomeContent =
            findViewById<android.view.View>(
                R.id.welcomeContent
            )

        welcomeContent.alpha = 0f

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
                },

                onSuccess = {

                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )

                    finish()
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

                    importMatrixLauncher.launch(
                        "image/png"
                    )
                }
            )
        }
    }
}
