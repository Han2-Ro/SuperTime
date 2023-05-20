package com.han2dev.supertime_v0.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.han2dev.supertime_v0.AlarmItem
import com.han2dev.supertime_v0.SavesManager
import com.han2dev.supertime_v0.TimerData

class MainActivityViewModel: ViewModel() {
    private val _alarms: MutableLiveData<List<String>> = MutableLiveData()
    val alarms: LiveData<List<String>> = _alarms

    private val _timers: MutableLiveData<List<String>> = MutableLiveData()
    val timers: LiveData<List<String>> = _timers

    fun load(context: Context){
         val allSaved = SavesManager.loadAll(context)
         _alarms.value = allSaved
             .filterIsInstance<AlarmItem>()
             .map{it.name}

        _timers.value = allSaved
            .filterIsInstance<TimerData>()
            .map{it.name}
    }
}
