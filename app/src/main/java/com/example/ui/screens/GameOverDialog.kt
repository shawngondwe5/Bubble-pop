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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
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
import com.example.model.GameMode
import com.example.ui.components.JellyButton
import com.example.ui.components.MascotWidget
import com.example.ui.components.WoodFrameCard
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameWoodDark
import com.example.ui.theme.TitanOneFontFamily

@Composable
fun GameOverDialog(
    score: Int,
    highScore: Int,
    isNewHigh: Boolean,
    mode: GameMode,
    bubblesPopped: Int,
    maxCombo: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onHome() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .testTag("game_over_dialog"),
        contentAlignment = Alignment.Center
    ) {
        WoodFrameCard(
            title = "GAME OVER",
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // New High Score Banner
            if (isNewHigh) {
                Box(
                    modifier = Modifier
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFFD54F),
                                    GameGold,
                                    Color(0xFFFF6F00)
                                )
                            )
                        )
                        .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEW HIGH SCORE!",
                            fontFamily = TitanOneFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameWoodDark
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Mascot celebrating or cheering
            MascotWidget(
                isCheering = isNewHigh,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Score breakdown card
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
                    ScoreRow(label = "FINAL SCORE", value = "$score", valueColor = GameGold, isBig = true)
                    ScoreRow(label = "BEST (${mode.title.uppercase()})", value = "$highScore", valueColor = Color.White)
                    ScoreRow(label = "BUBBLES POPPED", value = "$bubblesPopped", valueColor = Color(0xFF81D4FA))
                    ScoreRow(label = "MAX COMBO", value = "x$maxCombo", valueColor = Color(0xFFFFAB91))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Play Again Button
            JellyButton(
                text = "PLAY AGAIN",
                onClick = onPlayAgain,
                icon = Icons.Default.Replay,
                primaryColor = Color(0xFF43A047),
                darkColor = Color(0xFF1B5E20),
                lightColor = Color(0xFFA5D6A7),
                testTag = "play_again_button",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Return to Home Button
            JellyButton(
                text = "HOME",
                onClick = onHome,
                icon = Icons.Default.Home,
                primaryColor = Color(0xFF1E88E5),
                darkColor = Color(0xFF0D47A1),
                lightColor = Color(0xFF90CAF9),
                testTag = "game_over_home_button",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ScoreRow(
    label: String,
    value: String,
    valueColor: Color,
    isBig: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = FredokaFontFamily,
            fontSize = if (isBig) 13.sp else 12.sp,
            color = Color(0xFFB0BEC5)
        )
        Text(
            text = value,
            fontFamily = TitanOneFontFamily,
            fontSize = if (isBig) 22.sp else 16.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
