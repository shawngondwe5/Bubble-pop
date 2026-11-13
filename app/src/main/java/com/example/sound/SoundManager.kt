package com.example.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural Low-Latency Audio Synthesizer and Haptic Feedback Manager.
 * Operates 100% offline with zero external audio assets required.
 */
class SoundManager(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    private val sampleRate = 22050
    private var musicJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Plays a juicy, popping sound with pitch based on the combo streak.
     */
    fun playBubblePop(comboStreak: Int = 1) {
        if (!isSoundEnabled) {
            triggerPopHaptic()
            return
        }
        triggerPopHaptic()

        scope.launch {
            val baseFreq = 380.0 + (comboStreak.coerceAtMost(8) * 65.0)
            val durationMs = 65
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Frequency sweep upwards like an air bubble pop
                val freq = baseFreq + (1200.0 * (i.toDouble() / numSamples))
                val wave = sin(2.0 * PI * freq * t)
                // Fast envelope attack, exponential decay
                val envelope = exp(-t * 35.0)
                buffer[i] = (wave * envelope * Short.MAX_VALUE * 0.85).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Crisp tap sound for UI buttons.
     */
    fun playButtonClick() {
        if (isVibrationEnabled) {
            vibratePattern(20)
        }
        if (!isSoundEnabled) return

        scope.launch {
            val durationMs = 40
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val wave = sin(2.0 * PI * 850.0 * t) + 0.3 * sin(2.0 * PI * 1700.0 * t)
                val envelope = exp(-t * 60.0)
                buffer[i] = (wave * envelope * Short.MAX_VALUE * 0.55).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Joyful fanfare when game starts.
     */
    fun playGameStart() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(523.25, 90) // C5
            delay(100)
            playTone(659.25, 90) // E5
            delay(100)
            playTone(783.99, 140) // G5
        }
    }

    /**
     * Celebratory chime when a new high score is achieved.
     */
    fun playHighScoreChime() {
        if (isVibrationEnabled) {
            vibratePattern(80)
        }
        if (!isSoundEnabled) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                playTone(freq, 110)
                delay(120)
            }
        }
    }

    /**
     * Game over musical sign-off.
     */
    fun playGameOver() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(440.0, 140)
            delay(150)
            playTone(392.0, 140)
            delay(150)
            playTone(349.23, 260)
        }
    }

    /**
     * Starts cheerful background music cadence if enabled.
     */
    fun startBackgroundMusic() {
        if (!isMusicEnabled) {
            stopBackgroundMusic()
            return
        }
        if (musicJob?.isActive == true) return

        musicJob = scope.launch {
            val melody = doubleArrayOf(
                523.25, 587.33, 659.25, 783.99,
                659.25, 587.33, 523.25, 659.25,
                783.99, 880.00, 783.99, 659.25,
                587.33, 523.25, 587.33, 523.25
            )
            var noteIndex = 0
            while (isActive && isMusicEnabled) {
                val freq = melody[noteIndex % melody.size]
                playSoftPluck(freq, 120)
                noteIndex++
                delay(280)
            }
        }
    }

    fun stopBackgroundMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun playTone(freq: Double, durationMs: Int) {
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val wave = sin(2.0 * PI * freq * t)
            val envelope = exp(-t * 12.0)
            buffer[i] = (wave * envelope * Short.MAX_VALUE * 0.6).toInt().toShort()
        }
        playPcmBuffer(buffer)
    }

    private fun playSoftPluck(freq: Double, durationMs: Int) {
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val wave = sin(2.0 * PI * freq * t) + 0.2 * sin(4.0 * PI * freq * t)
            val envelope = exp(-t * 16.0)
            buffer[i] = (wave * envelope * Short.MAX_VALUE * 0.22).toInt().toShort()
        }
        playPcmBuffer(buffer)
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            track.setNotificationMarkerPosition(buffer.size)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) {
                    try {
                        t?.stop()
                        t?.release()
                    } catch (_: Exception) {}
                }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
        } catch (_: Exception) {
            // AudioTrack failure fallback safely ignored
        }
    }

    private fun triggerPopHaptic() {
        if (!isVibrationEnabled) return
        vibratePattern(25)
    }

    private fun vibratePattern(millis: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(millis)
            }
        } catch (_: Exception) {}
    }
}
