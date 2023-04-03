package com.han2dev.supertime_v0

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson

object SavesManager {
    fun save(timer: Timer, name: String) {
        val json = Gson().toJson(timer)
        println(json + "\nstored to " + MainActivity.context.filesDir)

        MainActivity.context.openFileOutput(name, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }

        Toast.makeText(MainActivity.context, "Saved", Toast.LENGTH_SHORT).show()
    }

    fun load(name: String) : Timer{
        val json: String = MainActivity.context.openFileInput(name).bufferedReader().use {
                it.readText()
        }
        println(json)
        return Gson().fromJson(json, TimerElem::class.java)
    }
}