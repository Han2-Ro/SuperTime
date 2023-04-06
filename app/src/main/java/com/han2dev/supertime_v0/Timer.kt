package com.han2dev.supertime_v0

import android.os.CountDownTimer


abstract class Timer(val name: String) : java.io.Serializable {
    protected lateinit var parent: TimerParent
    protected var endSound: SoundManager.TimerEndSound? = null
    abstract fun start(parent: TimerParent)
    abstract fun pause()
    abstract fun resume()
    abstract fun clone(): Timer
}

interface TimerParent {
    fun next()
    fun update(time: Long, cyclesLeft: MutableList<Int>)
    fun setSound(sound: SoundManager.TimerEndSound?)
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

    override fun hashCode(): Int {
        var result = repeats
        result = 31 * result + childrenTimers.hashCode()
        return result
    }
}


class TimerElem(val duration: Long = 0, name: String  = "untitled") : Timer(name) {
    private lateinit var cdTimer: CountDownTimer
    private var timeRemaining: Long = duration

    init {
        endSound = SoundManager.TimerEndSound(SoundManager.sound1, 1)
    }

    override fun start(parent: TimerParent) {
        //set up
        this.parent = parent
        parent.setSound(endSound)
        timeRemaining = duration

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

    override fun hashCode(): Int {
        return duration.hashCode()
    }
}