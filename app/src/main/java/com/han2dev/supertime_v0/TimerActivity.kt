package com.han2dev.supertime_v0

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

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
            val intent = Intent(this, TimerSetupActivity::class.java)
            startActivity(intent)
        }

        btnRestart.setOnClickListener {
            timer.start(this, this)

            btnRestart.visibility = View.GONE
            btnCancel.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
        }

        timer.start(this, this)
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