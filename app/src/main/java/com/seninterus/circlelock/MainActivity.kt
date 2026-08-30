package com.seninterus.circlelock

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.seninterus.circlelock.ui.theme.BackgroundDark
import com.seninterus.circlelock.util.AudioManager
import com.seninterus.circlelock.util.AppNotificationManager

class MainActivity : ComponentActivity() {

    private lateinit var insetsController: WindowInsetsControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge setup
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // Initialize insets controller
        insetsController = WindowInsetsControllerCompat(window, window.decorView)

        hideSystemBars()

        AudioManager.init(this)
        AppNotificationManager.createChannel(this)
        AppNotificationManager.scheduleDailyReminder(this)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                CircleLockApp()
                }
            }
        }
    }

    private fun hideSystemBars() {
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioManager.release()
    }
}
