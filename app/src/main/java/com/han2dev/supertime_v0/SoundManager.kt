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

    // loaded sounds: resId -> soundId
    private val loadedSounds: MutableMap<Int, Int> = mutableMapOf()

    val sounds: Array<TimerEndSound> = arrayOf(
        TimerEndSound(R.raw.sound1, "bing1",0),
        TimerEndSound(R.raw.halo_respawn_sound,"halo respawn" , 3290)
    )

    fun loadSound(resId: Int): Int {
        if (!loadedSounds.containsKey(resId)) {
            loadedSounds[resId] = soundPool.load(MainActivity.context, resId, 1)
            println("loaded sound: $resId")
        }
        return loadedSounds[resId]!!
    }

    fun loadSound(sound: TimerEndSound): TimerEndSound {
        sound.soundID = loadSound(sound.resId)
        return sound
    }

    fun playSound(sound: Int) {
        println("playing sound: $sound")
        val streamID = soundPool.play(sound, 1f, 1f, 1, 0, 1f)
        println("streamID: $streamID")
    }

    fun playSound(sound: TimerEndSound) {
        playSound(sound.soundID)
    }

    fun getSoundByName(name: String): TimerEndSound? {
        for (sound in sounds) {
            if (sound.name == name) return sound
        }
        return null
    }

    data class TimerEndSound(
        val resId: Int,
        val name: String,
        val playAtMsLeft: Int,
        var soundID: Int = -1
    )
}