package com.han2dev.supertime_v0

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView


class TimerActivity : AppCompatActivity(), TimerParent {

    private lateinit var txtTime: TextView
    private lateinit var txtCycles: TextView

    lateinit var btnPause: Button
    lateinit var btnResume: Button
    lateinit var btnCancel: Button
    lateinit var btnRestart: Button

    var nextTimerEndSound: SoundManager.TimerEndSound? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        txtTime = findViewById(R.id.txtTime)
        txtCycles = findViewById(R.id.txtCycles)

        val json = intent.getStringExtra("timer_json")
            ?: throw NullPointerException("Found no \"timer_json\": String in intent extra.")
        println("json from intent: $json")
        val timer: Timer = SavesManager.timerFromJson(json) ?: throw IllegalArgumentException("timer_json could not be parsed to Timer")


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
            finish()
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

    override fun update(time: Long, cyclesLeft: MutableList<Int>) {
        //check if sound should be played TODO: consider doing this in SoundManager for more modularity/separation
        if(nextTimerEndSound != null && time <= nextTimerEndSound!!.playAtMsLeft){
            SoundManager.playSound(nextTimerEndSound!!)
            nextTimerEndSound = null
        }

        //update UI
        txtTime.text = formatTime(time)
        var text = ""
        for (number in cyclesLeft) {
            text = "$number,$text" //TODO: consider formatting
        }
        txtCycles.text = text
    }

    override fun setSound(sound: SoundManager.TimerEndSound?) {
        nextTimerEndSound = if(sound != null) {
            SoundManager.loadSound(sound)
        } else {
            SoundManager.loadSound(SoundManager.sounds[0])
        }
    }



    /*
    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }*/
}