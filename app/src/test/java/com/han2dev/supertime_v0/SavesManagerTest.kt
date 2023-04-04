package com.han2dev.supertime_v0

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SavesManagerTest {

    @Test
    fun `single TimerElem`(){
        val timer1 = TimerElem(5)
        val json: String = SavesManager.timerToJson(timer1)
        println(json)
        val timer2 = SavesManager.timerFromJson(json)
        assertThat(timer2).isEqualTo(timer1)
    }

    @Test
    fun `TimerLoop with 2 TimerElem`(){
        val timer1 = TimerLoop(2)
        timer1.childrenTimers.add(TimerElem(5))
        timer1.childrenTimers.add(TimerElem(3))

        val json: String = SavesManager.timerToJson(timer1)
        println(json)
        val timer2 = SavesManager.timerFromJson(json)
        assertThat(timer2).isEqualTo(timer1)
    }

    @Test
    fun `TimerLoop with 2 TimerLoop`(){
        val timer1 = TimerLoop(2)

        val loop1 = TimerLoop(1)
        loop1.childrenTimers.add(TimerElem(5))
        timer1.childrenTimers.add(loop1)

        val loop2 = TimerLoop(10)
        loop2.childrenTimers.add(TimerElem(3))
        timer1.childrenTimers.add(loop2)

        val json: String = SavesManager.timerToJson(timer1)
        println(json)
        val timer2 = SavesManager.timerFromJson(json)
        assertThat(timer2).isEqualTo(timer1)
    }
}