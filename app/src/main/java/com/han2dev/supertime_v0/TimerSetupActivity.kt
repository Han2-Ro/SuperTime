package com.han2dev.supertime_v0

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
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
import androidx.lifecycle.ViewModelProvider
import com.han2dev.supertime_v0.ui.DropdownItem
import com.han2dev.supertime_v0.ui.MyDropdownMenu
import com.han2dev.supertime_v0.ui.TimeLI
import com.han2dev.supertime_v0.ui.TimerList
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme

class TimerSetupActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val viewModel = ViewModelProvider(this)[TimerSetupViewModel::class.java]
		viewModel.load(intent, this.applicationContext)

		viewModel.showSelectSoundDialog =
		{onSoundSelected ->
			val builder: AlertDialog.Builder = AlertDialog.Builder(this)

			builder.setTitle("Select Sound")
				.setItems(SoundManager.sounds.map { it.name }.toTypedArray()) { _, which ->
					onSoundSelected(SoundManager.sounds[which])
					Toast.makeText(this, "set sound: ${SoundManager.sounds[which].name}", Toast.LENGTH_SHORT).show()
				}

			val dialog: AlertDialog = builder.create()
			dialog.show()
		}

		title = "timer.value?.name"

		setContent {
			SuperTime_v0Theme {
				MainScreen(this, viewModel)
			}
		}
	}
}

@Composable
fun MainScreen(activity: Activity, viewModel: TimerSetupViewModel) {
	val timer by viewModel.timerNode.observeAsState()
	val isAddMenuVisible = rememberSaveable {
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
					IconButton(modifier = Modifier.testTag("addButton"), onClick = { isAddMenuVisible.value = true }) {
						Icon(Icons.Default.Add, contentDescription = "Add")
					}
					MyDropdownMenu(isAddMenuVisible, listOf(
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
	/*val timer = remember {
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
	}*/
}