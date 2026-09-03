package com.seninterus.circlelock.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seninterus.circlelock.ui.theme.*
import com.seninterus.circlelock.util.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("RESET ALL PROGRESS?", fontWeight = FontWeight.Bold) },
            text = { Text("This action cannot be undone. All your progress, currency, skins, and animations will be permanently deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        context.getSharedPreferences("circle_lock_prefs", Context.MODE_PRIVATE).edit().clear().commit()
                        context.getSharedPreferences("circle_lock_stats", Context.MODE_PRIVATE).edit().clear().commit()
                        context.getSharedPreferences("circle_lock_settings", Context.MODE_PRIVATE).edit().clear().commit()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("RESET", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("CANCEL")
                }
            },
            containerColor = SurfaceDark,
            titleContentColor = ErrorRed,
            textContentColor = Color.White.copy(alpha = 0.8f)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("OPTIONS", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OptionItem(
                title = "Sound Effects",
                subtitle = "Toggle game sounds",
                initialValue = SettingsManager.isSoundEnabled(context),
                onChanged = { SettingsManager.setSoundEnabled(context, it) }
            )
            OptionItem(
                title = "Music",
                subtitle = "Toggle background music",
                initialValue = SettingsManager.isMusicEnabled(context),
                onChanged = { SettingsManager.setMusicEnabled(context, it) }
            )
            OptionItem(
                title = "Vibration",
                subtitle = "Vibrate on events",
                initialValue = SettingsManager.isVibrationEnabled(context),
                onChanged = { SettingsManager.setVibrationEnabled(context, it) }
            )
            OptionItem(
                title = "Haptic Feedback",
                subtitle = "Snap alignment feedback",
                initialValue = SettingsManager.isHapticEnabled(context),
                onChanged = { SettingsManager.setHapticEnabled(context, it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ErrorRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("DANGER ZONE", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.15f), contentColor = ErrorRed),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("RESET ALL PROGRESS", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OptionItem(title: String, subtitle: String, initialValue: Boolean, onChanged: (Boolean) -> Unit) {
    var checked by remember { mutableStateOf(initialValue) }
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                Text(subtitle, color = TextDim, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onChanged(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SuccessGreen,
                    checkedTrackColor = SuccessGreen.copy(alpha = 0.2f),
                    uncheckedThumbColor = TextDim,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.08f)
                )
            )
        }
    }
}
