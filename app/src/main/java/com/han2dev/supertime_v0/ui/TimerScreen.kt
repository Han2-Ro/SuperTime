package com.han2dev.supertime_v0.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.han2dev.supertime_v0.SavesManager
import com.han2dev.supertime_v0.TimerActivity
import com.han2dev.supertime_v0.TimerSetupActivity

@Composable
fun TimerScreen(timers: List<String>?, onStartNewActivity: (String, Class<*>)->Unit) {
	//remove the file extension from titles that are shown to the user
	//TODO: consider moving this logic to the viewModel
	val titles = timers?.map{
		if (it.endsWith(SavesManager.SaveType.TIMER.fileExtension)){
			it.substringBeforeLast('.')
		}
		else{
			it
		}
	}

	Box (
		modifier = Modifier.fillMaxSize()
	){
		if (!titles.isNullOrEmpty()){
			SelectionList(titles, onStartNewActivity)
		}
		else {
			Text(text = "No timers.")
		}
	}
}

@Composable
private fun SelectionList(titles: List<String>, onStartNewActivity: (String, Class<*>)->Unit) {
	LazyColumn(
		Modifier.background(Color.DarkGray)
	) {
		items(titles) {
			ListItem(it, onStartNewActivity)
		}
	}
}

@Composable
private fun ListItem(title: String, onStartNewActivity: (String, Class<*>)->Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			//.background(Color.Green)
			.padding(4.dp)
	) {
		Row(
			//modifier = Modifier.background(Color.White),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = title,
				modifier = Modifier.weight(1f)
			)
			Icon(
				imageVector = Icons.Default.Edit,
				contentDescription = "Edit",
				modifier = Modifier
					.fillMaxHeight()
					.clickable {
						onStartNewActivity(title, TimerSetupActivity::class.java)
					}
			)
			Icon(
				imageVector = Icons.Default.Delete,
				contentDescription = "Delete",
				modifier = Modifier.clickable {
					/*TODO: SavesManager.deleteTimer(activity.applicationContext, titles[position])
					titles.removeAt(position)*/
				}
			)
			Icon(
				imageVector = Icons.Default.PlayArrow,
				contentDescription = "Play",
				modifier = Modifier.clickable {
					onStartNewActivity(title, TimerActivity::class.java)
				}
			)
		}

	}
}