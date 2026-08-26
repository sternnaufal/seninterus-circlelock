package com.senintrerus.circlelock.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

object AudioManager {
    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private val soundMap = mutableMapOf<String, Int>()
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        initialized = true
    }

    fun playSound(context: Context, key: String) {
        if (!SettingsManager.isSoundEnabled(context)) return
        init(context)

        val loadedId = soundMap[key]
        if (loadedId != null) {
            soundPool?.play(loadedId, 1f, 1f, 0, 0, 1f)
        } else {
            when (key) {
                "click" -> ToneGenerator.playClick()
                "snap" -> ToneGenerator.playSnap()
                "win" -> ToneGenerator.playWin()
                "error" -> ToneGenerator.playError()
            }
        }
    }

    fun startMusic(context: Context, resId: Int) {
        if (!SettingsManager.isMusicEnabled(context)) return
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                start()
            }
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) it.pause()
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        mediaPlayer?.release()
        mediaPlayer = null
        initialized = false
    }
}
