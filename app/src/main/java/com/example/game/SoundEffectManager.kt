package com.example.game

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SoundEffectManager manages all audio and haptic feedback across the game.
 * Provides custom, zero-latency synthesized audio feedback for:
 * 1. Block movements (crisp snap & slide clicks)
 * 2. Win conditions (exhilarating victory chime fanfare)
 * 3. Timer warnings (urgent countdown ticks & warning beeps)
 */
class SoundEffectManager(context: Context) {
    private val appContext = context.applicationContext
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        const val RECOMMENDED_VOLUME_PERCENT = 80
    }

    private var toneGenerator: ToneGenerator? = null

    var isSoundEnabled: Boolean = true
        set(value) {
            field = value
            updateToneGenerator()
        }

    var isHapticsEnabled: Boolean = true
    var isAnimationsEnabled: Boolean = true

    var volumePercent: Int = RECOMMENDED_VOLUME_PERCENT
        set(value) {
            field = value.coerceIn(0, 100)
            updateToneGenerator()
        }

    init {
        updateToneGenerator()
    }

    private fun updateToneGenerator() {
        try {
            toneGenerator?.release()
            toneGenerator = if (isSoundEnabled && volumePercent > 0) {
                ToneGenerator(AudioManager.STREAM_MUSIC, volumePercent)
            } else {
                null
            }
        } catch (_: Throwable) {
            toneGenerator = null
        }
    }

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Audio feedback for tile / block movements.
     * Produces an immediate, tactile snap click and light haptic feedback.
     */
    fun playBlockMove() {
        if (isHapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(18)
                }
            } catch (_: Throwable) { }
        }

        if (isSoundEnabled && volumePercent > 0) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
            } catch (_: Throwable) { }
        }
    }

    /**
     * Audio feedback for win conditions.
     * Plays a celebratory multi-note victory fanfare and triumph vibration pattern.
     */
    fun playWinCondition() {
        if (isHapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 70, 40, 100, 40, 180)
                    val amplitudes = intArrayOf(0, 180, 0, 220, 0, 255)
                    vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(300)
                }
            } catch (_: Throwable) { }
        }

        if (isSoundEnabled && volumePercent > 0) {
            managerScope.launch {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
                    delay(120)
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
                    delay(140)
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 280)
                } catch (_: Throwable) { }
            }
        }
    }

    /**
     * Audio feedback for timer warnings.
     * Emits an urgent high-pitched alert tick when time is running low or approaching par time.
     */
    fun playTimerWarning() {
        if (isHapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(25, 200))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            } catch (_: Throwable) { }
        }

        if (isSoundEnabled && volumePercent > 0) {
            try {
                // Urgent warning beep tone
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 60)
            } catch (_: Throwable) { }
        }
    }

    /**
     * Audio feedback for general interactive button clicks.
     */
    fun playClick() {
        if (isHapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(10, 80))
                }
            } catch (_: Throwable) { }
        }

        if (isSoundEnabled && volumePercent > 0) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 20)
            } catch (_: Throwable) { }
        }
    }

    /**
     * Audio feedback played when splash screen completes and game begins.
     */
    fun playStartupChime() {
        if (isSoundEnabled && volumePercent > 0) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 180)
            } catch (_: Throwable) { }
        }
    }

    fun release() {
        try {
            managerScope.cancel()
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Throwable) { }
    }
}
