package com.han2dev.supertime_v0

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp


data class DropdownItem(
	val text: String,
	val onClick: () -> Unit
)

@Composable
fun TimerListItem(
	backgroundColor: Color = Color.LightGray,
	dropdownItems: List<DropdownItem> = listOf(DropdownItem("Error :/") {}),
	topRowContent: @Composable (Modifier) -> Unit,
	extraContent: @Composable () -> Unit = {},
) {
	var isContextMenuVisible = rememberSaveable {
		mutableStateOf(false)
	}

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
				modifier = Modifier.clickable { isContextMenuVisible.value = true }
			)

			MyDropdownMenu(isContextMenuVisible, dropdownItems)

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
fun MyDropdownMenu(
	isContextMenuVisible: MutableState<Boolean>,
	dropdownItems: List<DropdownItem>
) {
	DropdownMenu(
		expanded = isContextMenuVisible.value,
		onDismissRequest = { isContextMenuVisible.value = false },
		offset = DpOffset(1000.dp, 0.dp), //TODO: fix this hardcoded offset
	) {
		dropdownItems.forEach { item ->
			DropdownMenuItem(
				onClick = {
					item.onClick()
					isContextMenuVisible.value = false
				}
			) {
				Text(text = item.text)
			}
		}
	}
}


@Composable
fun TimeLI(timer: TimerElemNode) {
	TimerListItem(
		Color(0x70FFFFFF),
		topRowContent = { modifier ->
			Row (
				verticalAlignment = Alignment.CenterVertically,
				modifier = modifier
			) {

				MyTextField(
					Modifier.testTag("minutesField"),
					value = timer.minutes.value,
					onValueChange = {timer.minutes.value = it})
				Text(text = " min  ")
				MyTextField(
					Modifier.testTag("secondsField"),
					value = timer.seconds.value,
					onValueChange = {timer.seconds.value = it})
				Text(text = " sec")
			}

		})
}

@Composable
fun LoopLI(timerLoop: TimerLoopNode) {
	TimerListItem(
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
		},
		extraContent = {
			TimerList(timerLoop.childrenTimers)
		}
	)
}

@Composable
fun TimerList(timers: List<TimerNode>) {
	//Normally, this would be a LazyColumn, but I'm not sure how to make it work with a LazyColumn
	Column(
		modifier = Modifier.wrapContentHeight()
	) {
		timers.forEach()
		{ timer ->
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