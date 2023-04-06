package com.han2dev.supertime_v0

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class SavesManagerTest {

	private val context: Context = ApplicationProvider.getApplicationContext()
	private lateinit var timer1: TimerLoop
	private lateinit var timer2: TimerLoop

	@Before
	fun setUp() {
		timer1 = TimerLoop(2)
		timer1.childrenTimers.add(TimerElem(5))
		timer1.childrenTimers.add(TimerElem(3))

		timer2 = TimerLoop(2)

		val loop1 = TimerLoop(1)
		loop1.childrenTimers.add(TimerElem(5))
		timer2.childrenTimers.add(loop1)

		val loop2 = TimerLoop(10)
		loop2.childrenTimers.add(TimerElem(3))
		timer2.childrenTimers.add(loop2)
	}

	@After
	fun tearDown() {
		val files = context.filesDir.listFiles()
		files.forEach { println("filename: $it.name") }
		for (file in files) {
			file.delete()
		}
	}


	@Test
	fun convert1ToAndFromJson_returnSame() {
		val json = SavesManager.timerToJson(timer1)
		val result = SavesManager.timerFromJson(json)
		assertThat(result).isEqualTo(timer1)
	}

	@Test
	fun convert2ToAndFromJson_returnSame() {
		val json = SavesManager.timerToJson(timer2)
		val result = SavesManager.timerFromJson(json)
		assertThat(result).isEqualTo(timer2)
	}

	@Test
	fun saveAndLoad1Timer_returnSame(){
		timer2.name = "timer2"
		SavesManager.save(context, timer2)
		val result2 = SavesManager.load(context, timer2.name)
		assertThat(result2).isEqualTo(timer2)
	}

	@Test
	fun saveAndLoad2Timers_returnSame(){
		timer2.name = "timer2"
		timer1.name = "timer1"

		SavesManager.save(context, timer2)
		val result2 = SavesManager.load(context, timer2.name)

		SavesManager.save(context, timer1)
		val result1 = SavesManager.load(context, timer1.name)

		assertThat(result2).isEqualTo(timer2)
		assertThat(result1).isEqualTo(timer1)
	}

	@Test
	fun loadNonExistingFile_returnErrorAsTitle(){
		val result = SavesManager.load(context, "nonExistingFile")
		assertThat(result.name).isEqualTo("Error: File not found")
	}

	@Test
	fun saveFileWithExistingName_returnFalse(){
		timer1.name = "timer1"
		timer2.name = "timer1"
		SavesManager.save(context, timer1)
		val result = SavesManager.save(context, timer2)
		assertThat(result).isFalse()
	}

	@Test
	fun SaveUsingConvertToAvailableName_returnTrue(){
		timer1.name = "timer"
		timer2.name = SavesManager.convertToAvailableFilename(context, "timer")
		SavesManager.save(context, timer1)
		val result = SavesManager.save(context, timer2)
		assertThat(result).isTrue()
	}
}