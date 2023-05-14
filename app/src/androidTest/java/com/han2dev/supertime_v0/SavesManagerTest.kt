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
	private lateinit var alarm2: AlarmItem

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

		alarm2 = AlarmItem(
			name = "alarm2",
			time = 2000,
			enabled = false)

		val files = context.filesDir.listFiles()
		files.forEach { it.delete() }
		files.forEach { Log.d(this::class.simpleName, "filename: ${it.name}") }
	}

	@After
	fun tearDown() {
		val files = context.filesDir.listFiles()
		files.forEach { Log.d(this::class.simpleName, "filename: ${it.name}") }
	}


	//region Timers
	@Test
	fun saveAndLoad1Timer_returnEqual(){
		timer2.name = "timer2"
		SavesManager.save(context, timer2)
		val result2: TimerData? = SavesManager.loadTimer(context, timer2.name)
		assertThat(result2).isEqualTo(timer2)
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
	fun saveTimerWithExistingNameWithOverrideSetFalse_returnFalse(){
		timer1.name = "sameName"
		timer2.name = "sameName"
		SavesManager.save(context, timer1, false)
		val saved = SavesManager.save(context, timer2, false)
		assertThat(saved).isFalse()
	}

	@Test
	fun saveTimerUsingConvertToAvailableName_returnTrue(){
		timer1.name = "timer"
		SavesManager.save(context, timer1)

		timer2.name = SavesManager.toAvailableTimerName(context, "timer")
		val result = SavesManager.save(context, timer2, false)
		assertThat(result).isTrue()
	}

	@Test
	fun renameTimer_returnTrue(){
		timer1.name = "timer1"
		SavesManager.save(context, timer1)
		timer1.name = "newName"
		val result = SavesManager.renameTimer(context, "timer1", timer1.name)
		assertThat(result).isTrue()
		assertThat(SavesManager.loadTimer(context, "newName")).isEqualTo(timer1)
	}

	@Test
	fun renameNonExistingTimer_returnFalse(){
		val result = SavesManager.renameTimer(context, "nonExistingTimer", "newName")
		assertThat(result).isFalse()
	}

	@Test
	fun renameTimerToExistingName_returnFalse(){
		timer1.name = "timer1"
		timer2.name = "timer2"
		SavesManager.save(context, timer1)
		SavesManager.save(context, timer2)
		val result = SavesManager.renameTimer(context, "timer1", "timer2")
		assertThat(result).isFalse()
	}

	@Test
	fun deleteTimer_returnTrue(){
		timer1.name = "timer1"
		SavesManager.save(context, timer1)
		val result = SavesManager.deleteTimer(context, "timer1")
		assertThat(result).isTrue()
	}

	@Test
	fun deleteNonExistingTimer_returnFalse(){
		val result = SavesManager.deleteTimer(context, "nonExistingTimer")
		assertThat(result).isFalse()
	}
	//endregion

	//region Alarms
	@Test
	fun saveAndLoad1Alarm_returnEqual(){
		alarm1.name = "alarm1"

		SavesManager.save(context, alarm1)
		val result1: AlarmItem? = SavesManager.loadAlarm(context, alarm1.name)

		assertThat(result1).isEqualTo(alarm1)
	}

	@Test
	fun saveAlarmWithExistingName_overrideAndReturnTrue(){
		alarm1.name = "sameName"
		alarm2.name = "sameName"
		SavesManager.save(context, alarm1)
		val saved = SavesManager.save(context, alarm2)
		assertThat(saved).isTrue()
		val result: AlarmItem? = SavesManager.loadAlarm(context, alarm2.name)
		assertThat(result).isEqualTo(alarm2)
	}

	@Test
	fun saveAlarmWithExistingNameWithOverrideSetFalse_returnFalse(){
		alarm1.name = "sameName"
		alarm2.name = "sameName"
		SavesManager.save(context, alarm1, false)
		val saved = SavesManager.save(context, alarm2, false)
		assertThat(saved).isFalse()
		assertThat(SavesManager.loadAlarm(context, "sameName")).isEqualTo(alarm1)
	}

	@Test
	fun saveAlarmUsingConvertToAvailableName_returnTrue(){
		alarm1.name = "alarm"
		SavesManager.save(context, alarm1)

		alarm2.name = SavesManager.toAvailableAlarmName(context, "alarm")
		val result = SavesManager.save(context, alarm2, false)
		assertThat(result).isTrue()
		assertThat(SavesManager.loadAlarm(context, alarm2.name)).isEqualTo(alarm2)
	}

	@Test
	fun renameAlarm_returnTrue(){
		alarm1.name = "alarm1"
		SavesManager.save(context, alarm1)
		alarm1.name = "newName"
		val result = SavesManager.renameAlarm(context, "alarm1", alarm1.name)
		assertThat(result).isTrue()
		assertThat(SavesManager.loadAlarm(context, "newName")).isEqualTo(alarm1)
	}

	@Test
	fun deleteAlarm_returnTrue(){
		alarm1.name = "alarm1"
		SavesManager.save(context, alarm1)
		val result = SavesManager.deleteAlarm(context, "alarm1")
		assertThat(result).isTrue()
		assertThat(SavesManager.loadAlarm(context, "alarm1")).isNull()
	}

	@Test
	fun deleteNonExistingAlarm_returnFalse(){
		val result = SavesManager.deleteAlarm(context, "nonExistingAlarm")
		assertThat(result).isFalse()
	}
	//endregion

	//region Timers x Alarms
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
	fun loadNonExistingFile_returnNull(){
		val result: TimerData? = SavesManager.loadTimer(context, "nonExistingFile")
		assertThat(result).isNull()
		val result2: AlarmItem? = SavesManager.loadAlarm(context, "nonExistingFile")
		assertThat(result2).isNull()
	}
	//endregion
}