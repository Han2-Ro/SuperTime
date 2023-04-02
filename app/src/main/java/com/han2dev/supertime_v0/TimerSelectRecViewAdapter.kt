package com.han2dev.supertime_v0

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class TimerSelectRecViewAdapter : RecyclerView.Adapter<TimerSelectRecViewAdapter.TimerSelectViewHolder>() {
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
    }

    fun add(new: String) {
        titles.add(new)
        notifyDataSetChanged() //TODO: change to notifyItemInserted(position)
    }

    class TimerSelectViewHolder(itemView: View) : ViewHolder(itemView) {
        val txtPosition: TextView = itemView.findViewById(R.id.txtPosition)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
    }
}