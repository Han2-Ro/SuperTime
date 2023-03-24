package com.han2dev.supertime_v0

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*

open class TimerViewHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : RecyclerView.ViewHolder(itemView){
    val txtPosition: TextView = itemView.findViewById(R.id.txtPosition)
    val dragHandle: ImageView = itemView.findViewById(R.id.dragHandle)
    private val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)
    private val btnUp: ImageView = itemView.findViewById(R.id.btnUp)
    private val btnDown: ImageView = itemView.findViewById(R.id.btnDown)

    init {
        //delete Button
        btnRemove.setOnClickListener {
            parentAdapter.remove(adapterPosition)
        }

        //up Button TODO: make it go and out of loops
        btnUp.setOnClickListener {
            val currentPos: Int = adapterPosition
            if (currentPos > 0) {
                val target: Timer = parentAdapter.timer.timer[currentPos - 1]
                if (target is TimerLoop) {
                    target.timer.add(parentAdapter.timer.timer[currentPos])
                    parentAdapter.remove(currentPos)
                    parentAdapter.notifyAllDataSetChanged()
                }
                else {
                    Collections.swap(parentAdapter.timer.timer, currentPos, currentPos - 1)
                    parentAdapter.notifyItemMoved(currentPos, currentPos - 1)
                }

            } else {
                Toast.makeText(parentAdapter.context, "already at the top", Toast.LENGTH_SHORT).show()
            }
        }

        //down Button TODO: make it go and out of loops
        btnDown.setOnClickListener {
            val currentPos: Int = adapterPosition
            if (currentPos+1 < parentAdapter.timer.timer.size) {
                Collections.swap(parentAdapter.timer.timer, currentPos, currentPos + 1)
                parentAdapter.notifyItemMoved(currentPos, currentPos + 1)
            } else {
                Toast.makeText(parentAdapter.context, "already at the bottom", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class TimerElemHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : TimerViewHolder(itemView, parentAdapter) {
    val edtTxtMin: EditText = itemView.findViewById(R.id.editTxtMin)
    val edtTxtSec: EditText = itemView.findViewById(R.id.editTxtSec)
}

class LoopHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : TimerViewHolder(itemView, parentAdapter) {
    val editTxtRepeats: EditText = itemView.findViewById(R.id.editTxtRepeats)
    val recView: RecyclerView = itemView.findViewById(R.id.recView)
    var adapter: TimesRecViewAdapter? = null
        set(value) {
            recView.adapter = value
            field = value
        }
}