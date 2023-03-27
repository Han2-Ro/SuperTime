package com.han2dev.supertime_v0

import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.util.*

open class TimerViewHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : RecyclerView.ViewHolder(itemView){
    val txtPosition: TextView = itemView.findViewById(R.id.txtPosition)
    val dragHandle: ImageView = itemView.findViewById(R.id.dragHandle)
    private val btnUp: ImageView = itemView.findViewById(R.id.btnUp)
    private val btnDown: ImageView = itemView.findViewById(R.id.btnDown)
    private val btnOptions: ImageView = itemView.findViewById(R.id.btnOptions)

    init {
        btnOptions.setOnClickListener {v: View ->
            val popup: PopupMenu = PopupMenu(itemView.context, v)
            popup.setOnMenuItemClickListener {item: MenuItem ->
                when (item.itemId) {
                    R.id.delete -> {
                        parentAdapter.remove(adapterPosition)
                        Toast.makeText(itemView.context, "deleted", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.cut -> {
                        Toast.makeText(itemView.context, "cut", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.copy -> {
                        Toast.makeText(itemView.context, "copy", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.paste -> {
                        Toast.makeText(itemView.context, "paste", Toast.LENGTH_SHORT).show()
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