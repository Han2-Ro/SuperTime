package com.han2dev.supertime_v0

import android.content.Context
import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileNotFoundException

object SavesManager {

	enum class SaveType(val fileExtension: String) {
		TIMER(".timer"),
		ALARM(".alarm")
	}


	/**
	 * Renames timer
	 * @param oldName old name of the timer
	 * @param newName new name of the timer
	 */
	fun rename(context: Context, oldName: String, newName: String): Boolean {
		val timer: TimerData = loadTimer(context, oldName) ?: return false //TODO: for alarms
		timer.name = newName
		if (save(context, timer, false)) {
			delete(context, oldName)
			return true
		}
		return false
	}


	private fun addExtension(name: String, extension: String): String {
		return if (name.endsWith(extension)) name //check only for '.'
		else name + extension
	}

	private fun allTimerFiles(context: Context): List<File> {
		val files = context.filesDir.listFiles()
		return files.filter { it.name.endsWith(SaveType.TIMER.fileExtension) }
	}

	/**
	 * Checks if a file with the given name already exists
	 * @param name name of the file to check
	 * @return true if filename is available
	 */
	private fun checkFilenameAvailability(context: Context, name: String): Boolean {
		val fileName = addExtension(name, SaveType.TIMER.fileExtension) //TODO: for alarms

		//check if timer with this filename already exists
		for (file in allTimerFiles(context)) {
			if (file.name == fileName) {
				Log.i(this::class.simpleName, "timer with name $name already exists")
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

		return addExtension("Why do you have over 1000 timers with the same name?!", SaveType.TIMER.fileExtension) //TODO: for alarms
	}


	fun loadAll(context: Context) : List<TimerData> {
		val timers = mutableListOf<TimerData>()
		val files = context.filesDir.listFiles()
		Log.d(this::class.simpleName,"files: ${files.forEach { it.name }}")
		for (file in allTimerFiles(context)) {
			timers.add(loadTimer(context, file.name) ?: continue) //TODO: for alarms
		}
		return timers
	}

	fun delete(context: Context, name: String): Boolean {
		val fileName = addExtension(name, SaveType.TIMER.fileExtension) //TODO: for alarms
		return context.deleteFile(fileName)
	}

	/**
	 * Deletes all saved timers
	 */
	fun deleteAllTimers(context: Context) {
		for (file in allTimerFiles(context)) {
			delete(context, file.name)
		}
		Log.i(this::class.simpleName, "All Deleted")
	}

	/**
	 * Saves timer to file
	 * @param item timer to save. Its name + TIMER_FILE_EXTENSION will be the file name.
	 * @return true if saved successfully
	 */
	fun save(context: Context, item: Savable, override: Boolean = true) : Boolean {
		val json = toJson(item)
		//TODO: consider using dictionary to look up extension
		val fileName = when (item) {
			is TimerData -> addExtension(item.name, SaveType.TIMER.fileExtension)
		is AlarmItem -> addExtension(item.name, SaveType.ALARM.fileExtension)
		}
		return saveJson(context, json, fileName, override)

	}

	/**
	 * Loads item from file
	 * @param name name of the item to load (with or without extension)
	 * @return Savable object or null if file not found
	 */
	fun loadTimer(context: Context, name: String): TimerData?{
		return load(context, addExtension(name, SaveType.TIMER.fileExtension))
	}

	fun loadAlarm(context: Context, name: String): AlarmItem?{
		return load(context, addExtension(name, SaveType.ALARM.fileExtension))
	}

	private fun <T: Savable> load(context: Context, fileName: String): T? {
		val json = loadJson(context, fileName)
		return fromJson(json) as T?
	}

	private fun saveJson(context: Context, json: String, fileName: String, override: Boolean = true) : Boolean {
		if (!override && !checkFilenameAvailability(context, fileName)) {
			Log.e(this::class.simpleName, "Could not save because '$fileName' already exists and override is false")
			return false
		}

		context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
			it.write(json.toByteArray())
		}

		Log.i(this::class.simpleName, "$json\nstored to $fileName")
		return true
	}

	private fun loadJson(context: Context, fileName: String): String {
		val json: String = try {
			context.openFileInput(fileName).bufferedReader().use {
				it.readText()
			}
		} catch (e: FileNotFoundException) {
			"Error: File not found"
		}
		Log.i(this::class.simpleName, "$json\nloaded from $fileName")
		return json
	}

	private fun toJson(savable: Savable): String {
		return Json.encodeToString(savable)
	}

	private fun fromJson(json: String): Savable? {
		Log.d("fromJson", Savable::class.simpleName!!)
		val timer = TimerElemData()
		return try {
			Json.decodeFromString<Savable>(json)
		} catch (e: Exception) {
			Log.e(this::class.simpleName, "Json deserialization failed", e)
			return null
		}

	}


}
