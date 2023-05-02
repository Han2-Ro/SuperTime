package com.han2dev.supertime_v0

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme

class TimerSetupActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val viewModel = ViewModelProvider(this)[TimerSetupViewModel::class.java]
		viewModel.load(intent, this.applicationContext)

		title = "timer.value?.name"

		setContent {
			SuperTime_v0Theme {
				Base(this, viewModel)
			}
		}
	}
}

@Composable
fun Base(activity: Activity, viewModel: TimerSetupViewModel) {
	val timer by viewModel.timerNode.observeAsState()
	var isAddMenuVisable = rememberSaveable {
		mutableStateOf(false)
	}

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
					IconButton(modifier = Modifier.testTag("addButton"), onClick = { isAddMenuVisable.value = true }) {
						Icon(Icons.Default.Add, contentDescription = "Add")
					}
					MyDropdownMenu(isAddMenuVisable, listOf(
						DropdownItem("Timer") { viewModel.addTimer(TimerElemData()) },
						DropdownItem("Loop") {viewModel.addTimer(TimerLoopData()) },
					))

					IconButton(modifier = Modifier.testTag("saveButton"), onClick = { viewModel.save(activity) }) {
						Icon(Icons.Default.Done, contentDescription = "Save")
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
		.padding(contentPadding)
	) {
		when (timer) {
			is TimerLoopNode -> {
				TimerList(timers = timer.childrenTimers) //LoopLI(timer)
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



@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
	val timer = remember {
		TimerLoopNode(
			name = "test",
			childrenTimers = mutableStateListOf(
				TimerElemNode(),
				TimerElemNode())
		)
	}


	SuperTime_v0Theme {
		Box (modifier = Modifier
			.background(Color.Cyan)
			.padding(0.dp)
		) {
			LoopLI(timer)
		}
	}
}