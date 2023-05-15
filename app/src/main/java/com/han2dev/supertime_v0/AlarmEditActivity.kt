package com.han2dev.supertime_v0

import android.os.Bundle
import android.text.Layout.Alignment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme

class AlarmEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperTime_v0Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen("Android")
                }
            }
        }
    }
}

@Composable
fun MainScreen(name: String, modifier: Modifier = Modifier) {
    Scaffold()
    {paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxWidth()
        ) {
            Time(Modifier.Companion.align(CenterHorizontally))
            Weekdays()
        }
    }


}

@Composable
private fun Time(modifier: Modifier) {
    Text(
        text = "12:35",
        fontSize = 80.sp,
        modifier = modifier
    )
}

@Composable
private fun Weekdays() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val weekdays = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su") //TODO: get this from resources
        weekdays.forEach() {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .background(Color.Magenta)
                    .padding(3.dp)
                    .toggleable(true, onValueChange = {/*TODO*/ }),
                elevation = 5.dp
            ) {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    //modifier = Modifier.background(Color(0x15459AFF))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SuperTime_v0Theme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            MainScreen("Android")
        }
    }
}