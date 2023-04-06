package com.han2dev.supertime_v0.ui.timer

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.han2dev.supertime_v0.R
import com.han2dev.supertime_v0.SavesManager
import com.han2dev.supertime_v0.TimerActivity
import com.han2dev.supertime_v0.TimerSetupActivity

class TimerSelectRecViewAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<TimerSelectRecViewAdapter.TimerSelectViewHolder>() {
    private var titles: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerSelectViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.li_timer, parent, false)
        return TimerSelectViewHolder(view)
    }


    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onBindViewHolder(holder: TimerSelectViewHolder, position: Int) {
        holder.txtPosition.text = "${position+1}."
        holder.txtTitle.text = titles[position]

        //set up buttons
        holder.btnPlay.setOnClickListener {
            startActivityWithTimer(position, TimerActivity::class.java)
        }

        holder.btnEdit.setOnClickListener {
            startActivityWithTimer(position, TimerSetupActivity::class.java)
        }

        holder.btnDelete.setOnClickListener {
            SavesManager.delete(activity.applicationContext, titles[position])
            titles.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * Starts an activity with the timer json as an extra.
     * @param position the position of the timer in the recycler view
     * @param activityClass the class of the activity to start
     */
    private fun startActivityWithTimer(
        position: Int,
        activityClass: Class<out Activity>
    ) {
        val json = SavesManager.loadJson(activity.applicationContext, titles[position])
        val intent = Intent(activity, activityClass)
        intent.putExtra("timer_json", json)
        activity.startActivity(intent)
    }

    fun add(new: String) {
        titles.add(new)
        notifyItemInserted(titles.size-1)
    }

    fun refresh() {
        titles.clear()
        SavesManager.loadAll(activity.applicationContext).forEach {
            add(it.name)
        }
    }

    class TimerSelectViewHolder(itemView: View) : ViewHolder(itemView) {
        val txtPosition: TextView = itemView.findViewById(R.id.txtPosition)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val btnPlay: View = itemView.findViewById(R.id.btnPlay)
        val btnEdit: View = itemView.findViewById(R.id.btnEdit)
        val btnDelete: View = itemView.findViewById(R.id.btnDelete)
    }
}