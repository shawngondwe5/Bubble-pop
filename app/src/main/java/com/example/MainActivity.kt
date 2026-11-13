package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.GameScreen
import com.example.ui.screens.GameOverDialog
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowToPlayDialog
import com.example.ui.screens.LevelCompletedDialog
import com.example.ui.screens.PauseDialog
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.StatsDialog
import com.example.ui.theme.BubblePopTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BubblePopTheme {
                BubbleShooterApp(viewModel = viewModel)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.soundManager.stopBackgroundMusic()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiState.value.currentScreen == GameScreen.PLAYING && viewModel.uiState.value.musicEnabled) {
            viewModel.soundManager.startBackgroundMusic()
        }
    }
}

@Composable
fun BubbleShooterApp(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (state.currentScreen) {
            GameScreen.HOME -> {
                HomeScreen(
                    selectedMode = state.mode,
                    highScore = state.highScore,
                    soundEnabled = state.soundEnabled,
                    onSelectMode = { viewModel.selectMode(it) },
                    onPlayClick = { viewModel.startGame() },
                    onSettingsClick = { viewModel.openSettings() },
                    onStatsClick = { viewModel.openStats() },
                    onHowToPlayClick = { viewModel.openHowToPlay() },
                    onToggleSound = { viewModel.toggleSound() }
                )
            }

            GameScreen.PLAYING -> {
                GameplayScreen(
                    level = state.level,
                    score = state.score,
                    highScore = state.highScore,
                    shotsRemaining = state.shotsRemaining,
                    foulCount = state.foulCount,
                    mode = state.mode,
                    combo = state.comboStreak,
                    currentBubble = state.currentBubble,
                    nextBubble = state.nextBubble,
                    aimAngle = state.aimAngle ?: -90f,
                    grid = state.grid,
                    projectile = state.projectile,
                    aimTrajectory = state.aimTrajectory,
                    fallingBubbles = state.fallingBubbles,
                    particles = state.particles,
                    shockwaves = state.shockwaves,
                    floatingBanners = state.floatingBanners,
                    mascotCheering = state.mascotCheering,
                    bubbleRadius = viewModel.bubbleRadius,
                    getHexCenter = { viewModel.getHexCenter(it) },
                    onAim = { x, y -> viewModel.onAim(x, y) },
                    onReleaseShoot = { viewModel.onReleaseShoot() },
                    onSwitchBubbles = { viewModel.switchBubbles() },
                    onGeometryChanged = { w, h -> viewModel.updateArenaGeometry(w, h) },
                    onPauseClick = { viewModel.pauseGame() }
                )
            }

            GameScreen.PAUSED -> {
                GameplayScreen(
                    level = state.level,
                    score = state.score,
                    highScore = state.highScore,
                    shotsRemaining = state.shotsRemaining,
                    foulCount = state.foulCount,
                    mode = state.mode,
                    combo = state.comboStreak,
                    currentBubble = state.currentBubble,
                    nextBubble = state.nextBubble,
                    aimAngle = state.aimAngle ?: -90f,
                    grid = state.grid,
                    projectile = null,
                    aimTrajectory = emptyList(),
                    fallingBubbles = state.fallingBubbles,
                    particles = state.particles,
                    shockwaves = state.shockwaves,
                    floatingBanners = state.floatingBanners,
                    mascotCheering = false,
                    bubbleRadius = viewModel.bubbleRadius,
                    getHexCenter = { viewModel.getHexCenter(it) },
                    onAim = { _, _ -> },
                    onReleaseShoot = {},
                    onSwitchBubbles = {},
                    onGeometryChanged = { _, _ -> },
                    onPauseClick = {}
                )
                PauseDialog(
                    score = state.score,
                    soundEnabled = state.soundEnabled,
                    onResume = { viewModel.resumeGame() },
                    onRestart = { viewModel.restartGame() },
                    onHome = { viewModel.exitToHome() },
                    onToggleSound = { viewModel.toggleSound() }
                )
            }

            GameScreen.GAME_OVER -> {
                GameplayScreen(
                    level = state.level,
                    score = state.score,
                    highScore = state.highScore,
                    shotsRemaining = state.shotsRemaining,
                    foulCount = state.foulCount,
                    mode = state.mode,
                    combo = state.comboStreak,
                    currentBubble = state.currentBubble,
                    nextBubble = state.nextBubble,
                    aimAngle = state.aimAngle ?: -90f,
                    grid = state.grid,
                    projectile = null,
                    aimTrajectory = emptyList(),
                    fallingBubbles = state.fallingBubbles,
                    particles = state.particles,
                    shockwaves = state.shockwaves,
                    floatingBanners = state.floatingBanners,
                    mascotCheering = false,
                    bubbleRadius = viewModel.bubbleRadius,
                    getHexCenter = { viewModel.getHexCenter(it) },
                    onAim = { _, _ -> },
                    onReleaseShoot = {},
                    onSwitchBubbles = {},
                    onGeometryChanged = { _, _ -> },
                    onPauseClick = {}
                )
                GameOverDialog(
                    score = state.score,
                    highScore = state.highScore,
                    isNewHigh = state.isNewHighScore,
                    mode = state.mode,
                    bubblesPopped = state.bubblesPoppedThisGame,
                    maxCombo = state.maxComboThisGame,
                    onPlayAgain = { viewModel.restartGame() },
                    onHome = { viewModel.exitToHome() }
                )
            }

            GameScreen.LEVEL_COMPLETED -> {
                GameplayScreen(
                    level = state.level,
                    score = state.score,
                    highScore = state.highScore,
                    shotsRemaining = state.shotsRemaining,
                    foulCount = state.foulCount,
                    mode = state.mode,
                    combo = state.comboStreak,
                    currentBubble = state.currentBubble,
                    nextBubble = state.nextBubble,
                    aimAngle = state.aimAngle ?: -90f,
                    grid = state.grid,
                    projectile = null,
                    aimTrajectory = emptyList(),
                    fallingBubbles = state.fallingBubbles,
                    particles = state.particles,
                    shockwaves = state.shockwaves,
                    floatingBanners = state.floatingBanners,
                    mascotCheering = true,
                    bubbleRadius = viewModel.bubbleRadius,
                    getHexCenter = { viewModel.getHexCenter(it) },
                    onAim = { _, _ -> },
                    onReleaseShoot = {},
                    onSwitchBubbles = {},
                    onGeometryChanged = { _, _ -> },
                    onPauseClick = {}
                )
                LevelCompletedDialog(
                    level = state.level,
                    score = state.score,
                    highScore = state.highScore,
                    onNextLevel = { viewModel.nextLevel() },
                    onHome = { viewModel.exitToHome() }
                )
            }

            GameScreen.SETTINGS -> {
                HomeScreen(
                    selectedMode = state.mode,
                    highScore = state.highScore,
                    soundEnabled = state.soundEnabled,
                    onSelectMode = { viewModel.selectMode(it) },
                    onPlayClick = { viewModel.startGame() },
                    onSettingsClick = {},
                    onStatsClick = {},
                    onHowToPlayClick = {},
                    onToggleSound = { viewModel.toggleSound() }
                )
                SettingsDialog(
                    soundEnabled = state.soundEnabled,
                    musicEnabled = state.musicEnabled,
                    vibrationEnabled = state.vibrationEnabled,
                    onToggleSound = { viewModel.toggleSound() },
                    onToggleMusic = { viewModel.toggleMusic() },
                    onToggleVibration = { viewModel.toggleVibration() },
                    onResetStats = { viewModel.resetStats() },
                    onClose = { viewModel.exitToHome() }
                )
            }

            GameScreen.STATS -> {
                HomeScreen(
                    selectedMode = state.mode,
                    highScore = state.highScore,
                    soundEnabled = state.soundEnabled,
                    onSelectMode = { viewModel.selectMode(it) },
                    onPlayClick = { viewModel.startGame() },
                    onSettingsClick = {},
                    onStatsClick = {},
                    onHowToPlayClick = {},
                    onToggleSound = { viewModel.toggleSound() }
                )
                StatsDialog(
                    totalBubbles = state.totalBubblesEver,
                    totalGames = state.totalGamesEver,
                    maxCombo = state.maxComboEver,
                    onClose = { viewModel.exitToHome() }
                )
            }

            GameScreen.HOW_TO_PLAY -> {
                HomeScreen(
                    selectedMode = state.mode,
                    highScore = state.highScore,
                    soundEnabled = state.soundEnabled,
                    onSelectMode = { viewModel.selectMode(it) },
                    onPlayClick = { viewModel.startGame() },
                    onSettingsClick = {},
                    onStatsClick = {},
                    onHowToPlayClick = {},
                    onToggleSound = { viewModel.toggleSound() }
                )
                HowToPlayDialog(
                    onClose = { viewModel.exitToHome() }
                )
            }
        }
    }
}
