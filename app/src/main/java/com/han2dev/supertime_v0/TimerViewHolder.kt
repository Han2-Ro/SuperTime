package com.han2dev.supertime_v0

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

open class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val txtPosition: TextView = itemView.findViewById(R.id.txtPosition)
    val dragHandle: ImageView = itemView.findViewById(R.id.dragHandle)
    val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)
    val btnUp: ImageView = itemView.findViewById(R.id.btnUp)
    val btnDown: ImageView = itemView.findViewById(R.id.btnDown)
}

class TimerElemHolder(itemView: View) : TimerViewHolder(itemView) {
    val edtTxtMin: EditText = itemView.findViewById(R.id.editTxtMin)
    val edtTxtSec: EditText = itemView.findViewById(R.id.editTxtSec)
}

class LoopHolder(itemView: View) : TimerViewHolder(itemView) {
    val editTxtRepeats: EditText = itemView.findViewById(R.id.editTxtRepeats)
    val recView: RecyclerView = itemView.findViewById(R.id.recView)
    var adapter: TimesRecViewAdapter? = null
        set(value) {
            recView.adapter = value
            field = value
        }
}