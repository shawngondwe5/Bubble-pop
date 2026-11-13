package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GameMode
import com.example.ui.components.JellyButton
import com.example.ui.components.MascotWidget
import com.example.ui.components.PurpleStitchedRibbon
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameWoodDark
import com.example.ui.theme.TitanOneFontFamily

@Composable
fun HomeScreen(
    selectedMode: GameMode,
    highScore: Int,
    soundEnabled: Boolean,
    onSelectMode: (GameMode) -> Unit,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onStatsClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "home_pulse")
    val titleFloat by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_float"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // 1. Fullscreen Farm Meadow Landscape Background
        Image(
            painter = painterResource(id = R.drawable.img_farm_landscape),
            contentDescription = "Farm Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient vignette overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33000000),
                            Color.Transparent,
                            Color(0x55000000)
                        )
                    )
                )
        )

        // Main Home Screen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Actions and High Score Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // High Score Pill
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(GameWoodDark)
                        .border(1.5.dp, GameGold, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GameGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "HIGH SCORE",
                                fontFamily = FredokaFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameGold
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

                // Action buttons: Help, Stats, Sound, Settings
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HomeIconButton(
                        icon = Icons.Default.Help,
                        contentDescription = "How to Play",
                        onClick = onHowToPlayClick,
                        testTag = "home_help_button"
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    HomeIconButton(
                        icon = Icons.Default.Equalizer,
                        contentDescription = "Statistics",
                        onClick = onStatsClick,
                        testTag = "home_stats_button"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    HomeIconButton(
                        icon = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Toggle Sound",
                        onClick = onToggleSound,
                        testTag = "home_sound_toggle_button"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    HomeIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings",
                        onClick = onSettingsClick,
                        testTag = "home_settings_button"
                    )
                }
            }

            // Center area: Animated 3D Game Title & Mascot
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = titleFloat.dp)
            ) {
                // 3D Cartoon Title
                Box(contentAlignment = Alignment.Center) {
                    // Deep 3D drop shadow
                    Text(
                        text = "BUBBLE POP!",
                        fontFamily = TitanOneFontFamily,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.offset(y = 4.dp)
                    )
                    // Bright vibrant text
                    Text(
                        text = "BUBBLE POP!",
                        fontFamily = TitanOneFontFamily,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFEB3B)
                    )
                }

                Text(
                    text = "• Farm Blast Edition •",
                    fontFamily = FredokaFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .shadow(2.dp)
                        .padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Cheerful Hen & Chick Mascot
                MascotWidget(
                    isCheering = false,
                    modifier = Modifier.size(118.dp)
                )
            }

            // Bottom Area: Game Mode Selector & Big Play Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Game Mode Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameMode.values().forEach { mode ->
                        val isSelected = mode == selectedMode
                        val bgColor = if (isSelected) GameGold else Color(0xBB000000)
                        val textColor = if (isSelected) GameWoodDark else Color.White
                        val borderColor = if (isSelected) Color.White else Color(0x66FFFFFF)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor)
                                .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                                .clickable { onSelectMode(mode) }
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                                .testTag("mode_${mode.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = mode.title,
                                    fontFamily = TitanOneFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = mode.subtitle,
                                    fontFamily = FredokaFontFamily,
                                    fontSize = 11.sp,
                                    color = if (isSelected) GameWoodDark else Color(0xFFCFD8DC)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Juicy PLAY Button
                JellyButton(
                    text = "PLAY NOW",
                    onClick = onPlayClick,
                    icon = Icons.Default.PlayArrow,
                    primaryColor = Color(0xFF43A047),
                    darkColor = Color(0xFF1B5E20),
                    lightColor = Color(0xFFA5D6A7),
                    minHeight = 64.dp,
                    fontSize = 24,
                    testTag = "play_button",
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Curved Purple Stitched Ribbon at bottom matching reference
            PurpleStitchedRibbon(title = "Shoot & Match!")
        }
    }
}

@Composable
private fun HomeIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(44.dp)
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
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}
