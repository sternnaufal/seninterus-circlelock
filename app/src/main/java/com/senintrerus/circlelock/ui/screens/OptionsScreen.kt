package com.senintrerus.circlelock.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.senintrerus.circlelock.ui.theme.BackgroundDark
import com.senintrerus.circlelock.ui.theme.ErrorRed
import com.senintrerus.circlelock.ui.theme.SuccessGreen
import com.senintrerus.circlelock.ui.theme.SurfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("circle_lock_prefs", Context.MODE_PRIVATE) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("OPTIONS", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            OptionItem("Sound Effects", true)
            OptionItem("Vibration", true)
            OptionItem("Haptic Feedback", true)
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    sharedPrefs.edit().clear().apply()
                    context.getSharedPreferences("circle_lock_stats", Context.MODE_PRIVATE).edit().clear().apply()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("RESET ALL PROGRESS", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OptionItem(title: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceDark).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        Switch(checked = checked, onCheckedChange = { checked = it }, colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen))
    }
}
