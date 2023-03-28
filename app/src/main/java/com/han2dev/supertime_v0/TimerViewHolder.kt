package com.han2dev.supertime_v0

import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.util.*

// "Do not place Android context classes in static fields (static reference to Timer which has field activity pointing to Activity); this is a memory leak"
// Fixed but TimerLoop has field pointing to Activity
var clipboard: Timer? = null

abstract class TimerViewHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : RecyclerView.ViewHolder(itemView) {
    val txtPosition: TextView = itemView.findViewById(R.id.txtPosition)
    val dragHandle: ImageView = itemView.findViewById(R.id.dragHandle)
    private val btnUp: ImageView = itemView.findViewById(R.id.btnUp)
    private val btnDown: ImageView = itemView.findViewById(R.id.btnDown)
    private val btnOptions: ImageView = itemView.findViewById(R.id.btnOptions)

    init {
        //TODO: add option to add timer/loop
        btnOptions.setOnClickListener {v: View ->
            val popup = PopupMenu(itemView.context, v)
            popup.setOnMenuItemClickListener {item: MenuItem ->
                when (item.itemId) {
                    R.id.delete -> {
                        parentAdapter.remove(adapterPosition)
                        Toast.makeText(itemView.context, "deleted", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.cut -> {
                        clipboard = readInput()
                        parentAdapter.remove(adapterPosition)
                        Toast.makeText(itemView.context, "cut", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.copy -> {
                        clipboard = readInput() //parentAdapter.timer.timer[adapterPosition].clone()
                        Toast.makeText(itemView.context, "copied", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.paste -> {
                        if (clipboard != null) {
                            parentAdapter.add(clipboard!!)
                            Toast.makeText(itemView.context, "pasted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(itemView.context, "Clipboard is empty.", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    else -> {false}
                }
            }
            popup.inflate(R.menu.timer_popup_menu)
            popup.show()
        }

        //up Button TODO: make it go and out of loops
        btnUp.setOnClickListener {
            val currentPos: Int = adapterPosition
            if (currentPos > 0) {
                Collections.swap(parentAdapter.timer.timer, currentPos, currentPos - 1)
                parentAdapter.notifyItemMoved(currentPos, currentPos - 1)

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

    //reads the input und writes it into the variables
    abstract fun readInput(): Timer
}

class TimerElemHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : TimerViewHolder(itemView, parentAdapter) {
    val edtTxtMin: EditText = itemView.findViewById(R.id.editTxtMin)
    val edtTxtSec: EditText = itemView.findViewById(R.id.editTxtSec)
    override fun readInput(): TimerElem {
        println("updating TimerElem...")
        val min: Long = edtTxtMin.text.toString().toLongOrNull() ?: 0
        val sec: Long = edtTxtSec.text.toString().toLongOrNull() ?: 0
        println("updated TimerElem: ${(min * 60 + sec) * 1000}")
        return TimerElem((min * 60 + sec) * 1000)
    }
}

class LoopHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : TimerViewHolder(itemView, parentAdapter) {
    val editTxtRepeats: EditText = itemView.findViewById(R.id.editTxtRepeats)
    val recView: RecyclerView = itemView.findViewById(R.id.recView)
    var adapter: TimesRecViewAdapter? = null
        set(value) {
            recView.adapter = value
            field = value
        }

    override fun readInput(): TimerLoop {
        println("updating TimerLoop...")

        val timerLoop = TimerLoop(editTxtRepeats.text.toString().toIntOrNull()?:1)
        for(holder in adapter!!.holders) {
            timerLoop.timer.add(holder.readInput())
        }
        adapter!!.timer = timerLoop
        println("updated TimerLoop: ${editTxtRepeats.text.toString().toIntOrNull()?:1}")
        return adapter!!.timer
    }
}