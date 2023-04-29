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
		val timer = TimerLoop(name = "test")
		timer.childrenTimers.add(TimerElem())
		timer.childrenTimers.add(TimerElem())
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

		Thread.sleep(500)
		rule.onNodeWithTag("minutesField").performTextClearance()
		rule.onNodeWithTag("minutesField").performTextInput("1")
		rule.onNodeWithTag("secondsField").performTextClearance()
		rule.onNodeWithTag("secondsField").performTextInput("30")
		Thread.sleep(5000)
		rule.onNodeWithText(" min  ").assertExists()
		rule.onNodeWithText(" sec").assertExists()
		rule.onNodeWithText("1").assertExists()
		rule.onNodeWithText("30").assertExists()
	}

	@Test
	fun test_repeatsTextFieldInput() {
		scenario = ActivityScenario.launch(
			createActivityIntent()
		)

		Thread.sleep(500)
		rule.onNodeWithTag("repeatsField").performTextClearance()
		rule.onNodeWithTag("repeatsField").performTextInput("22")
		Thread.sleep(5000)
		rule.onNodeWithText("22").assertExists()
	}

	@Test
	fun goBack() {
		scenario = ActivityScenario.launch(
			createActivityIntent("nonexistent")
		)

		Thread.sleep(500)
		rule.onNodeWithText("Go back").performClick()
		Thread.sleep(500)
	}

	@Test
	fun editAndSaveTimer_loadTimer_isEqual() {
		scenario = ActivityScenario.launch(
			createActivityIntent()
		)

		rule.onNodeWithTag("minutesField").performTextClearance()
		rule.onNodeWithTag("minutesField").performTextInput("1")
		rule.onNodeWithTag("secondsField").performTextClearance()
		rule.onNodeWithTag("secondsField").performTextInput("30")
		Thread.sleep(3000)
		rule.onNodeWithTag("saveButton").performClick()
		Thread.sleep(500)
		val timer = SavesManager.load(context, "test") as TimerElem
		assert(timer.durationMillis == 90000L)
	}
}