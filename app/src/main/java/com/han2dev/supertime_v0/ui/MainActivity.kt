package com.han2dev.supertime_v0.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.han2dev.supertime_v0.SavesManager
import com.han2dev.supertime_v0.TimerElemData
import com.han2dev.supertime_v0.TimerLoopData
import com.han2dev.supertime_v0.ui.theme.SuperTime_v0Theme
import com.han2dev.supertime_v0.ui.timer.NewTimerDialog

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
		viewModel.load(this.applicationContext)

		setContent {
			SuperTime_v0Theme {
				val navController = rememberNavController()
				MainScreen(navController, viewModel, this)
			}
		}
	}

	@Composable
	private fun MainScreen(
		navController: NavHostController,
		viewModel: MainActivityViewModel,
		context: Context
	) {
		Scaffold(
			topBar = {
				TopAppBar(
					title = {
						Text(text = navController.currentBackStackEntryAsState().value?.destination?.label.toString())
					},
					actions = {
						//TODO: add dialog for naming
						IconButton(modifier = Modifier.testTag("addButton"), onClick = {
							viewModel.addNewTimer(context, SavesManager.toAvailableTimerName(context, "timer"))
						}) {
							Icon(Icons.Default.Add, contentDescription = "Add")
						}
					}
				)
			},
			bottomBar = {
				BottomNavigationBar(
					items = listOf(
						BottomNavItem(
							name = "Alarm",
							route = "alarm",
							icon = Icons.Default.Info,
						),
						BottomNavItem(
							name = "Timer",
							route = "timer",
							icon = Icons.Default.Info,
						),
						BottomNavItem(
							name = "Settings",
							route = "settings",
							icon = Icons.Default.Settings,
						)
					),
					navController = navController,
					onItemClick = {
						navController.navigate(it.route)
					}
				)
			}
		) {
			Navigation(
				navController = navController,
				modifier = Modifier.padding(it),
				viewModel = viewModel,
				context = context
			)
		}
	}
}

@Composable
fun Navigation(
	navController: NavHostController,
	modifier: Modifier = Modifier,
	viewModel: MainActivityViewModel,
	context: Context
) {
	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = "alarm"
	) {
		composable("alarm") { AlarmScreen(viewModel.alarms.value) }
		composable("timer") { TimerScreen(viewModel.timers.value) { viewModel.playTimer(it, context) } }
		composable("settings") { SettingsScreen() }
	}
}

@Composable
fun BottomNavigationBar(
	items: List<BottomNavItem>,
	navController: NavController,
	modifier: Modifier = Modifier,
	onItemClick: (BottomNavItem) -> Unit
) {
	val backStackEntry = navController.currentBackStackEntryAsState()
	BottomNavigation (
		modifier = modifier,
		backgroundColor = Color.DarkGray,
		elevation = 5.dp
	){
		items.forEach {item ->
			val selected = item.route == backStackEntry.value?.destination?.route
			BottomNavigationItem(
				selected = selected,
				onClick = { onItemClick(item) },
				selectedContentColor = Color.Cyan,
				unselectedContentColor = Color.LightGray,
				icon = {
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Icon(
							imageVector = item.icon,
							contentDescription = item.name,
							modifier = Modifier.size(24.dp)
						)
						Text(
							text = item.name,
							style = MaterialTheme.typography.subtitle1
						)

					}
				}
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	SuperTime_v0Theme {
		//Navigation("Android")
	}
}