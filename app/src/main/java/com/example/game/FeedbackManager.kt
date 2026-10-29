package com.example.game

import android.content.Context

/**
 * FeedbackManager delegates to SoundEffectManager and handles tactile and sound synthesis.
 */
class FeedbackManager(context: Context) {
    val soundEffectManager: SoundEffectManager = SoundEffectManager(context)

    var isSoundEnabled: Boolean
        get() = soundEffectManager.isSoundEnabled
        set(value) {
            soundEffectManager.isSoundEnabled = value
        }

    var isHapticsEnabled: Boolean
        get() = soundEffectManager.isHapticsEnabled
        set(value) {
            soundEffectManager.isHapticsEnabled = value
        }

    var isAnimationsEnabled: Boolean
        get() = soundEffectManager.isAnimationsEnabled
        set(value) {
            soundEffectManager.isAnimationsEnabled = value
        }

    var volumePercent: Int
        get() = soundEffectManager.volumePercent
        set(value) {
            soundEffectManager.volumePercent = value
        }

    fun playMoveFeedback() {
        soundEffectManager.playBlockMove()
    }

    fun playVictoryFeedback() {
        soundEffectManager.playWinCondition()
    }

    fun playTimerWarning() {
        soundEffectManager.playTimerWarning()
    }

    fun playClick() {
        soundEffectManager.playClick()
    }

    fun playStartupChime() {
        soundEffectManager.playStartupChime()
    }

    fun release() {
        soundEffectManager.release()
    }
}
