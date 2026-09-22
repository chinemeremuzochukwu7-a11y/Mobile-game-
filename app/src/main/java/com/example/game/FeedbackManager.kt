package com.example.game

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class FeedbackManager(context: Context) {
    private val appContext = context.applicationContext
    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true
        set(value) {
            field = value
            updateToneGenerator()
        }
    var isHapticsEnabled: Boolean = true
    var isAnimationsEnabled: Boolean = true
    var volumePercent: Int = 80
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
        } catch (_: Exception) {
            null
        }
    }

    fun playMoveFeedback() {
        if (isHapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(20)
                }
            } catch (_: Exception) { }
        }

        if (isSoundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
            } catch (_: Exception) { }
        }
    }

    fun playVictoryFeedback() {
        if (isHapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 80, 50, 120, 50, 200)
                    val amplitudes = intArrayOf(0, 200, 0, 230, 0, 255)
                    vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(300)
                }
            } catch (_: Exception) { }
        }

        if (isSoundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 250)
            } catch (_: Exception) { }
        }
    }

    fun playClick() {
        if (isHapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(10, 80))
                }
            } catch (_: Exception) { }
        }

        if (isSoundEnabled && volumePercent > 0) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 20)
            } catch (_: Exception) { }
        }
    }

    fun playStartupChime() {
        if (isSoundEnabled && volumePercent > 0) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 200)
            } catch (_: Exception) { }
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
