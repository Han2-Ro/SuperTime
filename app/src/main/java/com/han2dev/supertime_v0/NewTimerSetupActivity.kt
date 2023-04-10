package com.han2dev.supertime_v0

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme

class NewTimerSetupActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val timerId = intent.getStringExtra("timer_id")
			?: throw NullPointerException("Found no \"timer_name\": String in intent extra.")
		println("name from intent: $timerId")
		val timer: Timer = SavesManager.load(this, timerId) ?: throw IllegalArgumentException("timer_id could not be parsed to Timer")

		title = timer.name

		setContent {
			SuperTime_v0Theme {
				Box (modifier = Modifier
					.fillMaxSize()
					.background(Color.Cyan)
					.padding(0.dp)
				) {
					LoopLI(timer as TimerLoop)
				}
			}
		}
	}
}

@Composable
fun MyListItem(backgroundColor: Color = Color.LightGray, TopRowContent: @Composable (Modifier) -> Unit, ExtraContent: @Composable () -> Unit = {}) {

	Column(
		modifier = Modifier
			.padding(20.dp, 5.dp, 2.dp, 5.dp)
			.clip(RoundedCornerShape(10.dp))
			.background(backgroundColor)
	){
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(10.dp)

		) {
			TopRowContent(
				Modifier
					.weight(1f)
					.wrapContentHeight())
			
			Spacer(modifier = Modifier.width(20.dp))

			Icon(
				imageVector = Icons.Default.Settings,
				contentDescription = null,
				modifier = Modifier.clickable { /*TODO*/ }
			)

			Column {
				Icon(
					imageVector = Icons.Default.KeyboardArrowUp,
					contentDescription = null,
					modifier = Modifier
						.offset(0.dp, 5.dp)
						.clickable { /*TODO*/ }
				)
				Icon(
					imageVector = Icons.Default.KeyboardArrowDown,
					contentDescription = null,
					modifier = Modifier
						.offset(0.dp, (-5).dp)
						.clickable { /*TODO*/ }
				)
			}
		}


		Column {
			ExtraContent()
		}

	}

}

@Composable
fun TimeLI(timer: TimerElem) {
	val timeStr = formatTime(timer.duration)
	val minutes: MutableState<Int?> = remember {
		mutableStateOf(timeStr.substring(0, 2).toInt())
	}
	val seconds: MutableState<Int?> = remember {
		mutableStateOf(timeStr.substring(3, 5).toInt())
	}

	MyListItem(
		Color(0x70FFFFFF),
		TopRowContent = {modifier ->
			Row (
				verticalAlignment = Alignment.CenterVertically,
				modifier = modifier
			) {

				MyTextField(minutes)
				Text(text = " min  ")
				MyTextField(seconds)
				Text(text = " sec")
			}

	})
}

@Composable
fun LoopLI(timerLoop: TimerLoop) {
	val repeats: MutableState<Int?> = remember {
		mutableStateOf(timerLoop.repeats)
	}

	MyListItem(
		Color(0x20000000),
		{modifier ->
			Row (
				verticalAlignment = Alignment.CenterVertically,
				modifier = modifier)
			{
				Text(text = "repeat ")
				MyTextField(repeats)
				Text(text = " time(s)") //TODO: dynamic plural
			}
	},{
		Column(){
			for (timer in timerLoop.childrenTimers) {
				if (timer is TimerElem)
					TimeLI(timer)
				else if (timer is TimerLoop)
					LoopLI(timer)
			}
		}
	})
}

@Composable
private fun MyTextField(state: MutableState<Int?>) {
	BasicTextField(
		value = state.value?.toString() ?: "",
		textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		modifier = Modifier
			.width(40.dp)
			.border(1.dp, Color.DarkGray, RoundedCornerShape(5.dp))
			.clip(RoundedCornerShape(5.dp))
			.background(Color(0x38000000))
			.padding(10.dp, 5.dp),
		onValueChange = {
			state.value = it.toIntOrNull()
		}
	)
}

@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
	val timerLoop = TimerLoop(5)
	timerLoop.childrenTimers = mutableListOf(
		TimerElem(5000),
		TimerElem(10000)
	)
	val timerLoop2 = TimerLoop(2)
	timerLoop2.childrenTimers = mutableListOf(
		timerLoop,
		TimerElem(90000)
	)

	SuperTime_v0Theme {
		Box (modifier = Modifier
			.background(Color.Cyan)
			.padding(0.dp)
		) {
			LoopLI(timerLoop2)
		}
	}
}