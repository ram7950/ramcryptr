package com.rambo.ramcryptr

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.ImageView

class BubbleService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: View

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        bubbleView = LayoutInflater.from(this)
            .inflate(R.layout.bubble_layout, null)

        val params = WindowManager.LayoutParams(
            150,
            150,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 300

        windowManager.addView(bubbleView, params)

        val bubbleIcon = bubbleView.findViewById<ImageView>(R.id.bubble_icon)

        // 🔥 DRAG + CLICK HANDLER
        bubbleIcon.setOnTouchListener(object : View.OnTouchListener {

            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isClick = true

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isClick = true
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - initialTouchX
                        val dy = event.rawY - initialTouchY

                        if (dx > 10 || dy > 10) isClick = false

                        params.x = initialX + dx.toInt()
                        params.y = initialY + dy.toInt()

                        windowManager.updateViewLayout(bubbleView, params)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        if (isClick) {
                            // 🔥 OPEN QUICK DECODE POPUP
                            QuickDecodeDialog.showWithPrefill(
                                applicationContext,
                                ""
                            )
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bubbleView.isInitialized) {
            windowManager.removeView(bubbleView)
        }
    }
}
