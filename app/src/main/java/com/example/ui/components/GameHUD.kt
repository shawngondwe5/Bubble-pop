package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
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
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameWoodBrown
import com.example.ui.theme.GameWoodDark
import com.example.ui.theme.TitanOneFontFamily

@Composable
fun GameHUD(
    level: Int,
    score: Int,
    highScore: Int,
    shotsRemaining: Int,
    foulCount: Int,
    mode: GameMode,
    combo: Int,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Top bar: Score, Level, High Score, Pause
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Current Score Box
            Box(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                GameWoodBrown,
                                GameWoodDark
                            )
                        )
                    )
                    .border(2.dp, GameGold, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SCORE",
                        fontFamily = FredokaFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameGold
                    )
                    Text(
                        text = "$score",
                        fontFamily = TitanOneFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Mode & Level Badge
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFD54F),
                                    GameGold,
                                    Color(0xFFFF8F00)
                                )
                            )
                        )
                        .border(1.5.dp, Color.White, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (mode == GameMode.CLASSIC) "LEVEL $level" else mode.title.uppercase(),
                        fontFamily = TitanOneFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameWoodDark
                    )
                }

                // Foul dots before ceiling drop
                Row(
                    modifier = Modifier.padding(top = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 0 until 4) {
                        val isFoul = i < foulCount
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isFoul) Color(0xFFFF3D00) else Color(0x66FFFFFF))
                        )
                    }
                }
            }

            // High Score & Pause
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(GameWoodDark)
                        .border(1.5.dp, Color(0xFFBCAAA4), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "BEST",
                            fontFamily = FredokaFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "$highScore",
                            fontFamily = TitanOneFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Pause Button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFB74D),
                                    Color(0xFFF57C00),
                                    Color(0xFFE65100)
                                )
                            )
                        )
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { onPauseClick() }
                        .testTag("pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause Game",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Combo Banner
        AnimatedVisibility(
            visible = combo > 1,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.CenterHorizontally)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFF3D00),
                                Color(0xFFFF9100),
                                Color(0xFFFFD600)
                            )
                        )
                    )
                    .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "COMBO x$combo!",
                    fontFamily = TitanOneFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
