package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import com.example.model.BubbleColor
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GameGold
import com.example.ui.theme.GamePurpleRibbon
import com.example.ui.theme.GameWoodBrown
import com.example.ui.theme.GameWoodDark
import com.example.ui.theme.SurfaceCream
import com.example.ui.theme.TitanOneFontFamily

/**
 * 3D Jelly Game Button with bevel highlight, bottom shadow, and press bounce.
 */
@Composable
fun JellyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "jelly_button",
    primaryColor: Color = Color(0xFF43A047),
    darkColor: Color = Color(0xFF1B5E20),
    lightColor: Color = Color(0xFFA5D6A7),
    icon: ImageVector? = null,
    minHeight: Dp = 56.dp,
    fontSize: Int = 20
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(100),
        label = "btn_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(6.dp, shape = RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(darkColor)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .testTag(testTag)
            .padding(bottom = 5.dp) // creates 3D depth lip
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            lightColor,
                            primaryColor,
                            primaryColor
                        )
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(26.dp)
                            .padding(end = 8.dp)
                    )
                }

                Box {
                    // Dark drop shadow text
                    Text(
                        text = text,
                        fontFamily = TitanOneFontFamily,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0x88000000),
                        modifier = Modifier.offset(y = 2.dp)
                    )
                    // White main text
                    Text(
                        text = text,
                        fontFamily = TitanOneFontFamily,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Wooden framed card with golden borders and rustic game aesthetic.
 */
@Composable
fun WoodFrameCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(GameWoodDark)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GameWoodBrown,
                            GameWoodDark
                        )
                    )
                )
                .border(2.dp, GameGold, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (title != null) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-6).dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFD54F),
                                        GameGold,
                                        Color(0xFFFF8F00)
                                    )
                                )
                            )
                            .border(1.5.dp, Color.White, RoundedCornerShape(16.dp))
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = title,
                            fontFamily = TitanOneFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameWoodDark
                        )
                    }
                }
                content()
            }
        }
    }
}

/**
 * Animated Hen and Chick mascot view inspired by the reference image.
 */
@Composable
fun MascotWidget(
    isCheering: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_idle")
    val idleBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_bob"
    )

    val cheerScale by animateFloatAsState(
        targetValue = if (isCheering) 1.15f else 1.0f,
        animationSpec = tween(200),
        label = "cheer_scale"
    )

    Box(
        modifier = modifier
            .offset(y = (-idleBob).dp)
            .scale(cheerScale),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_hen_mascot),
            contentDescription = "Farm Hen Mascot",
            modifier = Modifier
                .size(110.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
        )

        if (isCheering) {
            // Heart or star popping over mascot
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = GameGold,
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = 24.dp, y = (-85).dp)
            )
        }
    }
}

/**
 * Wooden Pedestal and Ship-Wheel cannon launcher matching the reference image.
 * Holds the current shooting bubble and shows remaining shots badge ("20").
 */
@Composable
fun WoodWheelPedestal(
    currentBubble: BubbleColor,
    shotsRemaining: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Pedestal Wheel Frame
        Box(
            modifier = Modifier
                .size(86.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            GameWoodBrown,
                            GameWoodDark
                        )
                    )
                )
                .border(4.dp, GameGold, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(54.dp)) {
                drawGlossyBubbleByColor(
                    color = currentBubble,
                    centerX = size.width / 2f,
                    centerY = size.height / 2f,
                    radiusPx = size.width / 2f
                )
            }
        }

        // Glowing Counter Badge ("20")
        Box(
            modifier = Modifier
                .offset(y = (-14).dp)
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFE082),
                            GameGold,
                            Color(0xFFFF8F00)
                        )
                    )
                )
                .border(2.dp, GameWoodDark, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$shotsRemaining",
                fontFamily = TitanOneFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GameWoodDark
            )
        }
    }
}

/**
 * Next bubble preview slot with interactive color switch / swap button.
 */
@Composable
fun NextBubbleSlot(
    nextBubble: BubbleColor,
    onSwitch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onSwitch() }
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF8D6E63),
                            Color(0xFF4E342E)
                        )
                    )
                )
                .border(3.dp, Color(0xFFFFB300), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(40.dp)) {
                drawGlossyBubbleByColor(
                    color = nextBubble,
                    centerX = size.width / 2f,
                    centerY = size.height / 2f,
                    radiusPx = size.width / 2f
                )
            }
        }

        // Swap Button Pill
        Box(
            modifier = Modifier
                .offset(y = (-8).dp)
                .shadow(3.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1E88E5))
                .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap Bubbles",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "SWAP",
                    fontFamily = TitanOneFontFamily,
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Purple stitched cartoon ribbon banner at bottom ("Shoot & Match!")
 * directly reproducing the reference image.
 */
@Composable
fun PurpleStitchedRibbon(
    title: String = "Shoot & Match!",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFAB47BC),
                        GamePurpleRibbon,
                        Color(0xFF4A148C)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box {
                Text(
                    text = title,
                    fontFamily = TitanOneFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF311B92),
                    modifier = Modifier.offset(y = 2.dp)
                )
                Text(
                    text = title,
                    fontFamily = TitanOneFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
