package com.rambo.ramcryptr

import android.app.Service
import android.content.*
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.ImageView
import android.os.Handler
import android.os.Looper

class BubbleService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: View
    private lateinit var clipboard: ClipboardManager

    private val handler = Handler(Looper.getMainLooper())

    private var lastText: String = ""
    private var lastShownTime: Long = 0

    override fun onCreate() {
        super.onCreate()

        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        initBubble()
        startClipboardListener()
    }

    // ---------------- BUBBLE UI ----------------

    private fun initBubble() {

        bubbleView = LayoutInflater.from(this)
            .inflate(R.layout.bubble_layout, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 300

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(bubbleView, params)

        val bubble = bubbleView.findViewById<ImageView>(R.id.bubble_icon)

        // 🔹 Drag
        bubble.setOnTouchListener(object : View.OnTouchListener {

            private var initialX = 0
            private var initialY = 0
            private var touchX = 0f
            private var touchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {

                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        touchX = event.rawX
                        touchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - touchX).toInt()
                        params.y = initialY + (event.rawY - touchY).toInt()
                        windowManager.updateViewLayout(bubbleView, params)
                        return true
                    }
                }
                return false
            }
        })

        // 🔹 Click = manual open
        bubble.setOnClickListener {
            openQuickDecode("")
        }
    }

    // ---------------- CLIPBOARD LISTENER ----------------

    private fun startClipboardListener() {

        clipboard.addPrimaryClipChangedListener {

            handler.post {
                handleClipboard()
            }
        }
    }

    private fun handleClipboard() {

        try {
            val clip = clipboard.primaryClip ?: return
            if (clip.itemCount == 0) return

            val text = clip.getItemAt(0).text?.toString() ?: return

            // 🔥 FILTER 1: empty / small junk
            if (text.length < 10) return

            // 🔥 FILTER 2: duplicate ignore
            if (text == lastText) return

            lastText = text

            // 🔥 FILTER 3: time gap (avoid spam)
            val now = System.currentTimeMillis()
            if (now - lastShownTime < 3000) return

            // 🔐 MAIN CHECK
            if (text.startsWith("AES256::")) {

                lastShownTime = now

                openQuickDecode(text)
            }

        } catch (e: Exception) {
            // crash safe
        }
    }

    // ---------------- OPEN DIALOG ----------------

    private fun openQuickDecode(text: String) {

        val intent = Intent(this, QuickDecodeActivity::class.java)
        intent.putExtra("data", text)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    // ---------------- CLEANUP ----------------

    override fun onDestroy() {
        super.onDestroy()
        try {
            windowManager.removeView(bubbleView)
        } catch (_: Exception) {}
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
