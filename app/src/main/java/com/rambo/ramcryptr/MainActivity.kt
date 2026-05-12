package com.rambo.ramcryptr

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var latestMatrixBitmap:
        android.graphics.Bitmap? = null


    private val PICK_ENCODE_FILE = 201
    private val PICK_DECODE_FILE = 202

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ChannelManager.initialize(this)
        setContentView(R.layout.activity_main)

        // 🔥 STEP 2: Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        // 🔥 Notification setup (FORCE SHOW for testing)
        NotificationHelper.createChannel(this)
        NotificationHelper.ensurePersistent(this)

        handleIncomingIntent(intent)

        val smartSwitch =
            findViewById<Switch>(R.id.switchSmartDecode)

        val input = findViewById<EditText>(R.id.editText)
        val encodeBtn = findViewById<Button>(R.id.btnEncode)
        val decodeBtn = findViewById<Button>(R.id.btnDecode)

        val btnTnetPanel =
            findViewById<Button>(R.id.btnTnetPanel)

        val tnetContainer =
            findViewById<LinearLayout>(R.id.tnetContainer)

        val btnInitiateCommn =
            findViewById<Button>(R.id.btnInitiateCommn)

        val btnPatchIn =
            findViewById<Button>(R.id.btnPatchIn)

        val btnRefreshChannels =
            findViewById<Button>(R.id.btnRefreshChannels)

        val channelListContainer =
            findViewById<LinearLayout>(R.id.channelListContainer)

        val tvEmptyChannels =
            findViewById<TextView>(R.id.tvEmptyChannels)

        renderChannels()

        smartSwitch.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {

                startActivity(
                    Intent(
                        this,
                        WelcomeActivity::class.java
                    )
                )

                smartSwitch.isChecked = false
            }
        }

        encodeBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            input.setText(
TextCrypto.encrypt(
    text,
    CryptoMasterProvider.getMaster(this)
)
)
        }

        decodeBtn.setOnClickListener {
            val text = input.text.toString()

            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!text.startsWith("AES256::")) {
                Toast.makeText(this, "Paglu 😏 ye text encoded nahi hai", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                input.setText(
TextCrypto.decrypt(
    text,
    CryptoMasterProvider.getMaster(this)
)
)
            } catch (e: Exception) {
                Toast.makeText(this, "Decode failed", Toast.LENGTH_SHORT).show()
            }
        }

        encodeBtn.setOnLongClickListener {
            pickFile(PICK_ENCODE_FILE)
            true
        }

        decodeBtn.setOnLongClickListener {
            pickFile(PICK_DECODE_FILE)
            true
        }



        btnRefreshChannels.setOnClickListener {

            Toast.makeText(
                this,
                "Refreshing tactical panel...",
                Toast.LENGTH_SHORT
            ).show()

            window.decorView.postDelayed({

                try {

                    if (
                        !isFinishing &&
                        !isDestroyed
                    ) {

                        renderChannels()
                    }

                } catch (_: Exception) {

                    Toast.makeText(
                        this,
                        "Refresh failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }, 150)
        }

        btnInitiateCommn.setOnClickListener {

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(40, 30, 40, 10)
            }

            val etChannelName = EditText(this).apply {
                hint = "Enter Channel Name"
            }

            val tvChannelId = TextView(this).apply {
                text = "Channel ID not generated"
            }

            val btnGenerate = Button(this).apply {
                text = "Generate Channel ID"
            }

            val btnSecure = Button(this).apply {
                text = "SECURE 🔐"
            }

            val btnCreateMatrix = Button(this).apply {
                text = "CREATE KEY MATRIX"
                visibility = android.view.View.GONE
            }

            val btnShareMatrix = Button(this).apply {
                text = "SHARE MATRIX"
                visibility = android.view.View.GONE
            }

            
            val ivMatrixPreview =
                ImageView(this).apply {

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
                        this,
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
                    this,
                    channel
                )

                ChannelManager.setActiveChannel(
                    this,
                    channel
                )

                btnCreateMatrix.visibility =
                    android.view.View.VISIBLE

                Toast.makeText(
                    this,
                    "Secure channel created",
                    Toast.LENGTH_SHORT
                ).show()
            }

            btnCreateMatrix.setOnClickListener {

                val matrixSeed =
                    etChannelName.text.toString() +
                    generatedId

                val matrixBuilder =
                    StringBuilder()

                val gridSize = 64

                val fixedSize =
                    gridSize * gridSize

                for (i in 0 until fixedSize) {

                    val row =
                        i / gridSize

                    val col =
                        i % gridSize

                    val isFinder =
                        (
                            (row < 4 && col < 4) ||
                            (row < 4 && col > 13) ||
                            (row > 13 && col < 4)
                        )

                    val isReserved =
                        (
                            row == 8 ||
                            col == 8
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

                        val payloadIndex =
                            i - 1

                        val bit =
                            if (
                                payloadIndex >= 0 &&
                                payloadIndex < payloadBits.length
                            ) {

                                payloadBits[
                                    payloadIndex
                                ]

                            } else {

                                val random =
                                    java.util.Random(
                                        (
                                            generatedId.hashCode()
                                                .toLong() shl 32
                                        ) xor
                                        matrixSeed.hashCode()
                                            .toLong() xor
                                        i.toLong()
                                    )

                                if (
                                    random.nextBoolean()
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

                latestMatrixBitmap =
                    MatrixBitmapGenerator
                        .generate(
                            finalMatrix
                        )

                ivMatrixPreview.setImageBitmap(
                    latestMatrixBitmap
                )

                ivMatrixPreview.visibility =
                    android.view.View.VISIBLE

                btnShareMatrix.visibility =
                    android.view.View.VISIBLE

                Toast.makeText(
                    this,
                    "Fixed tactical matrix generated",
                    Toast.LENGTH_SHORT
                ).show()
            }

            btnShareMatrix.setOnClickListener {

                try {

                    val bitmap =
                        latestMatrixBitmap
                            ?: return@setOnClickListener

                    val file =
                        java.io.File(
                            cacheDir,
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
                                this,
                                packageName +
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

                    startActivity(
                        Intent.createChooser(
                            intent,
                            "SHARE TACTICAL MATRIX"
                        )
                    )

                } catch (e: Exception) {

                    Toast.makeText(
                        this,
                        "Matrix share failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            layout.addView(etChannelName)
            layout.addView(btnGenerate)
            layout.addView(tvChannelId)
            layout.addView(btnSecure)
            layout.addView(btnCreateMatrix)
            layout.addView(btnShareMatrix)
            layout.addView(ivMatrixPreview)

            AlertDialog.Builder(this)
                .setTitle("INITIATE COMMN")
                .setView(layout)
                .show()
        }

        btnPatchIn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    WelcomeActivity::class.java
                )
            )
        }

        btnTnetPanel.setOnClickListener {
            val input = EditText(this)

            AlertDialog.Builder(this)
                .setTitle("T-NET ACCESS")
                .setMessage("Enter access password")
                .setView(input)

                .setPositiveButton("Proceed") { _, _ ->

                    val pw =
                        input.text.toString()

                    if (pw == "majhkhali@18") {

                        tnetContainer.visibility =
                            LinearLayout.VISIBLE

                        Toast.makeText(
                            this,
                            "T-NET unlocked",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            this,
                            "Access denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                .show()

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }


    private fun renderChannels() {

        val channelListContainer =
            findViewById<LinearLayout>(
                R.id.channelListContainer
            )

        val tvEmptyChannels = TextView(this).apply {

            text = "NO SECURE CHANNELS DETECTED"

            textSize = 14f

            setPadding(24, 24, 24, 24)

            gravity =
                android.view.Gravity.CENTER

            setTextColor(
                android.graphics.Color.GRAY
            )
        }

        channelListContainer.removeAllViews()

        val channels =
            ChannelManager.getAllChannels(this)

        if (channels.isEmpty()) {


            channelListContainer.addView(
                tvEmptyChannels
            )

            return
        }


        channels.forEachIndexed { index, channel ->

            val row = LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                setPadding(8, 16, 8, 16)
            }

            val sno = TextView(this).apply {
                layoutParams =
                    LinearLayout.LayoutParams(
                        80,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                text = "${index + 1}"
                setTextColor(
                    android.graphics.Color.WHITE
                )
            }

            val name = TextView(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )

                text = channel.channelName

                setTextColor(
                    android.graphics.Color.WHITE
                )
            }

            val inception = TextView(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        260,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                text =
                    java.text.SimpleDateFormat(
                        "dd-MM-yy\nHH:mm:ss",
                        java.util.Locale.getDefault()
                    ).format(
                        java.util.Date(
                            channel.createdAt
                        )
                    )

                setTextColor(
                    android.graphics.Color.LTGRAY
                )
            }

            val action = LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        160,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            }

            val stateView = TextView(this).apply {

                val active =
                    ChannelManager
                        .getActiveChannel()

                if (
                    active?.channelId ==
                    channel.channelId
                ) {

                    text = "ACTIVE"

                    setTextColor(
                        android.graphics.Color.GREEN
                    )

                } else {

                    text = "STANDBY"

                    setTextColor(
                        android.graphics.Color.LTGRAY
                    )
                }
            }

            action.addView(stateView)

            val deleteView = TextView(this).apply {

                text = "DELETE"

                textSize = 11f

                setPadding(0, 10, 0, 0)

                setTextColor(
                    android.graphics.Color.RED
                )

                setOnClickListener {

                    val active =
                        ChannelManager
                            .getActiveChannel()

                    if (
                        active?.channelId ==
                        channel.channelId
                    ) {

                        AlertDialog.Builder(
                            this@MainActivity
                        )

                            .setTitle(
                                "❗‼️Alert‼️❗"
                            )

                            .setMessage(

                                "Cannot delete Active Channel," +

                                " Change the channel first.\n\n" +

                                "__________________________\n\n" +

                                "Active channel ko delete nhi " +

                                "kar sakte, Pahle channel " +

                                "change karo."
                            )

                            .setPositiveButton(
                                "OK",
                                null
                            )

                            .show()

                        return@setOnClickListener
                    }

                    AlertDialog.Builder(
                        this@MainActivity
                    )

                        .setTitle(
                            "DELETE CHANNEL"
                        )

                        .setMessage(
                            "Delete this secure channel?"
                        )

                        .setPositiveButton(
                            "DELETE"
                        ) { _, _ ->

                            ChannelManager
                                .deleteChannel(
                                    this@MainActivity,
                                    channel
                                )

                            window.decorView.postDelayed({

                                if (
                                    !isFinishing &&
                                    !isDestroyed
                                ) {

                                    try {

                                        renderChannels()

                                        channelListContainer
                                            .invalidate()

                                        channelListContainer
                                            .requestLayout()

                                    } catch (_: Exception) {
                                    }
                                }

                            }, 250)

                            Toast.makeText(
                                this@MainActivity,
                                "Channel deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        .setNegativeButton(
                            "CANCEL",
                            null
                        )

                        .show()
                }
            }

            action.addView(deleteView)

            row.addView(sno)
            row.addView(name)
            row.addView(inception)
            row.addView(action)

            row.setOnClickListener {

                AlertDialog.Builder(this)

                    .setTitle("SWITCH CHANNEL")

                    .setMessage(
                        "Switch to this secure channel?\n\n" +
                        "Note: It will apply after restart."
                    )

                    .setPositiveButton("SWITCH") { _, _ ->

                        ChannelManager.setActiveChannel(
                            this,
                            channel
                        )

                        AlertDialog.Builder(this)

                            .setTitle("RESTART REQUIRED")

                            .setMessage(
                                "App is going to restart..."
                            )

                            .setCancelable(false)

                            .show()

                        android.os.Handler(
                            mainLooper
                        ).postDelayed({

                            recreate()

                        }, 2000)
                    }

                    .setNegativeButton(
                        "DISMISS",
                        null
                    )

                    .show()
            }

            channelListContainer.addView(row)
        }
    }


    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        when (intent.action) {

            Intent.ACTION_SEND -> {
                val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                uri?.let { showEncodeDialog(it) }
            }

            Intent.ACTION_VIEW -> {
                val uri = intent.data
                uri?.let { showDecodeDialog(it) }
            }
        }
    }

    private fun showEncodeDialog(uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Encode File")
            .setMessage("Do you want to encode this file?")
            .setPositiveButton("Encode") { _, _ ->
                val i = Intent(this, FileEncryptActivity::class.java)
                i.setData(uri)
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(i)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDecodeDialog(uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Decode File")
            .setMessage("Encoded file detected. Decode it?")
            .setPositiveButton("Decode") { _, _ ->
                val i = Intent(this, FileDecryptActivity::class.java)
                i.setData(uri)
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(i)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun pickFile(code: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK || data == null) return

        val uri = data.data ?: return

        when (requestCode) {
            PICK_ENCODE_FILE -> showEncodeDialog(uri)
            PICK_DECODE_FILE -> showDecodeDialog(uri)
        }
    }
}
