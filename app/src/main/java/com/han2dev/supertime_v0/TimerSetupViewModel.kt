package com.han2dev.supertime_v0

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
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

	private fun timerToNode(timer: TimerData, parent: TimerLoopNode? = null): TimerNode {
		return when (timer) {
			is TimerElemData -> {
				//TODO: consider using a better way to get minutes and seconds
				val timeStr = formatTime(timer.durationMillis)
				TimerElemNode(
					name = timer.name,
					minutes = mutableStateOf(timeStr.substring(0, 2).toInt()),
					seconds = mutableStateOf(timeStr.substring(3, 5).toInt()),
					parent = parent,
			)}
			is TimerLoopData -> {
				val loopNode = TimerLoopNode(
					name = timer.name,
					repeats = mutableStateOf(timer.repeats),
					parent = parent,
				)
				loopNode.childrenTimers = timer.childrenTimers.map { timerToNode(it, loopNode) }.toMutableStateList()
				loopNode
			}
		}
	}


	private fun nodeToTimerData(TimerNode: TimerNode): TimerData {
		return when (TimerNode) {
			is TimerElemNode -> {
				TimerElemData(
					name = TimerNode.name,
					//TODO: remove non-null assertions
					durationMillis = TimerNode.minutes.value!! * 60 * 1000L + TimerNode.seconds.value!! * 1000L,
				)
			}
			is TimerLoopNode -> {
				TimerLoopData(
					name = TimerNode.name,
					childrenTimers = TimerNode.childrenTimers.map { nodeToTimerData(it) },
					repeats = TimerNode.repeats.value!!,
				)
			}
		}
	}

	fun save(context: Context) {
		if (SavesManager.save(context, nodeToTimerData(_timerNode.value!!))) {
			Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
		} else {
			Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
		}
	}

	fun addTimer(timerData: TimerData) {
		if (_timerNode.value is TimerLoopNode) {
			(_timerNode.value as TimerLoopNode).childrenTimers.add(timerToNode(timerData))
		}
		else {
			Toast.makeText(null, "Can only add a timer to loops", Toast.LENGTH_SHORT).show()
		}
	}
}

sealed class TimerNode {
	abstract var name: String
	abstract val dropdownItems: List<DropdownItem>
	abstract var parent: TimerLoopNode?
}

data class TimerElemNode(
	override var name: String = "",
	var minutes: MutableState<Int?> = mutableStateOf(0),
	var seconds: MutableState<Int?> = mutableStateOf(0),
	override var parent: TimerLoopNode? = null,
) : TimerNode() {
	override val dropdownItems: List<DropdownItem> = listOf(
		DropdownItem("Delete") {
			parent?.childrenTimers?.remove(this) ?: throw NullPointerException("Parent is null")
		},
	)
}

data class TimerLoopNode(
	override var name: String = "",
	var childrenTimers: SnapshotStateList<TimerNode> = mutableStateListOf(TimerElemNode()),
	var repeats: MutableState<Int?> = mutableStateOf(1),
	override var parent: TimerLoopNode? = null,
) : TimerNode() {
	override val dropdownItems: List<DropdownItem> = listOf(
		DropdownItem("Delete") {
			parent?.childrenTimers?.remove(this) ?: throw NullPointerException("Parent is null")
	   	},
		DropdownItem("Add Timer") {
	  		childrenTimers.add(TimerElemNode())
		},
		DropdownItem("Add Loop") {
	 		childrenTimers.add(TimerLoopNode())
		},
	)
}