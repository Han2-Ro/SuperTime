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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimesRecViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            R.id.optSave -> save()
            R.id.optAddTimer -> adapter.add(TimerElem())
            R.id.optAddLoop -> adapter.add(TimerLoop())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        //val json1 = Json.encodeToString(adapter.timer)
        adapter.updateTimer()
        val gson = Gson()
        val json = gson.toJson(adapter.timer)
        println(json)
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
    }
}