package com.han2dev.supertime_v0

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

enum class ViewType {TIME, LOOP}

class TimesRecViewAdapter(private val context: Context, recyclerView: RecyclerView) : RecyclerView.Adapter<TimerViewHolder>() {

    var timer: TimerLoop = TimerLoop()
    private var holders: ArrayList<TimerViewHolder> = arrayListOf()
    private lateinit var touchHelper: ItemTouchHelper

    init {
        val simpleCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                override fun isLongPressDragEnabled(): Boolean = false

                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    remove(viewHolder.adapterPosition)
                }
            }

        touchHelper = ItemTouchHelper(simpleCallback)
        touchHelper.attachToRecyclerView(recyclerView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val viewHolder: TimerViewHolder = when (viewType) {
            ViewType.TIME.ordinal -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.li_set_time, parent, false)
                TimerElemHolder(view)
            }
            ViewType.LOOP.ordinal -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.li_loop, parent, false)
                LoopHolder(view)
            }
            else -> {
                val view: View = TextView(context).apply { text = "An error occurred." }
                TimerViewHolder(view)
            }
        }

        holders.add(viewHolder)
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return when (timer.timer[position]) {
            is TimerElem -> ViewType.TIME.ordinal
            is TimerLoop -> ViewType.LOOP.ordinal
            else -> -1
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.txtPosition.text = "${position+1}."

        holder.dragHandle.setOnTouchListener { v, event ->
            if (event.action ==
                MotionEvent.ACTION_DOWN
            ) {
                touchHelper.startDrag(holder)
            }
            false
        }


        //add RecyclerView to Loop Holder
        if (holder is LoopHolder) {
            val adapter = TimesRecViewAdapter(context, holder.recView)

            holder.recView.adapter = adapter
            holder.recView.layoutManager = LinearLayoutManager(context)

            //TODO: remove(only for testing)
            adapter.add(TimerElem(5))
            adapter.add(TimerElem(3))
        }

        //delete Button
        holder.btnRemove.setOnClickListener {
            remove(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        println("holders: ${holders.size}")
        return timer.timer.size
    }


    fun add(new: Timer) {
        timer.timer.add(new)
        notifyDataSetChanged()
    }

    fun updateTimer(): TimerLoop {
        for (holder in holders) {
            if (holder is TimerElemHolder) {
                println("updating TimerElem...")
                val min: Long = holder.edtTxtMin.text.toString().toLongOrNull() ?: 0
                val sec: Long = holder.edtTxtSec.text.toString().toLongOrNull() ?: 0
                timer.timer[holder.adapterPosition] = TimerElem((min * 60 + sec) * 1000)
                println("updated TimerElem: ${(min * 60 + sec) * 1000}")
            }
            else if (holder is LoopHolder) {
                println("updating TimerLoop...")
                val adapter: TimesRecViewAdapter = holder.recView.adapter as TimesRecViewAdapter
                timer.timer[holder.adapterPosition] = adapter.updateTimer()
                (timer.timer[holder.adapterPosition] as TimerLoop).repeats = holder.editTxtRepeats.text.toString().toInt()
                println("updated TimerLoop: ${holder.editTxtRepeats.text.toString().toInt()}")
            }
        }

        return timer
    }

    fun onRowMoved(fromPos: Int, toPos: Int) {
        Collections.swap(timer.timer, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
    }

    fun remove(position: Int) {
        updateTimer()
        timer.timer.removeAt(position)
        notifyItemRemoved(position)
    }
}