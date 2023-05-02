package com.han2dev.supertime_v0

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

@Deprecated("Replaced by TimerSetupActivity")
class OldTimerSetupActivity : AppCompatActivity() {

	private lateinit var recyclerView: RecyclerView
	private lateinit var adapter: TimesRecViewAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_timer_setup)

		val timerId = intent.getStringExtra("timer_id")
			?: throw NullPointerException("Found no \"timer_name\": String in intent extra.")
		println("name from intent: $timerId")
		val timer: Timer = TODO("removed")

		title = timer.data.name

		/*recyclerView = findViewById(R.id.rootRecView)
		adapter = TimesRecViewAdapter(this, recyclerView)
		if (timer is TimerLoop) {
			adapter.timerLoop = timer
		} else {
			adapter.add(timer)
		}

		recyclerView.adapter = adapter
		recyclerView.layoutManager = LinearLayoutManager(this)
		*/

		//TODO: Provide option to always automatically save changes
		//TODO: Only ask to save changes if changes have been made
		val callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				val builder = AlertDialog.Builder(this@OldTimerSetupActivity)
				builder.setMessage("Do you want to save changes?")
					.setCancelable(false)
					.setPositiveButton("Yes") { _, _ ->
						// Save changes
						if (SavesManager.save(this@OldTimerSetupActivity, TODO("removed"))) {
							Toast.makeText(this@OldTimerSetupActivity, "Saved", Toast.LENGTH_SHORT).show()
							finish()
						} else {
							Toast.makeText(this@OldTimerSetupActivity, "Failed to save", Toast.LENGTH_SHORT).show()
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
				if (SavesManager.save(this, TODO("removed"))) {
					Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
				} else {
					Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
				}
			}
			R.id.optAddTimer -> adapter.add(TODO("removed"))
			R.id.optAddLoop -> adapter.add(TODO("removed"))
		}
		return super.onOptionsItemSelected(item)
	}
}