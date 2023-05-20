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
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun AlarmScreen(alarms: List<String>?) {
	Box (
		modifier = Modifier.fillMaxSize()
	){
		if (!alarms.isNullOrEmpty()){
			SelectionList(alarms)
		}
		else {
			Text(text = "No alarms.")
		}
	}
}

@Composable
private fun SelectionList(titles: List<String>) {

	LazyColumn(
		Modifier.background(Color.DarkGray)
	) {
		items(titles) {
			ListItem(it)
		}
	}
}

@Composable
private fun ListItem(title: String) {
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
				imageVector = Icons.Filled.Edit,
				contentDescription = "Settings",
				modifier = Modifier
					.fillMaxHeight()
					.clickable { /*TODO*/ }
			)
			Icon(
				imageVector = Icons.Filled.Delete,
				contentDescription = "Delete",
				modifier = Modifier.clickable { /*TODO*/ }
			)
			Switch(checked = true, onCheckedChange = {/*TODO*/})
		}

	}
}

@Preview
@Composable
fun Preview() {
	AlarmScreen(listOf<String>("alarm1", "alarm2", "alarm3"))
}