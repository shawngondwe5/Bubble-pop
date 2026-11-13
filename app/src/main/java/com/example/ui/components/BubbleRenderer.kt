package com.example.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.Bubble
import com.example.model.BubbleColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawGlossyBubbleByColor(
    color: BubbleColor,
    centerX: Float,
    centerY: Float,
    radiusPx: Float,
    alpha: Float = 1f
) {
    drawBubbleBody(
        color = color,
        centerX = centerX,
        centerY = centerY,
        radius = radiusPx,
        alpha = alpha,
        wobblePhase = 0f
    )
}

fun DrawScope.drawGlossyBubble(
    bubble: Bubble,
    centerX: Float,
    centerY: Float,
    radiusPx: Float
) {
    if (bubble.isPopping) {
        val popScale = 1f + (bubble.popProgress * 0.4f)
        val alpha = (1f - bubble.popProgress).coerceIn(0f, 1f)
        drawBubbleBody(
            color = bubble.color,
            centerX = centerX,
            centerY = centerY,
            radius = radiusPx * popScale,
            alpha = alpha,
            wobblePhase = bubble.wobblePhase
        )
        return
    }

    // Gentle wobble bobbing
    val wobbleOffset = sin(bubble.wobblePhase * PI.toFloat() / 180f) * (radiusPx * 0.05f)
    val actualY = centerY + wobbleOffset

    drawBubbleBody(
        color = bubble.color,
        centerX = centerX,
        centerY = actualY,
        radius = radiusPx,
        alpha = 1f,
        wobblePhase = bubble.wobblePhase
    )
}

private fun DrawScope.drawBubbleBody(
    color: BubbleColor,
    centerX: Float,
    centerY: Float,
    radius: Float,
    alpha: Float,
    wobblePhase: Float
) {
    // 1. Ambient Drop Shadow underneath
    drawCircle(
        color = Color(0x33000000),
        radius = radius * 0.95f,
        center = Offset(centerX, centerY + radius * 0.12f)
    )

    // 2. Translucent outer atmospheric glow ring
    drawCircle(
        color = color.lightColor.copy(alpha = 0.45f * alpha),
        radius = radius * 1.06f,
        center = Offset(centerX, centerY)
    )

    // 3. Spherical 3D Radial Gradient Body
    val bodyBrush = Brush.radialGradient(
        colors = listOf(
            color.lightColor.copy(alpha = 0.92f * alpha),
            color.baseColor.copy(alpha = 0.96f * alpha),
            color.darkColor.copy(alpha = 1.0f * alpha)
        ),
        center = Offset(centerX - radius * 0.28f, centerY - radius * 0.28f),
        radius = radius * 1.25f
    )
    drawCircle(
        brush = bodyBrush,
        radius = radius,
        center = Offset(centerX, centerY)
    )

    // 4. Inner Glass Edge Ring
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                color.lightColor.copy(alpha = 0.4f * alpha),
                Color.White.copy(alpha = 0.7f * alpha)
            ),
            center = Offset(centerX, centerY),
            radius = radius
        ),
        radius = radius * 0.98f,
        center = Offset(centerX, centerY),
        style = Stroke(width = radius * 0.08f)
    )

    // 5. Center Emblem / Fruit Motif
    drawFruitEmblem(
        color = color,
        centerX = centerX,
        centerY = centerY,
        radius = radius * 0.46f,
        alpha = alpha
    )

    // 6. Secondary bottom-right soft rim reflection
    drawArc(
        color = Color.White.copy(alpha = 0.35f * alpha),
        startAngle = 35f,
        sweepAngle = 75f,
        useCenter = false,
        topLeft = Offset(centerX - radius * 0.85f, centerY - radius * 0.85f),
        size = Size(radius * 1.7f, radius * 1.7f),
        style = Stroke(width = radius * 0.09f)
    )

    // 7. Specular Top-Left Highlight (Bright Gloss Crescent)
    val highlightCenter = Offset(centerX - radius * 0.36f, centerY - radius * 0.36f)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f * alpha),
                Color.White.copy(alpha = 0.5f * alpha),
                Color.Transparent
            ),
            center = highlightCenter,
            radius = radius * 0.4f
        ),
        topLeft = Offset(highlightCenter.x - radius * 0.32f, highlightCenter.y - radius * 0.22f),
        size = Size(radius * 0.64f, radius * 0.44f)
    )

    // Tiny secondary sparkling dot
    drawCircle(
        color = Color.White.copy(alpha = 0.85f * alpha),
        radius = radius * 0.08f,
        center = Offset(centerX - radius * 0.54f, centerY - radius * 0.12f)
    )
}

