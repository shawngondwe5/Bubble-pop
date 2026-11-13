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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JellyButton
import com.example.ui.components.WoodFrameCard
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GameGold

@Composable
fun SettingsDialog(
    soundEnabled: Boolean,
    musicEnabled: Boolean,
    vibrationEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleVibration: () -> Unit,
    onResetStats: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .testTag("settings_dialog"),
        contentAlignment = Alignment.Center
    ) {
        WoodFrameCard(
            title = "SETTINGS",
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x44000000))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingSwitchRow(
                    label = "Sound Effects",
                    icon = Icons.Default.VolumeUp,
                    checked = soundEnabled,
                    onCheckedChange = { onToggleSound() },
                    testTag = "setting_sound_switch"
                )

                SettingSwitchRow(
                    label = "Game Music",
                    icon = Icons.Default.MusicNote,
                    checked = musicEnabled,
                    onCheckedChange = { onToggleMusic() },
                    testTag = "setting_music_switch"
                )

                SettingSwitchRow(
                    label = "Vibration Haptics",
                    icon = Icons.Default.Vibration,
                    checked = vibrationEnabled,
                    onCheckedChange = { onToggleVibration() },
                    testTag = "setting_vibration_switch"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset stats
            JellyButton(
                text = "RESET STATS",
                onClick = onResetStats,
                icon = Icons.Default.DeleteOutline,
                primaryColor = Color(0xFFD32F2F),
                darkColor = Color(0xFF8E0000),
                lightColor = Color(0xFFEF9A9A),
                minHeight = 46.dp,
                fontSize = 15,
                testTag = "reset_stats_button",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Done Button
            JellyButton(
                text = "DONE",
                onClick = onClose,
                icon = Icons.Default.Check,
                primaryColor = Color(0xFF43A047),
                darkColor = Color(0xFF1B5E20),
                lightColor = Color(0xFFA5D6A7),
                testTag = "settings_done_button",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GameGold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = label,
                fontFamily = FredokaFontFamily,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = GameGold,
                checkedTrackColor = Color(0xFF43A047),
                uncheckedThumbColor = Color(0xFFB0BEC5),
                uncheckedTrackColor = Color(0xFF37474F)
            )
        )
    }
}
