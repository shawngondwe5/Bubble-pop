package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JellyButton
import com.example.ui.components.WoodFrameCard
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GameGold
import com.example.ui.theme.TitanOneFontFamily

@Composable
fun PauseDialog(
    score: Int,
    soundEnabled: Boolean,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onResume() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .testTag("pause_dialog"),
        contentAlignment = Alignment.Center
    ) {
        WoodFrameCard(
            title = "PAUSED",
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Score reminder
            Text(
                text = "CURRENT SCORE",
                fontFamily = FredokaFontFamily,
                fontSize = 12.sp,
                color = GameGold
            )
            Text(
                text = "$score",
                fontFamily = TitanOneFontFamily,
                fontSize = 28.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Resume Button
            JellyButton(
                text = "RESUME",
                onClick = onResume,
                icon = Icons.Default.PlayArrow,
                primaryColor = Color(0xFF43A047),
                darkColor = Color(0xFF1B5E20),
                lightColor = Color(0xFFA5D6A7),
                testTag = "resume_button",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Restart Button
            JellyButton(
                text = "RESTART",
                onClick = onRestart,
                icon = Icons.Default.Refresh,
                primaryColor = Color(0xFFFFA000),
                darkColor = Color(0xFFE65100),
                lightColor = Color(0xFFFFD54F),
                testTag = "restart_button",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Home Button
            JellyButton(
                text = "HOME",
                onClick = onHome,
                icon = Icons.Default.Home,
                primaryColor = Color(0xFF1E88E5),
                darkColor = Color(0xFF0D47A1),
                lightColor = Color(0xFF90CAF9),
                testTag = "home_button",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Sound Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Toggle Sound",
                        tint = GameGold
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (soundEnabled) "Sound ON" else "Sound OFF",
                    fontFamily = FredokaFontFamily,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}
