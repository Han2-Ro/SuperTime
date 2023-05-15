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
import com.han2dev.supertime_v0.ui.DropdownItem

class TimerSetupViewModel() : ViewModel() {
	private val _timerNode: MutableLiveData<TimerNode?> = MutableLiveData()
	val timerNode: LiveData<TimerNode?> = _timerNode
	var showSelectSoundDialog: (onSoundSelected: (TimerEndSound) -> Unit) -> Unit =
		{throw IllegalStateException("showSelectSoundDialog() is not defined.")}

	fun load(intent: Intent, context: Context) {
		val timerId = intent.getStringExtra("timer_id")
			?: throw NullPointerException("Found no \"timer_id\": String in intent extra.")
		println("name from intent: $timerId")
		val timer: TimerData? = SavesManager.loadTimer(context, timerId)
		if (timer != null) _timerNode.value = timerToNode(timer)
	}

	private fun timerToNode(timer: TimerData, parent: TimerLoopNode? = null): TimerNode {
		return when (timer) {
			is TimerElemData -> {
				//TODO: consider using a better way to get minutes and seconds
				val timeStr = formatTime(timer.durationMillis)
				TimerElemNode(
					name = timer.name,
					parent = parent,
					viewModel = this,
					endSound = timer.endSound,
					minutes = mutableStateOf(timeStr.substring(0, 2).toInt()),
					seconds = mutableStateOf(timeStr.substring(3, 5).toInt()),
			)}
			is TimerLoopData -> {
				val loopNode = TimerLoopNode(
					name = timer.name,
					parent = parent,
					viewModel = this,
					endSound = timer.endSound,
					repeats = mutableStateOf(timer.repeats),
				)
				loopNode.childrenTimers = timer.childrenTimers.map { timerToNode(it, loopNode) }.toMutableStateList()
				loopNode
			}
		}
	}


	private fun nodeToTimerData(timerNode: TimerNode): TimerData {
		return when (timerNode) {
			is TimerElemNode -> {
				TimerElemData(
					name = timerNode.name,
					endSound = timerNode.endSound,
					//TODO: remove non-null assertions
					durationMillis = timerNode.minutes.value!! * 60 * 1000L + timerNode.seconds.value!! * 1000L,
				)
			}
			is TimerLoopNode -> {
				TimerLoopData(
					name = timerNode.name,
					endSound = timerNode.endSound,
					childrenTimers = timerNode.childrenTimers.map { nodeToTimerData(it) },
					repeats = timerNode.repeats.value!!,
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
	abstract val viewModel: TimerSetupViewModel
	abstract val endSound: TimerEndSound
}

data class TimerElemNode(
	override var name: String = "",
	override var parent: TimerLoopNode? = null,
	override val viewModel: TimerSetupViewModel,
	override var endSound: TimerEndSound = SoundManager.defaultTimerEndSound,
	var minutes: MutableState<Int?> = mutableStateOf(0),
	var seconds: MutableState<Int?> = mutableStateOf(0),
) : TimerNode() {
	override val dropdownItems: List<DropdownItem> = listOf(
		DropdownItem("Delete") {
			parent?.childrenTimers?.remove(this) ?: throw NullPointerException("Parent is null")
		},
		DropdownItem("Set Sound") {
			viewModel.showSelectSoundDialog(){
				endSound = it
			}
		},
	)
}

data class TimerLoopNode(
	override var name: String = "",
	override var parent: TimerLoopNode? = null,
	override val viewModel: TimerSetupViewModel,
	override var endSound: TimerEndSound = SoundManager.defaultTimerEndSound,
	var childrenTimers: SnapshotStateList<TimerNode> = mutableStateListOf(TimerElemNode(viewModel = viewModel)),
	var repeats: MutableState<Int?> = mutableStateOf(1),
) : TimerNode() {
	override val dropdownItems: List<DropdownItem> = listOf(
		DropdownItem("Delete") {
			parent?.childrenTimers?.remove(this) ?: throw NullPointerException("Parent is null")
	   	},
		DropdownItem("Add Timer") {
	  		childrenTimers.add(TimerElemNode(
				parent = this,
				viewModel = viewModel,
			  ))
		},
		DropdownItem("Add Loop") {
	 		childrenTimers.add(TimerLoopNode(
				parent = this,
				viewModel = viewModel,
			 ))
		},
	)
}