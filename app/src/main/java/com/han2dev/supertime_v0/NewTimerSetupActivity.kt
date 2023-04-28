package com.han2dev.supertime_v0

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme

class NewTimerSetupActivity : ComponentActivity() {

	val timer: MutableState<Timer?> = mutableStateOf(TimerLoop())
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val timerId = intent.getStringExtra("timer_id")
			?: throw NullPointerException("Found no \"timer_id\": String in intent extra.")
		println("name from intent: $timerId")
		timer.value = SavesManager.load(this, timerId) //?: throw IllegalArgumentException("timer_id '$timerId' could not be parsed to Timer")

		title = timer.value?.name

		setContent {
			SuperTime_v0Theme {
				Scaffold (
					topBar = {
						TopAppBar(
							title = { Text(timer.value?.name ?: "Timer not found")},
							navigationIcon = {
								IconButton(onClick = { finish() }) {
									Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
								}
							},
							actions = {
								IconButton(modifier = Modifier.testTag("saveButton"), onClick = { save() }) {
									Icon(Icons.Default.Done, contentDescription = "Delete")
								}
							}
							)
					}
				) {contentPadding ->
					Box(modifier = Modifier
						.fillMaxSize()
						.background(Color.Cyan)
						.padding(contentPadding)) {
						when (timer.value) {
							is TimerLoop -> {
								LoopLI(timer.value as TimerLoop)
							}
							is TimerElem -> {
								TimeLI(timer.value as TimerElem)
							}
							null -> {
								Column {
									Text("Timer not found")
									Button(onClick = { finish() }) {
										Text("Go back")
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private fun save() {
		TODO("Not yet implemented")
	}
}

@Composable
fun MyListItem(backgroundColor: Color = Color.LightGray, topRowContent: @Composable (Modifier) -> Unit, extraContent: @Composable () -> Unit = {}) {

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
			topRowContent(
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
						.clickable {
							/*TODO
							val currentPos: Int = adapterPosition
							if (currentPos > 0) {
								Collections.swap(parentAdapter.timerLoop.childrenTimers, currentPos, currentPos - 1)
								parentAdapter.notifyItemMoved(currentPos, currentPos - 1)

							} else {
								Toast.makeText(parentAdapter.context, "already at the top", Toast.LENGTH_SHORT).show()
							} */
						}
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
			extraContent()
		}

	}

}

@Composable
fun TimeLI(timer: TimerElem) {
	val timeStr = formatTime(timer.durationMillis)
	val minutes: MutableState<Int?> = remember {
		mutableStateOf(timeStr.substring(0, 2).toInt())
	}
	val seconds: MutableState<Int?> = remember {
		mutableStateOf(timeStr.substring(3, 5).toInt())
	}

	MyListItem(
		Color(0x70FFFFFF),
		topRowContent = { modifier ->
			Row (
				verticalAlignment = Alignment.CenterVertically,
				modifier = modifier
			) {

				MyTextField(minutes, modifier = Modifier.testTag("minutesField"))
				Text(text = " min  ")
				MyTextField(seconds, modifier = Modifier.testTag("secondsField"))
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
		LazyColumn (
			modifier = Modifier.wrapContentHeight()
		){
			items(timerLoop.childrenTimers)
			{ timer ->
				if (timer is TimerElem)
					TimeLI(timer)
				else if (timer is TimerLoop)
					LoopLI(timer)
			}
		}
	})
}

@Composable
private fun MyTextField(state: MutableState<Int?>, modifier: Modifier = Modifier) {
	BasicTextField(
		value = state.value?.toString() ?: "",
		textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		modifier = modifier
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
			TimeLI(timer = TimerElem(5000))
		}
	}
}