package com.han2dev.supertime_v0

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class TimerSetupTest {
	@get:Rule
	val rule = createComposeRule()

	@Test
	fun test() {
		val timerLoop = TimerLoop()
		//timerLoop.childrenTimers.add(TimerElem(5000))
		rule.setContent { TimeLI(timer = TimerElem()) }
		rule.onNodeWithTag("minutesField").performTextInput("1")
		rule.onNodeWithTag("secondsField").performTextInput("30")
		rule.onNodeWithText(" min  ").assertExists()
		rule.onNodeWithText(" sec").assertExists()
		rule.onNodeWithText("1").assertExists()
		rule.onNodeWithText("30").assertExists()
	}
}