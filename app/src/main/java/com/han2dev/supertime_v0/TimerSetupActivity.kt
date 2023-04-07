package com.han2dev.supertime_v0

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class TimerSetupActivity : AppCompatActivity() {

	private lateinit var recyclerView: RecyclerView
	private lateinit var adapter: TimesRecViewAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_timer_setup)

		val json = intent.getStringExtra("timer_json")
			?: throw NullPointerException("Found no \"timer_json\": String in intent extra.")
		println("json from intent: $json")
		val timer: Timer = SavesManager.timerFromJson(json) ?: throw IllegalArgumentException("timer_json could not be parsed to Timer")

		title = timer.name

		recyclerView = findViewById(R.id.rootRecView)
		adapter = TimesRecViewAdapter(this, recyclerView)
		if (timer is TimerLoop) {
			adapter.timerLoop = timer
		} else {
			adapter.add(timer)
		}

		recyclerView.adapter = adapter
		recyclerView.layoutManager = LinearLayoutManager(this)


		//TODO: Provide option to always automatically save changes
		//TODO: Only ask to save changes if changes have been made
		val callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				val builder = AlertDialog.Builder(this@TimerSetupActivity)
				builder.setMessage("Do you want to save changes?")
					.setCancelable(false)
					.setPositiveButton("Yes") { _, _ ->
						// Save changes
						if (SavesManager.save(this@TimerSetupActivity, adapter.updateTimer())) {
							Toast.makeText(this@TimerSetupActivity, "Saved", Toast.LENGTH_SHORT).show()
							finish()
						} else {
							Toast.makeText(this@TimerSetupActivity, "Failed to save", Toast.LENGTH_SHORT).show()
						}
					}
					.setNegativeButton("No") { _, _ ->
						// Do not save changes
						finish()
					}
					.setNeutralButton("Cancel") { _, _ ->
						// Do nothing
					}
				val alert = builder.create()
				alert.show()
			}
		}
		onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.timer_setup_menu,  menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.optSave -> {
				if (SavesManager.save(this, adapter.updateTimer())) {
					Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
				} else {
					Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
				}
			}
			R.id.optAddTimer -> adapter.add(TimerElem())
			R.id.optAddLoop -> adapter.add(TimerLoop())
		}
		return super.onOptionsItemSelected(item)
	}

	/*override fun onBackPressed() {
		AlertDialog.Builder(this)
			.setMessage("Do you want to save changes?")
			.setPositiveButton("Save") { _, _ ->
				// Save changes
				super.onBackPressed()
			}
			.setNegativeButton("Discard") { _, _ ->
				// Discard changes
				super.onBackPressed()
			}
			.show()
	}*/
}