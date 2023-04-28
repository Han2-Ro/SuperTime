package com.han2dev.supertime_v0

import android.content.Context
import com.google.gson.*
import java.io.File
import java.io.FileNotFoundException

object SavesManager {

	private const val TIMER_FILE_EXTENSION = ".timer"

	private val gson = GsonBuilder()
		.registerTypeAdapter(Timer::class.java, TimerSerializer())
		.registerTypeAdapter(TimerElem::class.java, TimerSerializer())
		.registerTypeAdapter(TimerLoop::class.java, TimerSerializer())
		.registerTypeAdapter(Timer::class.java, TimerDeserializer())
		.create()


	/**
	 * Renames timer
	 * @param oldName old name of the timer
	 * @param newName new name of the timer
	 */
	fun rename(context: Context, oldName: String, newName: String): Boolean {
		val timer = load(context, oldName) ?: return false
		timer.name = newName
		if (save(context, timer, false)) {
			delete(context, oldName)
			return true
		}
		return false
	}


	private fun addTimerExtension(name: String): String {
		return if (name.endsWith(TIMER_FILE_EXTENSION)) name
		else name + TIMER_FILE_EXTENSION
	}

	private fun allTimerFiles(context: Context): List<File> {
		val files = context.filesDir.listFiles()
		return files.filter { it.name.endsWith(TIMER_FILE_EXTENSION) }
	}

	/**
	 * Checks if a file with the given name already exists
	 * @param name name of the file to check
	 * @return true if filename is available
	 */
	private fun checkFilenameAvailability(context: Context, name: String): Boolean {
		val fileName = addTimerExtension(name)

		//check if timer with this filename already exists
		for (file in allTimerFiles(context)) {
			if (file.name == fileName) {
				println("Timer with this name already exists")
				return false
			}
		}
		return true
	}

	fun convertToAvailableFilename(context: Context, name: String): String {
		if (checkFilenameAvailability(context, name)) return name

		for (i in 2..1000) {
			val fileName = "$name ($i)"
			if (checkFilenameAvailability(context, fileName)) return fileName
		}

		return addTimerExtension("Why do you have over 1000 timers with the same name?!")
	}


	fun loadAll(context: Context) : List<Timer> {
		val timers = mutableListOf<Timer>()
		val files = context.filesDir.listFiles()
		println("files: ${files.forEach { it.name }}")
		for (file in allTimerFiles(context)) {
			timers.add(load(context, file.name) ?: continue)
		}
		return timers
	}

	fun delete(context: Context, name: String): Boolean {
		val fileName = addTimerExtension(name)
		return context.deleteFile(fileName)
	}

	/**
	 * Deletes all saved timers
	 */
	fun deleteAll(context: Context) {
		for (file in allTimerFiles(context)) {
			delete(context, file.name)
		}
		println("All Deleted")
	}

	/**
	 * Saves timer to file
	 * @param timer timer to save. Its name + TIMER_FILE_EXTENSION will be the file name.
	 * @return true if saved successfully
	 */
	fun save(context: Context, timer: Timer, override: Boolean = true) : Boolean {
		val json = timerToJson(timer)
		return saveJson(context, json, timer.name, override)
	}

	/**
	 * Loads timer from file
	 * @param name name of the timer to load (with or without extension)
	 * @return Timer object or null if file not found
	 */
	fun load(context: Context, name: String): Timer? {
		val json = loadJson(context, name)
		return timerFromJson(json)
	}

	private fun saveJson(context: Context, json: String, name: String, override: Boolean = true) : Boolean {
		val fileName = addTimerExtension(name)

		if (!override && !checkFilenameAvailability(context, fileName)) {
			println("Error: File already exists and override is false")
			return false
		}

		context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
			it.write(json.toByteArray())
		}

		println("$json\nstored to $fileName")
		return true
	}

	fun loadJson(context: Context, name: String): String {
		val fileName = addTimerExtension(name)

		val json: String = try {
			context.openFileInput(fileName).bufferedReader().use {
				it.readText()
			}
		} catch (e: FileNotFoundException) {
			return "Error: File not found"
		}
		println("$json\nloaded from $fileName")
		return json
	}

	fun timerToJson(timer: Timer): String {
		return gson.toJson(timer)
	}

	fun timerFromJson(json: String): Timer? {
		return try {
			gson.fromJson(json, Timer::class.java)
		} catch (e: Exception) {
			println("Error: Json deserialization failed")
			println(e)
			return null
		}

	}

	private class TimerSerializer : JsonSerializer<Timer> {
		override fun serialize(src: Timer, typeOfSrc: java.lang.reflect.Type, context: JsonSerializationContext): JsonElement {
			val jsonObject = JsonObject()
			jsonObject.addProperty("type", src.javaClass.name)
			jsonObject.addProperty("name", src.name)
			jsonObject.addProperty("endSound", src.endSound?.name)

			if (src is TimerLoop) {
				println("serialize TimerLoop")
				jsonObject.addProperty("repeats", src.repeats)
				jsonObject.add("childrenTimers", gson.toJsonTree(src.childrenTimers))
			}

			if (src is TimerElem) {
				println("serialize TimerElem")
				jsonObject.addProperty("duration", src.durationMillis)
			}

			return jsonObject
		}
	}

	private class TimerDeserializer : JsonDeserializer<Timer> {
		override fun deserialize(json: JsonElement, typeOfT: java.lang.reflect.Type, context: JsonDeserializationContext): Timer {
			val jsonObject = json.asJsonObject
			val type = jsonObject.get("type").asString
			val name = jsonObject.get("name").asString

			val timer = if (type == TimerLoop::class.java.name) {
				println("deserialize TimerLoop")
				val repeats = jsonObject.get("repeats").asInt
				val childrenTimers = jsonObject.get("childrenTimers").asJsonArray
				val timerLoop = TimerLoop(repeats, name)
				for (child in childrenTimers) {
					timerLoop.childrenTimers.add(deserialize(child, typeOfT, context))
				}
				timerLoop
			}
			else if (type == TimerElem::class.java.name) {
				println("deserialize TimerElem")
				val duration = jsonObject.get("duration").asLong
				TimerElem(duration, name)
			}
			else throw IllegalArgumentException("Unknown type: $type")

			try {
				timer.endSound = SoundManager.getSoundByName(jsonObject.get("endSound").asString)
			} catch (e: Exception) {
				println("Timer has no end sound")
			}

			return timer
		}
	}
}