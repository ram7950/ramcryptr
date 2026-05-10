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
        NotificationHelper.showPersistent(this)

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
            input.setText(TextCrypto.encrypt(text, "ramcryptr_secret"))
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
                input.setText(TextCrypto.decrypt(text, "ramcryptr_secret"))
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



        btnInitiateCommn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    WelcomeActivity::class.java
                )
            )
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

                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
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
