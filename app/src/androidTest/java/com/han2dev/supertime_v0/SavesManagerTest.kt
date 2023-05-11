package com.han2dev.supertime_v0

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class SavesManagerTest {

	private val context: Context = ApplicationProvider.getApplicationContext()
	private lateinit var timer1: TimerLoopData
	private lateinit var timer2: TimerLoopData
	private lateinit var alarm1: AlarmItem

	@Before
	fun setUp() {
		timer1 = TimerLoopData(
			childrenTimers = listOf(
				TimerElemData(durationMillis = 5000),
				TimerElemData(durationMillis = 3000)),
			repeats = 2)

		timer2 = TimerLoopData(
			childrenTimers = listOf(
				TimerLoopData(
					childrenTimers = listOf(
						TimerElemData(durationMillis = 5000)),
					repeats = 1),
				TimerLoopData(
					childrenTimers = listOf(
						TimerElemData(durationMillis = 3000)),
					repeats = 10)),
			repeats = 1)

		alarm1 = AlarmItem(
			name = "alarm1",
			time = 0,
			enabled = false)
	}

	@After
	fun tearDown() {
		val files = context.filesDir.listFiles()
		files.forEach { Log.d(this::class.simpleName, "filename: ${it.name}") }
		for (file in files) {
			file.delete()
		}
	}

	@Test
	fun saveAndLoad1Timer_returnEqual(){
		timer2.name = "timer2"
		SavesManager.save(context, timer2)
		val result2: TimerData? = SavesManager.loadTimer(context, timer2.name)
		assertThat(result2).isEqualTo(timer2)
	}

	@Test
	fun saveAndLoad1Alarm_returnEqual(){
		alarm1.name = "alarm1"

		SavesManager.save(context, alarm1)
		val result1: AlarmItem? = SavesManager.loadAlarm(context, alarm1.name)

		assertThat(result1).isEqualTo(alarm1)
	}

	@Test
	fun saveAndLoadTimerAndAlarmWithSameName_returnEqual(){
		timer1.name = "sameName"
		alarm1.name = "sameName"

		SavesManager.save(context, timer1)
		SavesManager.save(context, alarm1)

		val resultTimer: TimerData? = SavesManager.loadTimer(context, timer1.name)
		val resultAlarm: AlarmItem? = SavesManager.loadAlarm(context, alarm1.name)

		assertThat(resultTimer).isEqualTo(timer1)
		assertThat(resultAlarm).isEqualTo(alarm1)
	}

	@Test
	fun saveAndLoad2Timers_returnEqual(){
		timer2.name = "timer2"
		timer1.name = "timer1"

		SavesManager.save(context, timer2)
		val result2: TimerData? = SavesManager.loadTimer(context, timer2.name)

		SavesManager.save(context, timer1)
		val result1: TimerData? = SavesManager.loadTimer(context, timer1.name)

		assertThat(result2).isEqualTo(timer2)
		assertThat(result1).isEqualTo(timer1)
	}

	@Test
	fun loadNonExistingFile_returnNull(){
		val result: TimerData? = SavesManager.loadTimer(context, "nonExistingFile")
		assertThat(result).isNull()
	}

	@Test
	fun saveTimerWithExistingName_overrideAndReturnTrue(){
		timer1.name = "sameName"
		timer2.name = "sameName"
		SavesManager.save(context, timer1)
		val saved = SavesManager.save(context, timer2)
		assertThat(saved).isTrue()
		val result: TimerData? = SavesManager.loadTimer(context, timer2.name)
		assertThat(result).isEqualTo(timer2)
	}

	@Test
	fun saveTimerUsingConvertToAvailableName_returnTrue(){
		timer1.name = "timer"
		SavesManager.save(context, timer1)

		timer2.name = SavesManager.convertToAvailableFilename(context, "timer")
		val result = SavesManager.save(context, timer2)
		assertThat(result).isTrue()
	}

	@Test
	fun renameTimer_returnTrue(){
		timer1.name = "timer1"
		SavesManager.save(context, timer1)
		val result = SavesManager.rename(context, "timer1", "newName")
		assertThat(result).isTrue()
	}

	@Test
	fun renameNonExistingTimer_returnFalse(){
		val result = SavesManager.rename(context, "nonExistingTimer", "newName")
		assertThat(result).isFalse()
	}

	@Test
	fun renameTimerToExistingName_returnFalse(){
		timer1.name = "timer1"
		timer2.name = "timer2"
		SavesManager.save(context, timer1)
		SavesManager.save(context, timer2)
		val result = SavesManager.rename(context, "timer1", "timer2")
		assertThat(result).isFalse()
	}

	@Test
	fun deleteTimer_returnTrue(){
		timer1.name = "timer1"
		SavesManager.save(context, timer1)
		val result = SavesManager.delete(context, "timer1")
		assertThat(result).isTrue()
	}

	@Test
	fun deleteNonExistingTimer_returnFalse(){
		val result = SavesManager.delete(context, "nonExistingTimer")
		assertThat(result).isFalse()
	}
}