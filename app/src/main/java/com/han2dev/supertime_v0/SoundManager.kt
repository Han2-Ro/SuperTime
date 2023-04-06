package com.han2dev.supertime_v0

import android.media.AudioAttributes
import android.media.SoundPool

object SoundManager {
    // set up soundPool
    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
    .setMaxStreams(6)
    .setAudioAttributes(audioAttributes)
    .build()

    val sound1 = soundPool.load(MainActivity.context, R.raw.sound1, 1)

    fun init() {
        println("SoundManager initialized")
    }

    fun playSound(sound: Int) {
        println("playing sound: $sound")
        val streamID = soundPool.play(sound, 1f, 1f, 1, 0, 1f)
        println("streamID: $streamID")
    }

    data class TimerEndSound(val id: Int, val playAtMsLeft: Int)
}