package com.han2dev.supertime_v0

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerSetupViewModel() : ViewModel() {
	private val _timer: MutableLiveData<Timer> = MutableLiveData(TimerLoop())
	val timer: LiveData<Timer> = _timer

	fun load(intent: Intent, context: Context) {
		val timerId = intent.getStringExtra("timer_id")
			?: throw NullPointerException("Found no \"timer_id\": String in intent extra.")
		println("name from intent: $timerId")
		_timer.value = SavesManager.load(context, timerId)
	}

	fun onTimerChanged(timer: Timer) {
		_timer.value = timer
	}
	fun save(context: Context) {
		//TODO
	}
}