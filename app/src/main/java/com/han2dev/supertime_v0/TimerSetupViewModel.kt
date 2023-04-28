package com.han2dev.supertime_v0

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerSetupViewModel : ViewModel() {

	private val _timer: MutableLiveData<Timer> = MutableLiveData(TimerLoop())
	val timer: LiveData<Timer> = _timer

	//TODO: save
}