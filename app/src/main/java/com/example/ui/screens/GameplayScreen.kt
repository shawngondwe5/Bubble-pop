package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BubbleColor
import com.example.model.FallingBubble
import com.example.model.FloatingBanner
import com.example.model.GameMode
import com.example.model.GridBubble
import com.example.model.HexCoord
import com.example.model.Particle
import com.example.model.Projectile
import com.example.model.ShockwaveRing
import com.example.ui.components.GameCanvas
import com.example.ui.components.GameHUD
import com.example.ui.components.MascotWidget
import com.example.ui.components.NextBubbleSlot
import com.example.ui.components.PurpleStitchedRibbon
import com.example.ui.components.WoodWheelPedestal
import com.example.ui.theme.FredokaFontFamily

@Composable
fun GameplayScreen(
    level: Int,
    score: Int,
    highScore: Int,
    shotsRemaining: Int,
    foulCount: Int,
    mode: GameMode,
    combo: Int,
    currentBubble: BubbleColor,
    nextBubble: BubbleColor,
    grid: Map<HexCoord, GridBubble>,
    projectile: Projectile?,
    aimTrajectory: List<Offset>,
    fallingBubbles: List<FallingBubble>,
    particles: List<Particle>,
    shockwaves: List<ShockwaveRing>,
    floatingBanners: List<FloatingBanner>,
    mascotCheering: Boolean,
    bubbleRadius: Float,
    getHexCenter: (HexCoord) -> Offset,
    onAim: (touchX: Float, touchY: Float) -> Unit,
    onReleaseShoot: () -> Unit,
    onSwitchBubbles: () -> Unit,
    onGeometryChanged: (width: Float, height: Float) -> Unit,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onPauseClick() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("gameplay_screen")
    ) {
        // 1. Farm Landscape Background
        Image(
            painter = painterResource(id = R.drawable.img_farm_landscape),
            contentDescription = "Farm Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33000000),
                            Color.Transparent,
                            Color(0x22000000)
                        )
                    )
                )
        )

        // 2. Main Bubble Shooter Canvas
        GameCanvas(
            grid = grid,
            projectile = projectile,
            aimTrajectory = aimTrajectory,
            fallingBubbles = fallingBubbles,
            particles = particles,
            shockwaves = shockwaves,
            floatingBanners = floatingBanners,
            bubbleRadius = bubbleRadius,
            getHexCenter = getHexCenter,
            onAim = onAim,
            onReleaseShoot = onReleaseShoot,
            onGeometryChanged = onGeometryChanged,
            modifier = Modifier.fillMaxSize()
        )

        // 3. UI Layer: Top HUD and Bottom Cannon Controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top HUD
            GameHUD(
                level = level,
                score = score,
                highScore = highScore,
                shotsRemaining = shotsRemaining,
                foulCount = foulCount,
                mode = mode,
                combo = combo,
                onPauseClick = onPauseClick
            )

            // Initial Drag & Aim Helper Hint (visible at start of level)
            if (score == 0 && shotsRemaining >= 20) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 24.dp)
                        .background(
                            Color(0xCC000000),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🎯 Drag to aim trajectory • Release to shoot!",
                        fontFamily = FredokaFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Bottom Area: Next Bubble Slot, Ship-Wheel Cannon, Hen Mascot, Purple Ribbon
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Left: Next Bubble Preview & Swap Button
                    NextBubbleSlot(
                        nextBubble = nextBubble,
                        onSwitch = onSwitchBubbles,
                        modifier = Modifier.testTag("switch_bubble_button")
                    )

                    // Center: Ship-Wheel Cannon with current bubble and shots badge
                    WoodWheelPedestal(
                        currentBubble = currentBubble,
                        shotsRemaining = shotsRemaining,
                        onClick = onSwitchBubbles,
                        modifier = Modifier.testTag("cannon_launcher")
                    )

                    // Right: Cheerful Hen Mascot
                    MascotWidget(
                        isCheering = mascotCheering,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Curved Purple Stitched Ribbon
                PurpleStitchedRibbon(title = "Shoot & Match!")
            }
        }
    }
}
