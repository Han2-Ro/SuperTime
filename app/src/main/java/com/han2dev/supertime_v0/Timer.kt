package com.han2dev.supertime_v0

import android.app.Activity
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CountDownTimer
import android.widget.TextView

fun formatTime(millis: Long): String {
    //formatting from ms to MM:SS.cs
    val centis: Long = millis / 10 % 100
    val second: Long = millis / 1000 % 60
    val minute: Long = millis / (1000 * 60) //% 60
    //val hour: Long = millis / (1000 * 60 * 60) % 24

    return String.format("%02d:%02d.%02d", minute, second, centis)
}


abstract class Timer: java.io.Serializable {
    protected lateinit var activity: Activity
    protected lateinit var parent: TimerParent
    abstract fun start(activity: Activity, parent: TimerParent)
    abstract fun pause()
    abstract fun resume()
}

interface TimerParent {
    fun next()
}

class TimerLoop(val repeats: Int = 2) : Timer(), TimerParent {
    private var repeatsLeft: Int = 1
    private var currentTimer: Int = 0
    var timer: MutableList<Timer> = mutableListOf()
    private lateinit var txtCycles: TextView

    override fun start(activity: Activity, parent: TimerParent) {
        this.activity = activity
        this.parent = parent
        repeatsLeft = repeats
        currentTimer = 0

        txtCycles  = activity.findViewById(R.id.txtCycles)
        txtCycles.text = repeatsLeft.toString()//txtCycles.text.toString().replace("<n>;", repeats.toString())

        if(timer.size > currentTimer) {
            timer[currentTimer].start(activity, this)
        }
    }

    override fun pause() {
        timer[currentTimer].pause()
    }

    override fun resume() {
        timer[currentTimer].resume()
    }

    override fun next() {
        currentTimer++
        if(currentTimer < timer.size){
            timer[currentTimer].start(activity, this)
        }
        else{
            if(repeatsLeft > 1){
                repeatsLeft--
                currentTimer = 0
                timer[currentTimer].start(activity, this)
                txtCycles.text = repeatsLeft.toString()//txtCycles.text.toString().replace("<n>", repeats.toString())
            }
            else{
                parent.next()
            }
        }
    }
}


class TimerElem(val time: Long) : Timer() {
    private lateinit var cdTimer: CountDownTimer
    private lateinit var soundPool: SoundPool
    private var sound1: Int = -1
    private lateinit var txtTime: TextView
    private var timeRemaining: Long = time

    override fun start(activity: Activity, parent: TimerParent) {
        this.activity = activity
        this.parent = parent
        timeRemaining = time
        txtTime = activity.findViewById(R.id.txtTime)

        // set up soundPool
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        sound1 = soundPool.load(activity, R.raw.sound1, 1)

        //start timer
        timeRemaining = time
        resume()
    }

    override fun pause() {
        cdTimer.cancel()
    }

    override fun resume() {
        // set up timer
        cdTimer = object : CountDownTimer(timeRemaining,10){
            override fun onTick(millisUntilFinished: Long) {
                updateTime(millisUntilFinished)
            }
            override fun onFinish() {
                onTimerEnd()
            }
        }

        //start
        cdTimer.start()
    }

    fun updateTime(millisUntilFinished: Long) {
        txtTime.text = formatTime(millisUntilFinished)

        //update the global remainingTime
        timeRemaining = millisUntilFinished
    }

    fun onTimerEnd() {
        println("finished")
        soundPool.play(sound1, 1f, 1f, 0, 0, 1f)
        txtTime.text = "00:00.00"
        parent.next()
    }
}