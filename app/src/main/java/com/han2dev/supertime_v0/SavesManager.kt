package com.han2dev.supertime_v0

import android.content.Context
import android.widget.Toast
import com.google.gson.*

object SavesManager {

    private val gson = GsonBuilder()
        .registerTypeAdapter(Timer::class.java, TimerSerializer())
        .registerTypeAdapter(TimerElem::class.java, TimerSerializer())
        .registerTypeAdapter(TimerLoop::class.java, TimerSerializer())
        .registerTypeAdapter(Timer::class.java, TimerDeserializer())
        .create()

    /**
     * Saves timer to file
     * @param timer timer to save. Its name + ".timer" will be the file name.
     */
    fun save(timer: Timer) {
        val json = timerToJson(timer)
        saveJson(json, timer.name)
    }

    /**
     * Loads timer from file
     * @param name name of the timer to load (with or without .timer extension)
     * @return Timer object
     */
    fun load(name: String) : Timer{
        val json = loadJson(name)
        return timerFromJson(json)
    }

    private fun saveJson(json: String, name: String) {
        println(json + "\nstored to " + MainActivity.context.filesDir)

        var fileName = name
        if (!name.endsWith(".timer")) {
            fileName = "$name.timer"
        }

        MainActivity.context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun loadJson(name: String): String {
        var fileName = name
        if (!name.endsWith(".timer")) {
            fileName = "$name.timer"
        }

        val json: String = MainActivity.context.openFileInput(fileName).bufferedReader().use {
            it.readText()
        }
        println(json + "\nloaded from " + MainActivity.context.filesDir)
        return json
    }

    fun loadAll() : List<Timer> {
        val timers = mutableListOf<Timer>()
        val files = MainActivity.context.filesDir.listFiles()
        println("files: ${files.forEach { it.name }}")
        for (file in files) {
            if (file.name.endsWith(".timer")) {
                timers.add(load(file.name))
            }
        }
        return timers
    }

    fun delete(name: String) {
        var fileName = name
        if (!name.endsWith(".timer")) {
            fileName = "$name.timer"
        }

        if (MainActivity.context.deleteFile(fileName)) {
            Toast.makeText(MainActivity.context, "Deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(MainActivity.context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Deletes all saved timers
     */
    fun deleteAll() {
        val files = MainActivity.context.filesDir.listFiles()
        for (file in files) {
            delete(file.name)
        }
        Toast.makeText(MainActivity.context, "All Deleted", Toast.LENGTH_SHORT).show()
    }

    fun timerToJson(timer: Timer) : String {
        return gson.toJson(timer)
    }

    fun timerFromJson(json: String) : Timer {
        return try {
            gson.fromJson(json, Timer::class.java)
        } catch (e: Exception) {
            println(e)
            TimerElem(0, "Error")
        }

    }

    private class TimerDeserializer : JsonDeserializer<Timer> {
        override fun deserialize(json: JsonElement, typeOfT: java.lang.reflect.Type, context: JsonDeserializationContext): Timer {
            val jsonObject = json.asJsonObject
            val type = jsonObject.get("type").asString
            val name = jsonObject.get("name").asString

            if (type == TimerLoop::class.java.name) {
                println("deserialize TimerLoop")
                val repeats = jsonObject.get("repeats").asInt
                val childrenTimers = jsonObject.get("childrenTimers").asJsonArray
                val timerLoop = TimerLoop(repeats, name)
                for (child in childrenTimers) {
                    timerLoop.childrenTimers.add(deserialize(child, typeOfT, context))
                }
                return timerLoop
            }

            if (type == TimerElem::class.java.name) {
                println("deserialize TimerElem")
                val duration = jsonObject.get("duration").asLong
                return TimerElem(duration, name)
            }

            throw IllegalArgumentException("Unknown type: $type")
        }
    }

    private class TimerSerializer : JsonSerializer<Timer> {
        override fun serialize(src: Timer, typeOfSrc: java.lang.reflect.Type, context: JsonSerializationContext): JsonElement {
            val jsonObject = JsonObject()
            jsonObject.addProperty("type", src.javaClass.name)
            jsonObject.addProperty("name", src.name)

            if (src is TimerLoop) {
                println("serialize TimerLoop")
                jsonObject.addProperty("repeats", src.repeats)
                jsonObject.add("childrenTimers", gson.toJsonTree(src.childrenTimers))
            }

            if (src is TimerElem) {
                println("serialize TimerElem")
                jsonObject.addProperty("duration", src.duration)
            }

            return jsonObject
        }
    }
}