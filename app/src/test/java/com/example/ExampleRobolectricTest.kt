package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PreferencesRepository
import com.example.model.GameMode
import com.example.model.GameScreen
import com.example.viewmodel.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `appName should be Bubble Pop`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Bubble Pop", appName)
    }

    @Test
    fun `preferences repository handles high scores and settings`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = PreferencesRepository(context)

        repo.resetStatistics()
        assertEquals(0, repo.getHighScore(GameMode.CLASSIC))

        repo.saveHighScore(GameMode.CLASSIC, 350)
        assertEquals(350, repo.getHighScore(GameMode.CLASSIC))

        repo.saveHighScore(GameMode.CLASSIC, 200) // Lower score shouldn't overwrite
        assertEquals(350, repo.getHighScore(GameMode.CLASSIC))

        repo.saveHighScore(GameMode.CLASSIC, 500) // Higher score should overwrite
        assertEquals(500, repo.getHighScore(GameMode.CLASSIC))

        repo.setSoundEnabled(false)
        assertFalse(repo.isSoundEnabled())
        repo.setSoundEnabled(true)
        assertTrue(repo.isSoundEnabled())
    }

    @Test
    fun `bubble shooter viewModel flow and mechanics test`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = GameViewModel(app)

        assertEquals(GameScreen.HOME, viewModel.uiState.value.currentScreen)

        // Select Classic Mode
        viewModel.selectMode(GameMode.CLASSIC)
        assertEquals(GameMode.CLASSIC, viewModel.uiState.value.mode)

        // Start Game
        viewModel.startGame(GameMode.CLASSIC, level = 1)
        assertEquals(GameScreen.PLAYING, viewModel.uiState.value.currentScreen)
        assertEquals(1, viewModel.uiState.value.level)
        assertTrue(viewModel.uiState.value.shotsRemaining > 0)
        assertTrue(viewModel.uiState.value.grid.isNotEmpty())

        // Test bubble color swapping
        val initialCurrent = viewModel.uiState.value.currentBubble
        val initialNext = viewModel.uiState.value.nextBubble

        viewModel.switchBubbles()
        assertEquals(initialNext, viewModel.uiState.value.currentBubble)
        assertEquals(initialCurrent, viewModel.uiState.value.nextBubble)

        // Test aiming and shooting
        viewModel.updateArenaGeometry(1080f, 1920f)
        viewModel.onAim(540f, 300f)
        assertTrue(viewModel.uiState.value.aimTrajectory.isNotEmpty())

        viewModel.onReleaseShoot()
        assertNotNull(viewModel.uiState.value.projectile)
        assertTrue(viewModel.uiState.value.projectile!!.isActive)

        // Pause Game
        viewModel.pauseGame()
        assertEquals(GameScreen.PAUSED, viewModel.uiState.value.currentScreen)

        // Resume Game
        viewModel.resumeGame()
        assertEquals(GameScreen.PLAYING, viewModel.uiState.value.currentScreen)

        // Exit to Home
        viewModel.exitToHome()
        assertEquals(GameScreen.HOME, viewModel.uiState.value.currentScreen)
    }
}
