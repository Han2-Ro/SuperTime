package com.han2dev.supertime_v0.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.han2dev.supertime_v0.AlarmItem
import com.han2dev.supertime_v0.SavesManager
import com.han2dev.supertime_v0.TimerActivity
import com.han2dev.supertime_v0.TimerData
import com.han2dev.supertime_v0.TimerElemData
import com.han2dev.supertime_v0.TimerLoopData

class MainActivityViewModel(private val activity: MainActivity) : ViewModel() {
    private val _alarms: MutableLiveData<MutableList<String>> = MutableLiveData()
    val alarms: LiveData<MutableList<String>> = _alarms

    private val _timers: MutableLiveData<MutableList<String>> = MutableLiveData()
    val timers: LiveData<MutableList<String>> = _timers


    fun load(context: Context){
         val allSaved = SavesManager.loadAll(context)
         _alarms.value = allSaved
             .filterIsInstance<AlarmItem>()
             .map{it.name}
             .toMutableList()

        _timers.value = allSaved
            .filterIsInstance<TimerData>()
            .map{it.name}
            .toMutableList()
    }

    fun addNewTimer(context: Context,name: String) {
        val timer = TimerLoopData(name, childrenTimers =  listOf(TimerElemData(durationMillis = 10000)), repeats =  1)

        if (SavesManager.save(context, timer, false)) {
            _timers.value?.add(name) ?: throw IllegalStateException("timers not loaded")
        }
        else {
            Toast.makeText(context, "Failed to save timer.", Toast.LENGTH_SHORT).show()
        }
    }

    fun playTimer(title: String, context: Context) {
        val intent = Intent(activity, TimerActivity::class.java)
        intent.putExtra("timer_id", title)
        startActivity(context, intent, null)
    }
}
