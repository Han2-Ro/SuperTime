package com.han2dev.supertime_v0

import android.os.CountDownTimer

/**
 * formats time to string
 * @param millis time in ms
 * @return formatted time (MM:SS.cs)
 */
fun formatTime(millis: Long): String {
    val centis: Long = millis / 10 % 100
    val second: Long = millis / 1000 % 60
    val minute: Long = millis / (1000 * 60) //% 60
    //val hour: Long = millis / (1000 * 60 * 60) % 24

    return String.format("%02d:%02d.%02d", minute, second, centis)
}

fun timerFromData(data: TimerData): Timer {
    return when (data) {
        is TimerElemData -> TimerElem(data)
        is TimerLoopData -> TimerLoop(data)
    }
}

abstract class Timer() {
    abstract val data: TimerData
    protected lateinit var parent: TimerParent
    var endSound: SoundManager.TimerEndSound? = null
    abstract fun start(parent: TimerParent)
    abstract fun pause()
    abstract fun resume()
}

interface TimerParent {
    fun next()
    fun update(time: Long, cyclesLeft: MutableList<Int>)
    fun setSound(sound: SoundManager.TimerEndSound?)
}

class TimerLoop(override val data: TimerLoopData) : Timer(), TimerParent {
    private var repeatsLeft: Int = 1
    private var currentTimer: Int = 0
    var childrenTimers: MutableList<Timer> = data.childrenTimers.map { timerData ->
        timerFromData(timerData)
    }.toMutableList()

    override fun start(parent: TimerParent) {
        this.parent = parent
        repeatsLeft = data.repeats
        currentTimer = 0

        if(childrenTimers.size > currentTimer) {
            childrenTimers[currentTimer].start(this)
        }
        else{
            parent.next()
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
            childrenTimers[currentTimer].start(this)
        }
        else{
            currentTimer = 0
            if(repeatsLeft > 1){
                repeatsLeft--
                childrenTimers[currentTimer].start(this)
            }
            else{
                parent.next()
            }
        }
    }

    override fun update(time: Long, cyclesLeft: MutableList<Int>) {
        cyclesLeft.add(repeatsLeft)
        parent.update(time, cyclesLeft)
    }

    //TODO: consider doing this in the actual setter method
    override fun setSound(sound: SoundManager.TimerEndSound?) {
        parent.setSound(sound) //TODO: set own sound if last in last cycle
    }


    override fun equals(other: Any?): Boolean {
        if (other !is TimerLoop) return false
        return other.data.repeats == data.repeats && other.childrenTimers == childrenTimers
    }

    override fun hashCode(): Int {
        var result = data.repeats
        result = 31 * result + childrenTimers.hashCode()
        return result
    }
}


class TimerElem(override val data: TimerElemData) : Timer() {
    private lateinit var cdTimer: CountDownTimer
    private var timeRemaining: Long = data.durationMillis


    override fun start(parent: TimerParent) {
        //set up
        this.parent = parent
        parent.setSound(endSound)
        timeRemaining = data.durationMillis

        //start timer
        resume()
    }

    override fun pause() {
        cdTimer.cancel()
    }

    override fun resume() {
        // set up timer
        cdTimer = object : CountDownTimer(timeRemaining,10){
            override fun onTick(millisUntilFinished: Long) {
                parent.update(millisUntilFinished, mutableListOf())
                timeRemaining = millisUntilFinished
            }
            override fun onFinish() {
                onTimerEnd()
            }
        }

        //start
        cdTimer.start()
    }

    fun onTimerEnd() {
        println("finished")
        parent.update(0, mutableListOf())
        parent.next()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TimerElem) return false

        return other.data.durationMillis == data.durationMillis
    }

    override fun hashCode(): Int {
        return data.durationMillis.hashCode()
    }
}