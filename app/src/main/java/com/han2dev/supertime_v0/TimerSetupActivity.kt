package com.han2dev.supertime_v0

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
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

        recyclerView = findViewById(R.id.rootRecView)
        adapter = TimesRecViewAdapter(this, recyclerView)
        if (timer is TimerLoop) {
            adapter.timerLoop = timer
        } else {
            adapter.add(timer)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnStart: Button = findViewById(R.id.btnStart)
        btnStart.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            intent.putExtra("timer", adapter.updateTimer())
            startActivity(intent)
        }

        //TODO: consider removing these add buttons
        val btnAdd: Button = findViewById(R.id.btnAddTime)
        btnAdd.setOnClickListener {
            adapter.add(TimerElem())
        }

        val btnAddLoop: Button = findViewById(R.id.btnAddLoop)
        btnAddLoop.setOnClickListener {
            adapter.add(TimerLoop(1))
        }
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

}