package com.seninterus.circlelock.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

object ToneGenerator {
    private const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(Dispatchers.IO)

    fun playTone(frequency: Float, durationMs: Int, volume: Float = 0.3f) {
        try {
            val sampleCount = (SAMPLE_RATE * durationMs / 1000).toInt()
            val samples = ShortArray(sampleCount)

            for (i in 0 until sampleCount) {
                val t = i.toFloat() / SAMPLE_RATE
                val envelope = (1f - i.toFloat() / sampleCount)
                samples[i] = (sin(2.0 * Math.PI * frequency * t) * Short.MAX_VALUE * volume * envelope).toInt().toShort()
            }

            val bufferSize = sampleCount * 2
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, sampleCount)
            audioTrack.play()

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                audioTrack.release()
            }, durationMs + 50L)
        } catch (_: Exception) {
        }
    }

    fun playClick() {
        playTone(800f, 30, 0.15f)
    }

    fun playSnap() {
        playTone(1200f, 60, 0.25f)
    }

    fun playWin() {
        scope.launch {
            playTone(523f, 120, 0.3f)
            delay(130)
            playTone(659f, 120, 0.3f)
            delay(130)
            playTone(784f, 180, 0.35f)
        }
    }

    fun playError() {
        playTone(200f, 200, 0.3f)
    }
}