private fun DrawScope.drawFruitEmblem(
    color: BubbleColor,
    centerX: Float,
    centerY: Float,
    radius: Float,
    alpha: Float
) {
    when (color) {
        BubbleColor.RED -> {
            // Tomato with leafy green stem
            // Red body
            drawCircle(
                color = Color(0xFFD50000).copy(alpha = 0.9f * alpha),
                radius = radius * 0.88f,
                center = Offset(centerX, centerY + radius * 0.08f)
            )
            // Green Calyx leaves on top
            val stemPath = Path().apply {
                moveTo(centerX, centerY - radius * 0.9f)
                lineTo(centerX - radius * 0.45f, centerY - radius * 0.5f)
                lineTo(centerX - radius * 0.15f, centerY - radius * 0.4f)
                lineTo(centerX, centerY - radius * 0.25f)
                lineTo(centerX + radius * 0.15f, centerY - radius * 0.4f)
                lineTo(centerX + radius * 0.45f, centerY - radius * 0.5f)
                close()
            }
            drawPath(stemPath, Color(0xFF2E7D32).copy(alpha = alpha), style = Fill)
        }
        BubbleColor.BLUE -> {
            // Blueberry with crown indentation
            drawCircle(
                color = Color(0xFF0D47A1).copy(alpha = 0.9f * alpha),
                radius = radius * 0.82f,
                center = Offset(centerX, centerY)
            )
            // Blueberry crown star
            drawCircle(
                color = Color(0xFF1976D2).copy(alpha = alpha),
                radius = radius * 0.35f,
                center = Offset(centerX, centerY - radius * 0.25f),
                style = Stroke(width = radius * 0.12f)
            )
        }
        BubbleColor.YELLOW -> {
            // Golden Lemon with small leaf
            drawOval(
                color = Color(0xFFFFB300).copy(alpha = 0.9f * alpha),
                topLeft = Offset(centerX - radius * 0.8f, centerY - radius * 0.6f),
                size = Size(radius * 1.6f, radius * 1.2f)
            )
            // Leaf
            drawOval(
                color = Color(0xFF43A047).copy(alpha = alpha),
                topLeft = Offset(centerX - radius * 0.2f, centerY - radius * 0.95f),
                size = Size(radius * 0.5f, radius * 0.35f)
            )
        }
        BubbleColor.GREEN -> {
            // Lime slice or crisp green apple
            drawCircle(
                color = Color(0xFF1B5E20).copy(alpha = 0.9f * alpha),
                radius = radius * 0.85f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = Color(0xFF69F0AE).copy(alpha = 0.9f * alpha),
                radius = radius * 0.68f,
                center = Offset(centerX, centerY)
            )
            // Core
            drawCircle(
                color = Color.White.copy(alpha = 0.8f * alpha),
                radius = radius * 0.25f,
                center = Offset(centerX, centerY)
            )
        }
        BubbleColor.PURPLE -> {
            // Grape cluster
            val grapeRadius = radius * 0.34f
            val grapeColor = Color(0xFF4A148C).copy(alpha = 0.9f * alpha)
            drawCircle(grapeColor, grapeRadius, Offset(centerX - grapeRadius * 0.7f, centerY - grapeRadius * 0.5f))
            drawCircle(grapeColor, grapeRadius, Offset(centerX + grapeRadius * 0.7f, centerY - grapeRadius * 0.5f))
            drawCircle(grapeColor, grapeRadius, Offset(centerX, centerY + grapeRadius * 0.5f))
        }
        BubbleColor.GOLDEN_STAR -> {
            // Glowing 5-pointed Star
            drawStar(
                centerX = centerX,
                centerY = centerY,
                points = 5,
                outerRadius = radius * 0.95f,
                innerRadius = radius * 0.45f,
                color = Color.White.copy(alpha = alpha)
            )
        }
    }
}

fun DrawScope.drawStar(
    centerX: Float,
    centerY: Float,
    points: Int,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {
    val path = Path()
    val angleStep = (2 * PI / (points * 2)).toFloat()
    var currentAngle = -PI.toFloat() / 2f

    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val x = centerX + cos(currentAngle) * r
        val y = centerY + sin(currentAngle) * r
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        currentAngle += angleStep
    }
    path.close()
    drawPath(path, color, style = Fill)
}
