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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JellyButton
import com.example.ui.components.WoodFrameCard
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GameGold
import com.example.ui.theme.TitanOneFontFamily

@Composable
fun StatsDialog(
    totalBubbles: Int,
    totalGames: Int,
    maxCombo: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .testTag("stats_dialog"),
        contentAlignment = Alignment.Center
    ) {
        WoodFrameCard(
            title = "STATISTICS",
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
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatItem(label = "Total Bubbles Popped", value = "$totalBubbles", color = Color(0xFF81D4FA))
                StatItem(label = "Total Games Played", value = "$totalGames", color = Color(0xFFA5D6A7))
                StatItem(label = "Max Combo Achieved", value = "x$maxCombo", color = GameGold)
            }

            Spacer(modifier = Modifier.height(18.dp))

            JellyButton(
                text = "CLOSE",
                onClick = onClose,
                icon = Icons.Default.Check,
                primaryColor = Color(0xFF1E88E5),
                darkColor = Color(0xFF0D47A1),
                lightColor = Color(0xFF90CAF9),
                testTag = "stats_close_button",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = FredokaFontFamily,
            fontSize = 14.sp,
            color = Color(0xFFCFD8DC)
        )
        Text(
            text = value,
            fontFamily = TitanOneFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
