package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FallingBubble
import com.example.model.FloatingBanner
import com.example.model.GridBubble
import com.example.model.HexCoord
import com.example.model.Particle
import com.example.model.Projectile
import com.example.model.ShockwaveRing
import com.example.ui.theme.TitanOneFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameCanvas(
    grid: Map<HexCoord, GridBubble>,
    projectile: Projectile?,
    aimTrajectory: List<Offset>,
    fallingBubbles: List<FallingBubble>,
    particles: List<Particle>,
    shockwaves: List<ShockwaveRing>,
    floatingBanners: List<FloatingBanner>,
    bubbleRadius: Float,
    getHexCenter: (HexCoord) -> Offset,
    onAim: (touchX: Float, touchY: Float) -> Unit,
    onReleaseShoot: () -> Unit,
    onGeometryChanged: (width: Float, height: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("bubble_play_area")
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        LaunchedEffect(widthPx, heightPx) {
            onGeometryChanged(widthPx, heightPx)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onAim(offset.x, offset.y)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            onAim(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            onReleaseShoot()
                        },
                        onDragCancel = {
                            onReleaseShoot()
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        onAim(offset.x, offset.y)
                        onReleaseShoot()
                    }
                }
        ) {
            // 1. Draw Dotted Aiming Trajectory
            if (aimTrajectory.size >= 2) {
                for (i in 0 until aimTrajectory.size - 1) {
                    val p1 = aimTrajectory[i]
                    val p2 = aimTrajectory[i + 1]
                    drawLine(
                        color = Color.White.copy(alpha = 0.85f),
                        start = p1,
                        end = p2,
                        strokeWidth = with(density) { 3.dp.toPx() },
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)
                    )
                }
                // Target dot at the end of trajectory
                val endPoint = aimTrajectory.last()
                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = bubbleRadius * 0.45f,
                    center = endPoint,
                    style = Stroke(width = with(density) { 2.5.dp.toPx() })
                )
            }

            // 2. Draw Danger Line (near bottom)
            val dangerY = heightPx - bubbleRadius * 6f
            drawLine(
                color = Color(0x66FF1744),
                start = Offset(0f, dangerY),
                end = Offset(widthPx, dangerY),
                strokeWidth = with(density) { 2.dp.toPx() },
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
            )

            // 3. Draw Grid Bubbles hanging from ceiling
            grid.values.forEach { gb ->
                val center = getHexCenter(gb.coord)
                drawGlossyBubbleByColor(
                    color = gb.color,
                    centerX = center.x,
                    centerY = center.y,
                    radiusPx = bubbleRadius
                )
            }

            // 4. Draw Flying Projectile
            if (projectile != null && projectile.isActive) {
                // Electric glow ring around flying bubble matching reference image
                drawCircle(
                    color = Color(0xFF80D8FF).copy(alpha = 0.7f),
                    radius = bubbleRadius * 1.35f,
                    center = Offset(projectile.x, projectile.y),
                    style = Stroke(width = with(density) { 3.dp.toPx() })
                )
                drawGlossyBubbleByColor(
                    color = projectile.color,
                    centerX = projectile.x,
                    centerY = projectile.y,
                    radiusPx = bubbleRadius
                )
            }

            // 5. Draw Falling Bubbles (Avalanche detached from ceiling)
            fallingBubbles.forEach { fb ->
                drawGlossyBubbleByColor(
                    color = fb.color,
                    centerX = fb.x,
                    centerY = fb.y,
                    radiusPx = bubbleRadius,
                    alpha = fb.alpha
                )
            }

            // 6. Draw Shockwaves
            shockwaves.forEach { shock ->
                drawCircle(
                    color = shock.color.copy(alpha = shock.alpha),
                    radius = shock.currentRadius,
                    center = Offset(shock.x, shock.y),
                    style = Stroke(width = with(density) { 4.dp.toPx() })
                )
                // Lightning sparks
                val sparkCount = 8
                val angleStep = (2 * PI / sparkCount).toFloat()
                for (i in 0 until sparkCount) {
                    val angle = i * angleStep
                    val startR = shock.currentRadius * 0.85f
                    val endR = shock.currentRadius * 1.18f
                    val x1 = shock.x + cos(angle) * startR
                    val y1 = shock.y + sin(angle) * startR
                    val x2 = shock.x + cos(angle + 0.2f) * endR
                    val y2 = shock.y + sin(angle + 0.2f) * endR
                    drawLine(
                        color = Color.White.copy(alpha = shock.alpha),
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = with(density) { 2.dp.toPx() }
                    )
                }
            }

            // 7. Draw Particles
            particles.forEach { p ->
                if (p.isStar) {
                    drawStar(
                        centerX = p.x,
                        centerY = p.y,
                        points = 4,
                        outerRadius = p.size * 1.4f,
                        innerRadius = p.size * 0.5f,
                        color = p.color.copy(alpha = p.alpha)
                    )
                } else {
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.size,
                        center = Offset(p.x, p.y)
                    )
                }
            }
        }

        // 8. Floating Comic Scores & Banners
        floatingBanners.forEach { banner ->
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (banner.x - 70).toInt().coerceAtLeast(0),
                            y = (banner.y - 30).toInt().coerceAtLeast(0)
                        )
                    }
                    .alpha(banner.alpha)
                    .scale(banner.scale)
            ) {
                Text(
                    text = banner.text,
                    fontFamily = TitanOneFontFamily,
                    fontSize = if (banner.isBigCombo) 24.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723),
                    modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                )
                Text(
                    text = banner.text,
                    fontFamily = TitanOneFontFamily,
                    fontSize = if (banner.isBigCombo) 24.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = banner.color
                )
            }
        }
    }
}
