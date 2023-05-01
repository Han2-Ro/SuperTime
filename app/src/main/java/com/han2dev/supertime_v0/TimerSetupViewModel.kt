package com.han2dev.supertime_v0

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerSetupViewModel() : ViewModel() {
	private val _timerNode: MutableLiveData<TimerNode?> = MutableLiveData()
	val timerNode: LiveData<TimerNode?> = _timerNode

	fun load(intent: Intent, context: Context) {
		val timerId = intent.getStringExtra("timer_id")
			?: throw NullPointerException("Found no \"timer_id\": String in intent extra.")
		println("name from intent: $timerId")
		val timer = SavesManager.load(context, timerId)
		if (timer != null) _timerNode.value = timerToNode(timer)
	}

	private fun timerToNode(timer: Timer): TimerNode {
		return when (timer) {
			is TimerElem -> {
				//TODO: consider using a better way to get minutes and seconds
				val timeStr = formatTime(timer.durationMillis)
				TimerElemNode(
					name = timer.name,
					minutes = mutableStateOf(timeStr.substring(0, 2).toInt()),
					seconds = mutableStateOf(timeStr.substring(3, 5).toInt()),
			)}
			is TimerLoop -> TimerLoopNode(
				name = timer.name,
				childrenTimers = mutableStateListOf<TimerNode>().apply {
					timer.childrenTimers.forEach {
						add(timerToNode(it))
					}
				},
				repeats = mutableStateOf(timer.repeats),
			)
			else -> throw IllegalArgumentException("Unknown timer type.")
		}
	}

	fun save(context: Context) {
		//TODO
	}
}

abstract class TimerNode {abstract var name: String}

data class TimerElemNode(
	override var name: String = "",
	var minutes: MutableState<Int?> = mutableStateOf(0),
	var seconds: MutableState<Int?> = mutableStateOf(0),
) : TimerNode()

data class TimerLoopNode(
	override var name: String = "",
	var childrenTimers: SnapshotStateList<TimerNode> = mutableStateListOf<TimerNode>(),
	var repeats: MutableState<Int?> = mutableStateOf(1),
) : TimerNode()