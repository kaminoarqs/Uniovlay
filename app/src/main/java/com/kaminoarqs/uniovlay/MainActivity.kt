package com.kaminoarqs.uniovlay

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Checks if permission to draw over other apps is missing
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 123)
        } else {
            startFloatingService()
        }
    }

    private fun startFloatingService() {
        startService(Intent(this, FloatingGamepadService::class.java))
        finish() // Closes the background config menu automatically
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && Settings.canDrawOverlays(this)) {
            startFloatingService()
        }
    }
}
