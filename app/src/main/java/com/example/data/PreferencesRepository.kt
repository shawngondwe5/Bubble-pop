package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GameMode

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bubble_pop_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HIGH_SCORE_CLASSIC = "high_score_classic"
        private const val KEY_HIGH_SCORE_TIME_ATTACK = "high_score_time_attack"
        private const val KEY_HIGH_SCORE_ENDLESS = "high_score_endless"
        private const val KEY_TOTAL_BUBBLES_POPPED = "total_bubbles_popped"
        private const val KEY_TOTAL_GAMES_PLAYED = "total_games_played"
        private const val KEY_MAX_COMBO = "max_combo"

        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    }

    fun getHighScore(mode: GameMode): Int {
        val key = when (mode) {
            GameMode.CLASSIC -> KEY_HIGH_SCORE_CLASSIC
            GameMode.TIME_ATTACK -> KEY_HIGH_SCORE_TIME_ATTACK
            GameMode.ENDLESS -> KEY_HIGH_SCORE_ENDLESS
        }
        return prefs.getInt(key, 0)
    }

    fun saveHighScore(mode: GameMode, score: Int) {
        val current = getHighScore(mode)
        if (score > current) {
            val key = when (mode) {
                GameMode.CLASSIC -> KEY_HIGH_SCORE_CLASSIC
                GameMode.TIME_ATTACK -> KEY_HIGH_SCORE_TIME_ATTACK
                GameMode.ENDLESS -> KEY_HIGH_SCORE_ENDLESS
            }
            prefs.edit().putInt(key, score).apply()
        }
    }

    fun getTotalBubblesPopped(): Int = prefs.getInt(KEY_TOTAL_BUBBLES_POPPED, 0)

    fun addBubblesPopped(count: Int) {
        val current = getTotalBubblesPopped()
        prefs.edit().putInt(KEY_TOTAL_BUBBLES_POPPED, current + count).apply()
    }

    fun getTotalGamesPlayed(): Int = prefs.getInt(KEY_TOTAL_GAMES_PLAYED, 0)

    fun incrementGamesPlayed() {
        val current = getTotalGamesPlayed()
        prefs.edit().putInt(KEY_TOTAL_GAMES_PLAYED, current + 1).apply()
    }

    fun getMaxCombo(): Int = prefs.getInt(KEY_MAX_COMBO, 0)

    fun recordMaxCombo(combo: Int) {
        val current = getMaxCombo()
        if (combo > current) {
            prefs.edit().putInt(KEY_MAX_COMBO, combo).apply()
        }
    }

    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND_ENABLED, true)
    fun setSoundEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()

    fun isMusicEnabled(): Boolean = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
    fun setMusicEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply()

    fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
    fun setVibrationEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()

    fun resetStatistics() {
        prefs.edit()
            .putInt(KEY_HIGH_SCORE_CLASSIC, 0)
            .putInt(KEY_HIGH_SCORE_TIME_ATTACK, 0)
            .putInt(KEY_HIGH_SCORE_ENDLESS, 0)
            .putInt(KEY_TOTAL_BUBBLES_POPPED, 0)
            .putInt(KEY_TOTAL_GAMES_PLAYED, 0)
            .putInt(KEY_MAX_COMBO, 0)
            .apply()
    }
}
