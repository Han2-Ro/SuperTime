package com.han2dev.supertime_v0

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class TimerSetupTest {
	@get:Rule
	val rule = createComposeRule()//createAndroidComposeRule(NewTimerSetupActivity::class.java)

	@Test
	fun test_textFieldInput() {
		val timerLoop = TimerLoop()
		timerLoop.childrenTimers.add(TimerElem(5000))
		rule.setContent { TimeLI(timer = TimerElem()) }
		Thread.sleep(1000)
		rule.onNodeWithTag("minutesField").performTextClearance()
		rule.onNodeWithTag("secondsField").performTextClearance()
		rule.onNodeWithTag("minutesField").performTextInput("1")
		rule.onNodeWithTag("secondsField").performTextInput("30")
		Thread.sleep(1000)
		rule.onNodeWithText(" min  ").assertExists()
		rule.onNodeWithText(" sec").assertExists()
		rule.onNodeWithText("1").assertExists()
		rule.onNodeWithText("30").assertExists()
	}
}