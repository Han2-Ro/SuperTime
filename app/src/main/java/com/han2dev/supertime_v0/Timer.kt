package com.han2dev.supertime_v0

import android.app.Activity
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CountDownTimer
import android.widget.TextView
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

fun formatTime(millis: Long): String {
    //formatting from ms to MM:SS.cs
    val centis: Long = millis / 10 % 100
    val second: Long = millis / 1000 % 60
    val minute: Long = millis / (1000 * 60) //% 60
    //val hour: Long = millis / (1000 * 60 * 60) % 24

    return String.format("%02d:%02d.%02d", minute, second, centis)
}


@Serializable
abstract class Timer(val name: String) : java.io.Serializable {
    protected lateinit var parent: TimerParent
    abstract fun start(activity: Activity, parent: TimerParent)
    abstract fun pause()
    abstract fun resume()
    abstract fun clone(): Timer
}

interface TimerParent {
    fun next()
}

class TimerLoop(var repeats: Int = 1, name: String  = "untitled") : Timer(name), TimerParent {
    @Transient private lateinit var activity: Activity
    private var repeatsLeft: Int = 1
    private var currentTimer: Int = 0
    var childrenTimers: MutableList<Timer> = mutableListOf()
    @Transient private lateinit var txtCycles: TextView

    override fun start(activity: Activity, parent: TimerParent) {
        this.activity = activity
        this.parent = parent
        repeatsLeft = repeats
        currentTimer = 0

        txtCycles  = activity.findViewById(R.id.txtCycles)
        txtCycles.text = repeatsLeft.toString()//txtCycles.text.toString().replace("<n>;", repeats.toString())

        if(childrenTimers.size > currentTimer) {
            childrenTimers[currentTimer].start(activity, this)
        }
    }

    override fun pause() {
        childrenTimers[currentTimer].pause()
    }

    override fun resume() {
        childrenTimers[currentTimer].resume()
    }

    override fun next() {
        currentTimer++
        if(currentTimer < childrenTimers.size){
            childrenTimers[currentTimer].start(activity, this)
        }
        else{
            currentTimer = 0
            if(repeatsLeft > 1){
                repeatsLeft--
                childrenTimers[currentTimer].start(activity, this)
                txtCycles.text = repeatsLeft.toString()//txtCycles.text.toString().replace("<n>", repeats.toString())
            }
            else{
                parent.next()
            }
        }
    }

    override fun clone(): TimerLoop {
        println("Copied Loop")
        val copy = TimerLoop(repeats)
        copy.childrenTimers = childrenTimers.toMutableList()
        return copy
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TimerLoop) return false
        return other.repeats == repeats && other.childrenTimers == childrenTimers
    }
}


class TimerElem(val duration: Long = 0, name: String  = "untitled") : Timer(name) {
    private lateinit var cdTimer: CountDownTimer
    private lateinit var soundPool: SoundPool
    private var sound1: Int = -1
    private lateinit var txtTime: TextView
    private var timeRemaining: Long = duration

    override fun start(activity: Activity, parent: TimerParent) {
        this.parent = parent
        timeRemaining = duration
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
        timeRemaining = duration
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

    override fun clone(): TimerElem {
        println("copied TimerElem")
        return TimerElem(duration)
    }

    fun getSeconds(): Long {
        return duration / 1000 % 60
    }

    fun getMinutes(): Long {
        return duration / (1000 * 60)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TimerElem) return false

        return other.duration == duration
    }
}