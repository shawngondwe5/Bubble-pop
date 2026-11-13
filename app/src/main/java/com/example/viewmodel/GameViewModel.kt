package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PreferencesRepository
import com.example.model.BubbleColor
import com.example.model.FallingBubble
import com.example.model.FloatingBanner
import com.example.model.GameMode
import com.example.model.GameScreen
import com.example.model.GridBubble
import com.example.model.HexCoord
import com.example.model.Particle
import com.example.model.Projectile
import com.example.model.ShockwaveRing
import com.example.sound.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

data class GameUiState(
    val currentScreen: GameScreen = GameScreen.HOME,
    val mode: GameMode = GameMode.CLASSIC,
    val level: Int = 1,
    val score: Int = 0,
    val highScore: Int = 0,
    val shotsRemaining: Int = 25,
    val currentBubble: BubbleColor = BubbleColor.BLUE,
    val nextBubble: BubbleColor = BubbleColor.RED,
    val grid: Map<HexCoord, GridBubble> = emptyMap(),
    val foulCount: Int = 0,
    val ceilingRowOffset: Int = 0,
    val projectile: Projectile? = null,
    val aimAngle: Float? = -90f,
    val aimTrajectory: List<Offset> = emptyList(),
    val fallingBubbles: List<FallingBubble> = emptyList(),
    val particles: List<Particle> = emptyList(),
    val shockwaves: List<ShockwaveRing> = emptyList(),
    val floatingBanners: List<FloatingBanner> = emptyList(),
    val comboStreak: Int = 0,
    val maxComboThisGame: Int = 0,
    val bubblesPoppedThisGame: Int = 0,
    val isNewHighScore: Boolean = false,
    val mascotCheering: Boolean = false,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val totalBubblesEver: Int = 0,
    val totalGamesEver: Int = 0,
    val maxComboEver: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application)
    val soundManager = SoundManager(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var nextFallingId = 1L
    private var nextParticleId = 1L
    private var nextBannerId = 1L
    private var nextShockwaveId = 1L

    // Honeycomb layout configuration
    val colsEven = 8
    val colsOdd = 7
    val maxRows = 12

    // Dynamic arena geometry (measured by GameCanvas in px)
    var arenaWidth = 1080f
    var arenaHeight = 1920f
    var bubbleRadius = 42f
    var cannonPos = Offset(540f, 1600f)

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        val sound = repository.isSoundEnabled()
        val music = repository.isMusicEnabled()
        val vib = repository.isVibrationEnabled()

        soundManager.isSoundEnabled = sound
        soundManager.isMusicEnabled = music
        soundManager.isVibrationEnabled = vib

        _uiState.update {
            it.copy(
                soundEnabled = sound,
                musicEnabled = music,
                vibrationEnabled = vib,
                totalBubblesEver = repository.getTotalBubblesPopped(),
                totalGamesEver = repository.getTotalGamesPlayed(),
                maxComboEver = repository.getMaxCombo()
            )
        }
        updateHighScoreForMode(_uiState.value.mode)
    }

    private fun updateHighScoreForMode(mode: GameMode) {
        val high = repository.getHighScore(mode)
        _uiState.update { it.copy(highScore = high) }
    }

    fun selectMode(mode: GameMode) {
        _uiState.update { it.copy(mode = mode) }
        updateHighScoreForMode(mode)
    }

    fun startGame(mode: GameMode = _uiState.value.mode, level: Int = 1) {
        stopAllGameJobs()
        soundManager.playGameStart()
        soundManager.startBackgroundMusic()

        val high = repository.getHighScore(mode)
        val initialShots = when (mode) {
            GameMode.CLASSIC -> 25 + (level * 2)
            GameMode.TIME_ATTACK -> 35
            GameMode.ENDLESS -> 999
        }

        val initialGrid = generateLevelGrid(level)
        val current = getRandomColorFromGrid(initialGrid)
        val next = getRandomColorFromGrid(initialGrid)

        _uiState.update {
            it.copy(
                currentScreen = GameScreen.PLAYING,
                mode = mode,
                level = level,
                score = 0,
                highScore = high,
                shotsRemaining = initialShots,
                currentBubble = current,
                nextBubble = next,
                grid = initialGrid,
                foulCount = 0,
                ceilingRowOffset = 0,
                projectile = null,
                aimAngle = -90f,
                aimTrajectory = emptyList(),
                fallingBubbles = emptyList(),
                particles = emptyList(),
                shockwaves = emptyList(),
                floatingBanners = emptyList(),
                comboStreak = 0,
                maxComboThisGame = 0,
                bubblesPoppedThisGame = 0,
                isNewHighScore = false,
                mascotCheering = false
            )
        }

        startGameLoop()
    }

    fun updateArenaGeometry(width: Float, height: Float) {
        arenaWidth = width
        arenaHeight = height
        // Calculate bubble radius so that 8 bubbles span the arena width
        bubbleRadius = width / (colsEven * 2f)
        cannonPos = Offset(width / 2f, height - bubbleRadius * 3.4f)
    }

    private fun generateLevelGrid(level: Int): Map<HexCoord, GridBubble> {
        val grid = mutableMapOf<HexCoord, GridBubble>()
        val rows = when {
            level == 1 -> 5
            level == 2 -> 6
            else -> 7
        }
        val colorsPool = listOf(
            BubbleColor.BLUE,
            BubbleColor.RED,
            BubbleColor.YELLOW,
            BubbleColor.GREEN,
            BubbleColor.PURPLE
        )

        for (r in 0 until rows) {
            val cols = if (r % 2 == 0) colsEven else colsOdd
            for (c in 0 until cols) {
                // Keep clusters of same colors for playable matches
                val clusterFactor = (c / 2 + r / 2) % colorsPool.size
                val color = colorsPool[clusterFactor]
                val coord = HexCoord(r, c)
                grid[coord] = GridBubble(coord = coord, color = color)
            }
        }
        return grid
    }

    private fun getRandomColorFromGrid(grid: Map<HexCoord, GridBubble>): BubbleColor {
        val remainingColors = grid.values.map { it.color }.distinct()
        return if (remainingColors.isNotEmpty()) {
            remainingColors.random()
        } else {
            BubbleColor.BLUE
        }
    }

    fun getHexCenter(coord: HexCoord): Offset {
        val r = coord.row + _uiState.value.ceilingRowOffset
        val col = coord.col
        val rowHeight = bubbleRadius * sqrt(3f)
        val y = bubbleRadius + r * rowHeight
        val x = if (r % 2 == 0) {
            bubbleRadius + col * 2f * bubbleRadius
        } else {
            2f * bubbleRadius + col * 2f * bubbleRadius
        }
        return Offset(x, y)
    }

    fun onAim(touchX: Float, touchY: Float) {
        if (_uiState.value.projectile?.isActive == true) return

        val dx = touchX - cannonPos.x
        val dy = touchY - cannonPos.y

        // Angle in radians
        var angleRad = atan2(dy, dx)
        var angleDeg = (angleRad * 180f / PI).toFloat()

        // Clamp angle so user only aims towards the upper board
        angleDeg = angleDeg.coerceIn(-165f, -15f)
        angleRad = (angleDeg * PI / 180f).toFloat()

        // Calculate dotted trajectory line with wall bounce
        val trajectory = calculateTrajectory(cannonPos, angleRad)

        _uiState.update {
            it.copy(
                aimAngle = angleDeg,
                aimTrajectory = trajectory
            )
        }
    }

    private fun calculateTrajectory(start: Offset, angleRad: Float): List<Offset> {
        val points = mutableListOf<Offset>()
        points.add(start)

        var curX = start.x
        var curY = start.y
        var dirX = cos(angleRad)
        var dirY = sin(angleRad)
        val step = 16f
        var bounces = 0

        while (curY > bubbleRadius && bounces < 2 && points.size < 40) {
            curX += dirX * step
            curY += dirY * step

            // Bounce off left wall
            if (curX <= bubbleRadius) {
                curX = bubbleRadius
                dirX = -dirX
                bounces++
                points.add(Offset(curX, curY))
            }
            // Bounce off right wall
            else if (curX >= arenaWidth - bubbleRadius) {
                curX = arenaWidth - bubbleRadius
                dirX = -dirX
                bounces++
                points.add(Offset(curX, curY))
            }

            // Check if ray collides with any bubble in the grid
            val hitGrid = _uiState.value.grid.values.any { gb ->
                val center = getHexCenter(gb.coord)
                hypot(curX - center.x, curY - center.y) <= bubbleRadius * 1.8f
            }

            if (hitGrid || curY <= bubbleRadius) {
                points.add(Offset(curX, curY))
                break
            }

            if (points.size % 2 == 0) {
                points.add(Offset(curX, curY))
            }
        }
        return points
    }

    fun onReleaseShoot() {
        val state = _uiState.value
        if (state.projectile?.isActive == true || state.shotsRemaining <= 0) return

        val angleDeg = state.aimAngle ?: -90f
        val angleRad = (angleDeg * PI / 180f).toFloat()
        val speed = 32f

        val projectile = Projectile(
            x = cannonPos.x,
            y = cannonPos.y,
            vx = cos(angleRad) * speed,
            vy = sin(angleRad) * speed,
            color = state.currentBubble,
            isActive = true
        )

        soundManager.playBubblePop(1)

        val newShots = state.shotsRemaining - 1
        val newCurrent = state.nextBubble
        val newNext = getRandomColorFromGrid(state.grid)

        _uiState.update {
            it.copy(
                projectile = projectile,
                shotsRemaining = newShots,
                currentBubble = newCurrent,
                nextBubble = newNext,
                aimTrajectory = emptyList()
            )
        }
    }

    fun switchBubbles() {
        if (_uiState.value.projectile?.isActive == true) return
        soundManager.playButtonClick()
        _uiState.update {
            it.copy(
                currentBubble = it.nextBubble,
                nextBubble = it.currentBubble
            )
        }
    }

    private fun startGameLoop() {
        gameLoopJob = viewModelScope.launch {
            while (isActive && _uiState.value.currentScreen == GameScreen.PLAYING) {
                delay(16L) // ~60 FPS
                updatePhysics()
            }
        }
    }

    private fun updatePhysics() {
        val state = _uiState.value

        // 1. Update Projectile Movement & Collision
        val proj = state.projectile
        if (proj != null && proj.isActive) {
            proj.x += proj.vx
            proj.y += proj.vy

            // Wall bounce
            if (proj.x <= bubbleRadius) {
                proj.x = bubbleRadius
                proj.vx = -proj.vx
            } else if (proj.x >= arenaWidth - bubbleRadius) {
                proj.x = arenaWidth - bubbleRadius
                proj.vx = -proj.vx
            }

            // Check ceiling collision
            val ceilingY = bubbleRadius + state.ceilingRowOffset * (bubbleRadius * sqrt(3f))
            var collisionDetected = proj.y <= ceilingY

            // Check collision with grid bubbles
            var hitBubble: GridBubble? = null
            if (!collisionDetected) {
                for (gb in state.grid.values) {
                    val center = getHexCenter(gb.coord)
                    if (hypot(proj.x - center.x, proj.y - center.y) <= bubbleRadius * 1.85f) {
                        collisionDetected = true
                        hitBubble = gb
                        break
                    }
                }
            }

            if (collisionDetected) {
                proj.isActive = false
                attachBubbleToGrid(proj.x, proj.y, proj.color, hitBubble)
                return
            }
        }

        // 2. Update Falling Bubbles
        val updatedFalling = state.fallingBubbles.mapNotNull { fb ->
            fb.x += fb.vx
            fb.y += fb.vy
            fb.vy += 0.9f // gravity
            fb.rotation += 4f
            fb.alpha -= 0.018f
            if (fb.y > arenaHeight || fb.alpha <= 0.05f) null else fb
        }

        // 3. Update Particles
        val updatedParticles = state.particles.mapNotNull { p ->
            p.x += p.vx
            p.y += p.vy
            p.vy += 0.35f
            p.alpha -= 0.025f
            if (p.alpha <= 0.05f) null else p
        }

        // 4. Update Shockwaves
        val updatedShockwaves = state.shockwaves.mapNotNull { s ->
            s.currentRadius += (s.maxRadius - s.currentRadius) * 0.18f
            s.alpha -= 0.045f
            if (s.alpha <= 0.05f || s.currentRadius >= s.maxRadius * 0.95f) null else s
        }

        // 5. Update Floating Banners
        val updatedBanners = state.floatingBanners.mapNotNull { b ->
            b.y -= 1.8f
            b.alpha -= 0.02f
            b.scale = (b.scale + 0.015f).coerceAtMost(1.35f)
            if (b.alpha <= 0.05f) null else b
        }

        _uiState.update {
            it.copy(
                fallingBubbles = updatedFalling,
                particles = updatedParticles,
                shockwaves = updatedShockwaves,
                floatingBanners = updatedBanners
            )
        }
    }

    private fun attachBubbleToGrid(
        projX: Float,
        projY: Float,
        color: BubbleColor,
        hitBubble: GridBubble?
    ) {
        val state = _uiState.value
        val grid = state.grid.toMutableMap()

        // Find closest empty valid hex cell
        var bestCoord: HexCoord? = null
        var minDistance = Float.MAX_VALUE

        val candidateCoords = mutableListOf<HexCoord>()
        if (hitBubble != null) {
            candidateCoords.addAll(hitBubble.coord.getNeighbors())
        } else {
            // Attached to ceiling (row 0)
            val effectiveCol = ((projX - bubbleRadius) / (bubbleRadius * 2f)).toInt().coerceIn(0, colsEven - 1)
            candidateCoords.add(HexCoord(0, effectiveCol))
            candidateCoords.add(HexCoord(0, (effectiveCol - 1).coerceAtLeast(0)))
            candidateCoords.add(HexCoord(0, (effectiveCol + 1).coerceAtMost(colsEven - 1)))
        }

        for (coord in candidateCoords) {
            val maxCol = if ((coord.row + state.ceilingRowOffset) % 2 == 0) colsEven else colsOdd
            if (coord.row >= 0 && coord.col in 0 until maxCol && !grid.containsKey(coord)) {
                val center = getHexCenter(coord)
                val dist = hypot(projX - center.x, projY - center.y)
                if (dist < minDistance) {
                    minDistance = dist
                    bestCoord = coord
                }
            }
        }

        // Fallback: search row 0 if no neighbor found
        if (bestCoord == null) {
            val col = ((projX - bubbleRadius) / (bubbleRadius * 2f)).toInt().coerceIn(0, colsEven - 1)
            bestCoord = HexCoord(0, col)
        }

        val placedCoord = bestCoord
        grid[placedCoord] = GridBubble(coord = placedCoord, color = color)

        // 1. Find Connected Matching Cluster (Flood Fill / BFS)
        val matchCluster = findMatchingCluster(grid, placedCoord, color)

        var newScore = state.score
        var newCombo = state.comboStreak
        var newFoulCount = state.foulCount
        var newCeilingOffset = state.ceilingRowOffset
        var newPopped = state.bubblesPoppedThisGame
        var cheer = false

        val newParticles = mutableListOf<Particle>()
        val newShockwaves = mutableListOf<ShockwaveRing>()
        val newBanners = mutableListOf<FloatingBanner>()
        val newFalling = mutableListOf<FallingBubble>()

        if (matchCluster.size >= 3) {
            // MATCH! Pop all in cluster
            newCombo++
            cheer = true
            soundManager.playBubblePop(newCombo)

            val pointsEarned = matchCluster.size * 20 * newCombo
            newScore += pointsEarned
            newPopped += matchCluster.size
            newFoulCount = 0 // reset fouls

            val hitCenter = getHexCenter(placedCoord)
            newShockwaves.add(
                ShockwaveRing(
                    id = nextShockwaveId++,
                    x = hitCenter.x,
                    y = hitCenter.y,
                    currentRadius = 15f,
                    maxRadius = 110f,
                    color = color.lightColor
                )
            )

            newBanners.add(
                FloatingBanner(
                    id = nextBannerId++,
                    text = "+$pointsEarned",
                    x = hitCenter.x,
                    y = hitCenter.y - 30f,
                    color = Color(0xFFFFD600),
                    scale = 1.2f
                )
            )

            if (newCombo >= 2) {
                newBanners.add(
                    FloatingBanner(
                        id = nextBannerId++,
                        text = if (newCombo >= 4) "PERFECT!" else "COMBO x$newCombo!",
                        x = hitCenter.x,
                        y = hitCenter.y - 65f,
                        color = Color(0xFFFF4081),
                        scale = 1.35f,
                        isBigCombo = true
                    )
                )
            }

            for (c in matchCluster) {
                val center = getHexCenter(c)
                // Particles
                for (p in 0 until 6) {
                    val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                    val spd = Random.nextFloat() * 7f + 3f
                    newParticles.add(
                        Particle(
                            id = nextParticleId++,
                            x = center.x,
                            y = center.y,
                            vx = cos(angle) * spd,
                            vy = sin(angle) * spd - 1.5f,
                            color = color.baseColor,
                            size = Random.nextFloat() * 7f + 4f,
                            isStar = (p % 2 == 0)
                        )
                    )
                }
                grid.remove(c)
            }

            // 2. Find Disconnected / Floating Bubbles (Avalanche Drop)
            val disconnected = findDisconnectedBubbles(grid)
            if (disconnected.isNotEmpty()) {
                val avalancheBonus = disconnected.size * 50
                newScore += avalancheBonus
                newPopped += disconnected.size

                newBanners.add(
                    FloatingBanner(
                        id = nextBannerId++,
                        text = "AVALANCHE +$avalancheBonus!",
                        x = arenaWidth / 2f,
                        y = arenaHeight / 2.5f,
                        color = Color(0xFF00E676),
                        scale = 1.3f,
                        isBigCombo = true
                    )
                )

                for (discCoord in disconnected) {
                    val discBubble = grid.remove(discCoord)
                    if (discBubble != null) {
                        val center = getHexCenter(discCoord)
                        newFalling.add(
                            FallingBubble(
                                id = nextFallingId++,
                                x = center.x,
                                y = center.y,
                                vx = (Random.nextFloat() - 0.5f) * 6f,
                                vy = -Random.nextFloat() * 4f,
                                color = discBubble.color
                            )
                        )
                    }
                }
            }
        } else {
            // Foul shot: No match
            newCombo = 0
            newFoulCount++
            if (newFoulCount >= 4) {
                // Ceiling drops down 1 row
                newCeilingOffset++
                newFoulCount = 0
                newBanners.add(
                    FloatingBanner(
                        id = nextBannerId++,
                        text = "CEILING DROP!",
                        x = arenaWidth / 2f,
                        y = arenaHeight / 3f,
                        color = Color(0xFFFF3D00),
                        scale = 1.25f,
                        isBigCombo = true
                    )
                )
            }
        }

        // Check High Score
        val isNewHigh = newScore > state.highScore
        if (isNewHigh && !state.isNewHighScore) {
            soundManager.playHighScoreChime()
        }

        val maxCombo = maxOf(state.maxComboThisGame, newCombo)

        // Check Win Condition (board cleared)
        val isLevelCleared = grid.isEmpty()

        // Check Loss Condition (bubbles reached danger line near cannon or out of shots)
        val dangerY = cannonPos.y - bubbleRadius * 2.5f
        val reachedDangerLine = grid.keys.any { coord ->
            getHexCenter(coord).y >= dangerY
        }
        val isOutOfShots = state.shotsRemaining <= 0 && !isLevelCleared
        val isGameOver = (reachedDangerLine || isOutOfShots) && !isLevelCleared

        _uiState.update {
            it.copy(
                grid = grid,
                score = newScore,
                highScore = if (isNewHigh) newScore else it.highScore,
                foulCount = newFoulCount,
                ceilingRowOffset = newCeilingOffset,
                comboStreak = newCombo,
                maxComboThisGame = maxCombo,
                bubblesPoppedThisGame = newPopped,
                isNewHighScore = isNewHigh,
                mascotCheering = cheer,
                projectile = null,
                particles = it.particles + newParticles,
                shockwaves = it.shockwaves + newShockwaves,
                floatingBanners = it.floatingBanners + newBanners,
                fallingBubbles = it.fallingBubbles + newFalling
            )
        }

        if (cheer) {
            viewModelScope.launch {
                delay(1200L)
                _uiState.update { it.copy(mascotCheering = false) }
            }
        }

        if (isLevelCleared) {
            handleLevelComplete()
        } else if (isGameOver) {
            gameOver()
        }
    }

    private fun findMatchingCluster(
        grid: Map<HexCoord, GridBubble>,
        start: HexCoord,
        targetColor: BubbleColor
    ): Set<HexCoord> {
        val visited = mutableSetOf<HexCoord>()
        val queue = ArrayDeque<HexCoord>()

        queue.add(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            for (nbr in curr.getNeighbors()) {
                val nbrBubble = grid[nbr]
                if (nbrBubble != null && nbrBubble.color == targetColor && !visited.contains(nbr)) {
                    visited.add(nbr)
                    queue.add(nbr)
                }
            }
        }
        return visited
    }

    private fun findDisconnectedBubbles(grid: Map<HexCoord, GridBubble>): Set<HexCoord> {
        val ceilingBubbles = grid.keys.filter { it.row == 0 }
        val anchored = mutableSetOf<HexCoord>()
        val queue = ArrayDeque<HexCoord>()

        for (c in ceilingBubbles) {
            anchored.add(c)
            queue.add(c)
        }

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            for (nbr in curr.getNeighbors()) {
                if (grid.containsKey(nbr) && !anchored.contains(nbr)) {
                    anchored.add(nbr)
                    queue.add(nbr)
                }
            }
        }

        return grid.keys.filter { !anchored.contains(it) }.toSet()
    }

    private fun handleLevelComplete() {
        stopAllGameJobs()
        soundManager.stopBackgroundMusic()
        soundManager.playHighScoreChime()

        val finalScore = _uiState.value.score
        val mode = _uiState.value.mode
        val popped = _uiState.value.bubblesPoppedThisGame
        val maxCombo = _uiState.value.maxComboThisGame

        repository.saveHighScore(mode, finalScore)
        repository.addBubblesPopped(popped)
        repository.incrementGamesPlayed()
        repository.recordMaxCombo(maxCombo)

        _uiState.update {
            it.copy(
                currentScreen = GameScreen.LEVEL_COMPLETED,
                totalBubblesEver = repository.getTotalBubblesPopped(),
                totalGamesEver = repository.getTotalGamesPlayed(),
                maxComboEver = repository.getMaxCombo()
            )
        }
    }

    fun nextLevel() {
        soundManager.playButtonClick()
        val nextLvl = _uiState.value.level + 1
        startGame(_uiState.value.mode, nextLvl)
    }

    fun gameOver() {
        stopAllGameJobs()
        soundManager.stopBackgroundMusic()
        soundManager.playGameOver()

        val finalScore = _uiState.value.score
        val mode = _uiState.value.mode
        val popped = _uiState.value.bubblesPoppedThisGame
        val maxCombo = _uiState.value.maxComboThisGame

        repository.saveHighScore(mode, finalScore)
        repository.addBubblesPopped(popped)
        repository.incrementGamesPlayed()
        repository.recordMaxCombo(maxCombo)

        _uiState.update {
            it.copy(
                currentScreen = GameScreen.GAME_OVER,
                totalBubblesEver = repository.getTotalBubblesPopped(),
                totalGamesEver = repository.getTotalGamesPlayed(),
                maxComboEver = repository.getMaxCombo()
            )
        }
    }

    fun pauseGame() {
        if (_uiState.value.currentScreen != GameScreen.PLAYING) return
        stopAllGameJobs()
        soundManager.playButtonClick()
        soundManager.stopBackgroundMusic()
        _uiState.update { it.copy(currentScreen = GameScreen.PAUSED) }
    }

    fun resumeGame() {
        if (_uiState.value.currentScreen != GameScreen.PAUSED) return
        soundManager.playButtonClick()
        soundManager.startBackgroundMusic()
        _uiState.update { it.copy(currentScreen = GameScreen.PLAYING) }
        startGameLoop()
    }

    fun restartGame() {
        soundManager.playButtonClick()
        startGame(_uiState.value.mode, _uiState.value.level)
    }

    fun openHowToPlay() {
        soundManager.playButtonClick()
        _uiState.update { it.copy(currentScreen = GameScreen.HOW_TO_PLAY) }
    }

    fun openSettings() {
        soundManager.playButtonClick()
        _uiState.update { it.copy(currentScreen = GameScreen.SETTINGS) }
    }

    fun openStats() {
        soundManager.playButtonClick()
        _uiState.update {
            it.copy(
                currentScreen = GameScreen.STATS,
                totalBubblesEver = repository.getTotalBubblesPopped(),
                totalGamesEver = repository.getTotalGamesPlayed(),
                maxComboEver = repository.getMaxCombo()
            )
        }
    }

    fun exitToHome() {
        stopAllGameJobs()
        soundManager.playButtonClick()
        soundManager.stopBackgroundMusic()
        updateHighScoreForMode(_uiState.value.mode)
        _uiState.update { it.copy(currentScreen = GameScreen.HOME) }
    }

    fun toggleSound() {
        val newVal = !_uiState.value.soundEnabled
        repository.setSoundEnabled(newVal)
        soundManager.isSoundEnabled = newVal
        _uiState.update { it.copy(soundEnabled = newVal) }
        if (newVal) soundManager.playButtonClick()
    }

    fun toggleMusic() {
        val newVal = !_uiState.value.musicEnabled
        repository.setMusicEnabled(newVal)
        soundManager.isMusicEnabled = newVal
        _uiState.update { it.copy(musicEnabled = newVal) }
        if (newVal && _uiState.value.currentScreen == GameScreen.PLAYING) {
            soundManager.startBackgroundMusic()
        } else {
            soundManager.stopBackgroundMusic()
        }
    }

    fun toggleVibration() {
        val newVal = !_uiState.value.vibrationEnabled
        repository.setVibrationEnabled(newVal)
        soundManager.isVibrationEnabled = newVal
        _uiState.update { it.copy(vibrationEnabled = newVal) }
        if (newVal) soundManager.playButtonClick()
    }

    fun resetStats() {
        repository.resetStatistics()
        soundManager.playButtonClick()
        _uiState.update {
            it.copy(
                highScore = 0,
                totalBubblesEver = 0,
                totalGamesEver = 0,
                maxComboEver = 0
            )
        }
    }

    private fun stopAllGameJobs() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAllGameJobs()
        soundManager.stopBackgroundMusic()
    }
}
