package com.han2dev.supertime_v0

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerSetupTest {
	private lateinit var scenario: ActivityScenario<Activity>

	@get:Rule
	val rule = createEmptyComposeRule()
	private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext


	@Before
	fun setUp() {
		val timer = TimerLoopData(
			name = "test",
			childrenTimers = listOf(
				TimerElemData(),
				TimerElemData()))
		SavesManager.save(context, timer)
	}

	@After
	fun tearDown() {
		SavesManager.deleteAll(context)
		if (::scenario.isInitialized) scenario.close()
	}

	private fun createActivityIntent(
		timerId: String = "test",
	): Intent {
		val intent = Intent(context, NewTimerSetupActivity::class.java)
		intent.putExtra("timer_id", timerId)
		return intent
	}


	@Test
	fun test_timeTextFieldInput() {
		scenario = ActivityScenario.launch(
			createActivityIntent()
		)

		val minutesField = rule.onAllNodesWithTag("minutesField").onFirst()
		minutesField.performTextClearance()
		minutesField.performTextInput("1")

		val secondsField = rule.onAllNodesWithTag("secondsField").onFirst()
		secondsField.performTextClearance()
		secondsField.performTextInput("30")

		rule.onAllNodesWithText(" min  ").onFirst().assertExists()
		rule.onAllNodesWithText(" sec").onFirst().assertExists()
		rule.onAllNodesWithText("1").onFirst().assertExists()
		rule.onAllNodesWithText("30").onFirst().assertExists()
	}

	@Test
	fun test_repeatsTextFieldInput() {
		scenario = ActivityScenario.launch(
			createActivityIntent()
		)

		val repeatsField = rule.onAllNodesWithTag("repeatsField").onFirst()
		repeatsField.performTextClearance()
		repeatsField.performTextInput("22")

		rule.onAllNodesWithText("22").onFirst().assertExists()
	}

	@Test
	fun goBack() {
		scenario = ActivityScenario.launch(
			createActivityIntent("nonexistent")
		)

		rule.onNodeWithText("Go back").performClick()
	}

	@Test
	fun editAndSaveTimer_loadTimer_isEqual() {
		val timer1 = TimerLoopData(name = "test", childrenTimers = listOf(TimerElemData()))
		SavesManager.save(context, timer1)

		scenario = ActivityScenario.launch(
			createActivityIntent()
		)

		rule.onNodeWithTag("minutesField").performTextClearance()
		rule.onNodeWithTag("minutesField").performTextInput("1")
		rule.onNodeWithTag("secondsField").performTextClearance()
		rule.onNodeWithTag("secondsField").performTextInput("30")
		rule.onNodeWithTag("repeatsField").performTextClearance()
		rule.onNodeWithTag("repeatsField").performTextInput("2")

		rule.onNodeWithTag("saveButton").performClick()

		Thread.sleep(500)

		val timer2 = SavesManager.load(context, "test") as TimerLoopData
		assert(timer2.childrenTimers.size == 1)
		assert(timer2.repeats == 2)
		assert(timer2.childrenTimers[0] is TimerElemData)
		assert((timer2.childrenTimers[0] as TimerElemData).durationMillis == 90000L)
	}
}