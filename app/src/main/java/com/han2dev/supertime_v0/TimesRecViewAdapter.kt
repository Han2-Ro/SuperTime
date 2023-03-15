package com.han2dev.supertime_v0

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class TimesRecViewAdapter(context: Context, recyclerView: RecyclerView) : RecyclerView.Adapter<TimesRecViewAdapter.ViewHolder>() {

    var timer: TimerLoop = TimerLoop()
    private var holders: ArrayList<ViewHolder> = arrayListOf()
    private lateinit var touchHelper: ItemTouchHelper

    init {
        val simpleCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun isLongPressDragEnabled(): Boolean = false

                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            }

        touchHelper = ItemTouchHelper(simpleCallback)
        touchHelper.attachToRecyclerView(recyclerView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.li_set_time, parent, false)
        val viewHolder: ViewHolder = ViewHolder(view)
        holders.add(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtPosition.text = "${position+1}."

        holder.dragHandle.setOnTouchListener { v, event ->
            if (event.action ==
                MotionEvent.ACTION_DOWN
            ) {
                touchHelper.startDrag(holder);
            }
            false
        }

        holder.btnRemove.setOnClickListener {
            updateTimer()
            timer.timer.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        println("holders: ${holders.size}")
        return timer.timer.size
    }


    fun addTime(value: Long) {
        timer.timer.add(TimerElem(value))
        notifyDataSetChanged()
    }

    fun updateTimer(): Timer {
        for (holder in holders) {
            val min: Long = holder.edtTxtMin.text.toString().toLongOrNull()?:0
            val sec: Long = holder.edtTxtSec.text.toString().toLongOrNull()?:0
            timer.timer[holder.adapterPosition] = TimerElem((min*60 + sec) * 1000)
        }

        /*for (i in timer.timer.indices) {
            val min: Long = holders[i].edtTxtMin.text.toString().toLongOrNull()?:0
            val sec: Long = holders[i].edtTxtSec.text.toString().toLongOrNull()?:0
            timer.timer[i] = TimerElem((min*60 + sec) * 1000)
            println("$i: $sec s")
        }*/
        return timer
    }

    fun onRowMoved(fromPos: Int, toPos: Int) {
        Collections.swap(timer.timer, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPosition: TextView = itemView.findViewById(R.id.txtPosition)
        val edtTxtMin: EditText = itemView.findViewById(R.id.editTxtMin)
        val edtTxtSec: EditText = itemView.findViewById(R.id.editTxtSec)
        val dragHandle: ImageView = itemView.findViewById(R.id.dragHandle)
        val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)
    }
}