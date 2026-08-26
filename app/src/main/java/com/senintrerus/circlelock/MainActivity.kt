package com.senintrerus.circlelock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.senintrerus.circlelock.ui.theme.BackgroundDark
import com.senintrerus.circlelock.util.AudioManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AudioManager.init(this)
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

    override fun onDestroy() {
        super.onDestroy()
        AudioManager.release()
    }
}
