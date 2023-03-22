package com.han2dev.supertime_v0

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private var times: LongArray = longArrayOf(10000, 5000)
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rootRecView)
        val adapter = TimesRecViewAdapter(this, recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnStart: Button = findViewById(R.id.btnStart)
        btnStart.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            intent.putExtra("timer", adapter.updateTimer())
            startActivity(intent)
        }

        val btnAdd: Button = findViewById(R.id.btnAddTime)
        btnAdd.setOnClickListener {
            adapter.add(TimerElem())
        }

        val btnAddLoop: Button = findViewById(R.id.btnAddLoop)
        btnAddLoop.setOnClickListener {
            ViewType.LOOP.ordinal
            adapter.add(TimerLoop(1))
        }
    }

}