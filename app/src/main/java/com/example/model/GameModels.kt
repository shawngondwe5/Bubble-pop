package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sqrt

enum class GameMode(
    val title: String,
    val subtitle: String,
    val initialShots: Int,
    val description: String
) {
    CLASSIC(
        title = "Classic",
        subtitle = "Level Stages",
        initialShots = 25,
        description = "Match 3+ bubbles to clear the board before running out of shots!"
    ),
    TIME_ATTACK(
        title = "Time Attack",
        subtitle = "Fast Frenzy",
        initialShots = 35,
        description = "Race against time! Fast matches and high combo multiplier bonuses!"
    ),
    ENDLESS(
        title = "Endless",
        subtitle = "Survival",
        initialShots = 999,
        description = "Keep clearing bubbles as the ceiling drops after every few foul shots!"
    )
}

enum class GameScreen {
    HOME,
    PLAYING,
    PAUSED,
    GAME_OVER,
    LEVEL_COMPLETED,
    SETTINGS,
    STATS,
    HOW_TO_PLAY
}

enum class BubbleColor(
    val points: Int,
    val baseColor: Color,
    val darkColor: Color,
    val lightColor: Color,
    val fruitName: String
) {
    BLUE(
        points = 10,
        baseColor = Color(0xFF2979FF),
        darkColor = Color(0xFF1565C0),
        lightColor = Color(0xFF82B1FF),
        fruitName = "Blueberry"
    ),
    GREEN(
        points = 15,
        baseColor = Color(0xFF00E676),
        darkColor = Color(0xFF2E7D32),
        lightColor = Color(0xFFB9F6CA),
        fruitName = "Lime"
    ),
    YELLOW(
        points = 20,
        baseColor = Color(0xFFFFD600),
        darkColor = Color(0xFFFF8F00),
        lightColor = Color(0xFFFFFF8D),
        fruitName = "Lemon"
    ),
    RED(
        points = 25,
        baseColor = Color(0xFFFF3D00),
        darkColor = Color(0xFFC62828),
        lightColor = Color(0xFFFF8A80),
        fruitName = "Tomato"
    ),
    PURPLE(
        points = 30,
        baseColor = Color(0xFFAB47BC),
        darkColor = Color(0xFF6A1B9A),
        lightColor = Color(0xFFE1BEE7),
        fruitName = "Grape"
    ),
    GOLDEN_STAR(
        points = 50,
        baseColor = Color(0xFFFFC107),
        darkColor = Color(0xFFFF6F00),
        lightColor = Color(0xFFFFF9C4),
        fruitName = "Star"
    )
}

data class HexCoord(val row: Int, val col: Int) {
    fun getNeighbors(): List<HexCoord> {
        val neighbors = mutableListOf<HexCoord>()
        // Same row
        neighbors.add(HexCoord(row, col - 1))
        neighbors.add(HexCoord(row, col + 1))

        if (row % 2 == 0) {
            // Even row
            neighbors.add(HexCoord(row - 1, col - 1))
            neighbors.add(HexCoord(row - 1, col))
            neighbors.add(HexCoord(row + 1, col - 1))
            neighbors.add(HexCoord(row + 1, col))
        } else {
            // Odd row
            neighbors.add(HexCoord(row - 1, col))
            neighbors.add(HexCoord(row - 1, col + 1))
            neighbors.add(HexCoord(row + 1, col))
            neighbors.add(HexCoord(row + 1, col + 1))
        }
        return neighbors
    }
}

data class GridBubble(
    val coord: HexCoord,
    val color: BubbleColor,
    var isPopping: Boolean = false,
    var popProgress: Float = 0f
)

data class Projectile(
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var color: BubbleColor = BubbleColor.BLUE,
    var isActive: Boolean = false
)

data class FallingBubble(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: BubbleColor,
    var alpha: Float = 1f,
    var rotation: Float = 0f
)

data class Particle(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1f,
    val isStar: Boolean = false
)

data class ShockwaveRing(
    val id: Long,
    val x: Float,
    val y: Float,
    var currentRadius: Float,
    val maxRadius: Float,
    val color: Color,
    var alpha: Float = 1f
)

data class FloatingBanner(
    val id: Long,
    val text: String,
    var x: Float,
    var y: Float,
    val color: Color,
    var alpha: Float = 1f,
    var scale: Float = 1f,
    val isBigCombo: Boolean = false
)
