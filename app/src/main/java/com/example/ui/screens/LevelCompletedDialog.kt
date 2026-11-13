package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JellyButton
import com.example.ui.components.MascotWidget
import com.example.ui.components.WoodFrameCard
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameWoodDark
import com.example.ui.theme.TitanOneFontFamily

@Composable
fun LevelCompletedDialog(
    level: Int,
    score: Int,
    highScore: Int,
    onNextLevel: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onHome() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .testTag("level_completed_dialog"),
        contentAlignment = Alignment.Center
    ) {
        WoodFrameCard(
            title = "LEVEL $level CLEARED!",
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 3 Celebratory Golden Stars
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GameGold,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GameGold,
                    modifier = Modifier.size(46.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GameGold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cheerful Mascot
            MascotWidget(
                isCheering = true,
                modifier = Modifier.size(88.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Score card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x44000000))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TOTAL SCORE",
                            fontFamily = FredokaFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFFB0BEC5)
                        )
                        Text(
                            text = "$score",
                            fontFamily = TitanOneFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameGold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "BEST SCORE",
                            fontFamily = FredokaFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFFB0BEC5)
                        )
                        Text(
                            text = "$highScore",
                            fontFamily = TitanOneFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Next Level Button
            JellyButton(
                text = "NEXT LEVEL",
                onClick = onNextLevel,
                icon = Icons.Default.ArrowForward,
                primaryColor = Color(0xFF43A047),
                darkColor = Color(0xFF1B5E20),
                lightColor = Color(0xFFA5D6A7),
                testTag = "next_level_button",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Home Button
            JellyButton(
                text = "HOME",
                onClick = onHome,
                icon = Icons.Default.Home,
                primaryColor = Color(0xFF1E88E5),
                darkColor = Color(0xFF0D47A1),
                lightColor = Color(0xFF90CAF9),
                testTag = "level_complete_home_button",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
