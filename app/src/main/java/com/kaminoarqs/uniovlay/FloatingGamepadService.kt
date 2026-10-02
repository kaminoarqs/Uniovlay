package com.kaminoarqs.uniovlay

import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button

class FloatingGamepadService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: Button

    override fun onBind(intent: android.content.Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // 1. Generate the button container
        floatingButton = Button(this).apply {
            text = "A"
            setBackgroundColor(android.graphics.Color.argb(150, 0, 0, 255)) // Semi-transparent blue
            setTextColor(android.graphics.Color.WHITE)
        }

        // 2. Set layout parameters to draw over external applications globally
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        // 3. Track thumb movements to execute real-time repositioning
        floatingButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingButton, params)
                        return true
                    }
                }
                return false
            }
        })

        // 4. Render the layout directly to the active system canvas
        windowManager.addView(floatingButton, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingButton.isInitialized) windowManager.removeView(floatingButton)
    }
}
