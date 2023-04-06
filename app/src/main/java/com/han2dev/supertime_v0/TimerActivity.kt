package com.han2dev.supertime_v0

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView


fun formatTime(millis: Long): String {
    //formatting from ms to MM:SS.cs
    val centis: Long = millis / 10 % 100
    val second: Long = millis / 1000 % 60
    val minute: Long = millis / (1000 * 60) //% 60
    //val hour: Long = millis / (1000 * 60 * 60) % 24

    return String.format("%02d:%02d.%02d", minute, second, centis)
}


class TimerActivity : AppCompatActivity(), TimerParent {

    private lateinit var txtTime: TextView

    lateinit var btnPause: Button
    lateinit var btnResume: Button
    lateinit var btnCancel: Button
    lateinit var btnRestart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        txtTime = findViewById(R.id.txtTime)

        val json = intent.getStringExtra("timer_json")
            ?: throw NullPointerException("Found no \"timer_json\": String in intent extra.")
        println("json from intent: $json")
        val timer: Timer = SavesManager.timerFromJson(json)


        btnPause = findViewById(R.id.btnPause)
        btnResume = findViewById(R.id.btnResume)
        btnCancel = findViewById(R.id.btnCancel)
        btnRestart = findViewById(R.id.btnRestart)

        btnPause.setOnClickListener {
            timer.pause()

            //update button visibilities
            btnPause.visibility = View.GONE
            btnResume.visibility = View.VISIBLE
            btnCancel.visibility = View.VISIBLE
        }

        btnResume.setOnClickListener {
            timer.resume()

            //update button visibilities
            btnPause.visibility = View.VISIBLE
            btnResume.visibility = View.GONE
            btnCancel.visibility = View.GONE
        }

        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnRestart.setOnClickListener {
            timer.start(this)

            btnRestart.visibility = View.GONE
            btnCancel.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
        }

        timer.start(this)
    }

    override fun next() {
        //txtTime.text = "Finished!"
        btnPause.visibility = View.GONE
        btnResume.visibility = View.GONE
        btnCancel.visibility = View.VISIBLE
        btnRestart.visibility = View.VISIBLE
    }

    override fun update(time: Long) {
        txtTime.text = formatTime(time)
    }

    /*
    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }*/
}