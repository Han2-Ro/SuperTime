package com.han2dev.supertime_v0

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileNotFoundException

object SavesManager {

	private const val TIMER_FILE_EXTENSION = ".timer"


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


	fun loadAll(context: Context) : List<TimerData> {
		val timers = mutableListOf<TimerData>()
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
	fun save(context: Context, timer: TimerData, override: Boolean = true) : Boolean {
		val json = timerToJson(timer)
		return saveJson(context, json, timer.name, override)
	}

	/**
	 * Loads timer from file
	 * @param name name of the timer to load (with or without extension)
	 * @return Timer object or null if file not found
	 */
	fun load(context: Context, name: String): TimerData? {
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

	fun timerToJson(timer: TimerData): String {
		return Json.encodeToString(timer)
	}

	fun timerFromJson(json: String): TimerData? {
		return try {
			Json.decodeFromString<TimerData>(json)
		} catch (e: Exception) {
			println("Error: Json deserialization failed")
			println(e)
			return null
		}

	}


}

@Serializable
sealed class TimerData {abstract var name: String}

@Serializable
data class TimerElemData(
	override var name: String = "untitled",
	var durationMillis: Long = 0,
) : TimerData()

@Serializable
data class TimerLoopData(
	override var name: String = "untitled",
	var childrenTimers: List<TimerData> = listOf(),
	var repeats: Int = 1,
) : TimerData()