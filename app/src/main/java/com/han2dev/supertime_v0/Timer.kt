package com.han2dev.supertime_v0

import android.os.CountDownTimer


abstract class Timer(val name: String) : java.io.Serializable {
    protected lateinit var parent: TimerParent
    abstract fun start(parent: TimerParent)
    abstract fun pause()
    abstract fun resume()
    abstract fun clone(): Timer
}

interface TimerParent {
    fun next()
    fun update(time: Long, cyclesLeft: MutableList<Int>)
}

class TimerLoop(var repeats: Int = 1, name: String  = "untitled") : Timer(name), TimerParent {
    private var repeatsLeft: Int = 1
    private var currentTimer: Int = 0
    var childrenTimers: MutableList<Timer> = mutableListOf()

    override fun start(parent: TimerParent) {
        this.parent = parent
        repeatsLeft = repeats
        currentTimer = 0

        if(childrenTimers.size > currentTimer) {
            childrenTimers[currentTimer].start(this)
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
    private var timeRemaining: Long = duration

    override fun start(parent: TimerParent) {
        this.parent = parent
        timeRemaining = duration

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
        SoundManager.playSound(SoundManager.sound1)
        parent.update(0, mutableListOf())
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