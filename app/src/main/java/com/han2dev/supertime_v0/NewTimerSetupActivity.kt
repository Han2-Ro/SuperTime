package com.han2dev.supertime_v0

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme

class NewTimerSetupActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val viewModel = ViewModelProvider(this)[TimerSetupViewModel::class.java]
		viewModel.load(intent, this.applicationContext)

		title = "timer.value?.name"

		setContent {
			val timer by viewModel.timerNode.observeAsState()

			SuperTime_v0Theme {
				Base(timer, this)
			}
		}
	}
}

@Composable
fun Base(timer: TimerNode?, activity: Activity) {
	Scaffold (
		topBar = {
			TopAppBar(
				title = { Text(timer?.name ?: "Timer not found")},
				navigationIcon = {
					IconButton(onClick = { activity.finish() }) {
						Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
					}
				},
				actions = {
					IconButton(modifier = Modifier.testTag("saveButton"), onClick = { /*TODO*/ }) {
						Icon(Icons.Default.Done, contentDescription = "Delete")
					}
				}
			)
		}
	) {contentPadding ->
		Root(contentPadding, timer, activity)
	}
}

@Composable
fun Root(contentPadding: PaddingValues, timer: TimerNode?, activity: Activity) {
	Box(modifier = Modifier
		.fillMaxSize()
		.background(Color.Cyan)
		.padding(contentPadding)) {
		when (timer) {
			is TimerLoopNode -> {
				LoopLI(timer)
			}
			is TimerElemNode -> {
				TimeLI(timer)
			}
			null -> {
				Column {
					Text("Timer not found")
					Button(onClick = { activity.finish() }) {
						Text("Go back")
					}
				}
			}
		}
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
fun TimeLI(timer: TimerElemNode) {
	/*TODO: remove if not needed anymore
	val timeStr = formatTime(timer.durationMillis)
	val minutes: MutableState<Int?> = remember {
		mutableStateOf(timeStr.substring(0, 2).toInt())
	}
	val seconds: MutableState<Int?> = remember {
		mutableStateOf(timeStr.substring(3, 5).toInt())
	}*/

	MyListItem(
		Color(0x70FFFFFF),
		topRowContent = { modifier ->
			Row (
				verticalAlignment = Alignment.CenterVertically,
				modifier = modifier
			) {

				MyTextField(Modifier.testTag("minutesField"),
					value = timer.minutes.value,
					onValueChange = {timer.minutes.value = it})
				Text(text = " min  ")
				MyTextField(Modifier.testTag("secondsField"),
					value = timer.seconds.value,
					onValueChange = {timer.seconds.value = it})
				Text(text = " sec")
			}

	})
}

@Composable
fun LoopLI(timerLoop: TimerLoopNode) {
	/*val repeats: MutableState<Int?> = remember {
		mutableStateOf(timerLoop.repeats)
	}*/

	MyListItem(
		Color(0x20000000),
		topRowContent = {modifier ->
			Row (
				verticalAlignment = Alignment.CenterVertically,
				modifier = modifier)
			{
				Text(text = "repeat ")
				MyTextField(
					modifier = Modifier.testTag("repeatsField"),
					value = timerLoop.repeats.value,
					onValueChange = {
						timerLoop.repeats.value = it
					})
				Text(text = " time(s)") //TODO: dynamic plural
			}
	}
	) {
		LazyColumn(
			modifier = Modifier.wrapContentHeight()
		) {
			itemsIndexed(timerLoop.childrenTimers)
			{index, timer ->
				if (timer is TimerElemNode) {
					println("adding timeLI")
					TimeLI(timer)
				} else if (timer is TimerLoopNode) {
					println("adding loopLI")
					LoopLI(timer)
				}
			}
		}
	}
}

@Composable
private fun MyTextField(modifier: Modifier = Modifier, value: Int? = 0, onValueChange: (Int?) -> Unit = {}) {
	BasicTextField(
		value = value?.toString() ?: "",
		textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		modifier = modifier
			.width(40.dp)
			.border(1.dp, Color.DarkGray, RoundedCornerShape(5.dp))
			.clip(RoundedCornerShape(5.dp))
			.background(Color(0x38000000))
			.padding(10.dp, 5.dp),
		onValueChange = {
			onValueChange(it.toIntOrNull())
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
			TimeLI(timer = TimerElemNode("untitled"))
		}
	}
}