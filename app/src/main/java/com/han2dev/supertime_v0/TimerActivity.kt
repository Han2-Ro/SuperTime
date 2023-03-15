package com.han2dev.supertime_v0

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class TimerActivity : AppCompatActivity(), TimerParent, java.io.Serializable {

    private lateinit var txtTime: TextView

    lateinit var btnPause: Button
    lateinit var btnResume: Button
    lateinit var btnCancel: Button
    lateinit var btnRestart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        txtTime = findViewById(R.id.txtTime)

        val timer: Timer? =  if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra("timer", Timer::class.java)
        } else {
            intent.getSerializableExtra("timer") as Timer
        }
        if (timer == null) throw NullPointerException("Found no \"timer\": Timer in intent extra.")

        println((timer as TimerLoop).timer.toString())
        timer.start(this, this)

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
            //TODO: cancel timer and return to MainActivity
        }

        btnRestart.setOnClickListener {
            timer.start(this, this)

            btnRestart.visibility = View.GONE
            btnCancel.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
        }
    }

    override fun next() {
        txtTime.text = "Finished!"
        btnPause.visibility = View.GONE
        btnResume.visibility = View.GONE
        btnCancel.visibility = View.VISIBLE
        btnRestart.visibility = View.VISIBLE
    }

    /*
    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }*/
}