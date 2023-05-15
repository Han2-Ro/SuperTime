package com.han2dev.supertime_v0.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme

@Composable
fun SelectionList() {

	LazyColumn() {
		items(listOf<String>("alarm1", "alarm2", "alarm3")) {
			ListItem(it)
		}
	}
}

@Composable
fun ListItem(title: String) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.background(Color.Green)
			.padding(4.dp)
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
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

@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
	SuperTime_v0Theme() {
		SelectionList()
	}
}