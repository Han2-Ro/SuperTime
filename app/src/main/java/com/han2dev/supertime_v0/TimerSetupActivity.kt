package com.han2dev.supertime_v0

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class TimerSetupActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimesRecViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_setup)

        recyclerView = findViewById(R.id.rootRecView)
        adapter = TimesRecViewAdapter(this, recyclerView)

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
            R.id.optSave -> SavesManager.save(adapter.updateTimer(), "<name>")
            R.id.optAddTimer -> adapter.add(TimerElem())
            R.id.optAddLoop -> adapter.add(TimerLoop())
        }
        return super.onOptionsItemSelected(item)
    }

}