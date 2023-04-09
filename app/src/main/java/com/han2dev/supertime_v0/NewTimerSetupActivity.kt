package com.han2dev.supertime_v0

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme

class NewTimerSetupActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			SuperTime_v0Theme {

			}
		}
	}
}

@Composable
fun MyListItem(backgroundColor: Color = Color.LightGray, TopRowContent: @Composable (Modifier) -> Unit, ExtraContent: @Composable () -> Unit = {}) {

	Column(
		modifier = Modifier
			.padding(5.dp)
			.clip(RoundedCornerShape(10.dp))
			.background(backgroundColor)
	){
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(10.dp)

		) {
			TopRowContent(Modifier.weight(1f))

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

		Box (modifier = Modifier.padding(start = 20.dp)){
			Column {
				ExtraContent()
			}
		}
	}

}

@Composable
fun TimeLI() {
	MyListItem(
		TopRowContent = {modifier ->
			Text( modifier = modifier,
				text = "mm:ss")
	})
}

@Composable
fun LoopLI(ListItems: @Composable () -> Unit) {
	MyListItem(
		Color.Cyan,
		{modifier ->
		OutlinedTextField(
			value = "loops",
			onValueChange = {/*TODO*/},
			modifier = modifier)
	},{
		ListItems()
	})
}

@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
	SuperTime_v0Theme {
		Column {
			LoopLI {
				TimeLI()
				TimeLI()
			}
		}
	}
}