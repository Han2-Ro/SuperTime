package com.han2dev.supertime_v0

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class TimerSelectRecViewAdapter(val activity: FragmentActivity) : RecyclerView.Adapter<TimerSelectRecViewAdapter.TimerSelectViewHolder>() {
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
            val json = SavesManager.loadJson(titles[position])
            val intent = Intent(activity, TimerActivity::class.java)
            intent.putExtra("timer_json", json)
            activity.startActivity(intent)
        }

        holder.btnEdit.setOnClickListener {
            Toast.makeText(MainActivity.context, "Not yet implemented.", Toast.LENGTH_SHORT).show()
        }

        holder.btnDelete.setOnClickListener {
            SavesManager.delete(titles[position])
        }
    }

    fun add(new: String) {
        titles.add(new)
        notifyDataSetChanged() //TODO: change to notifyItemInserted(position)
    }

    class TimerSelectViewHolder(itemView: View) : ViewHolder(itemView) {
        val txtPosition: TextView = itemView.findViewById(R.id.txtPosition)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val btnPlay: View = itemView.findViewById(R.id.btnPlay)
        val btnEdit: View = itemView.findViewById(R.id.btnEdit)
        val btnDelete: View = itemView.findViewById(R.id.btnDelete)
    }
}