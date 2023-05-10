package com.han2dev.supertime_v0

import kotlinx.serialization.Serializable

@Serializable
sealed interface Savable {
	var name: String
}


@Serializable
sealed class TimerData: Savable {
	abstract override var name: String
	abstract var endSound: TimerEndSound
}

@Serializable
data class TimerElemData(
	override var name: String = "untitled",
	override var endSound: TimerEndSound = SoundManager.defaultTimerEndSound,
	var durationMillis: Long = 0,
) : TimerData()

@Serializable
data class TimerLoopData(
	override var name: String = "untitled",
	override var endSound: TimerEndSound = SoundManager.defaultTimerEndSound,
	var childrenTimers: List<TimerData> = listOf(),
	var repeats: Int = 1,
) : TimerData()

@Serializable
data class AlarmItem(
	override var name: String,
	//@Contextual
	//var time: LocalDateTime,
	var time: Long,
	var enabled: Boolean = false,
): Savable