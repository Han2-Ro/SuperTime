package com.han2dev.supertime_v0

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

enum class ViewType {TIME, LOOP}

class TimesRecViewAdapter(val context: Context, recyclerView: RecyclerView) : RecyclerView.Adapter<TimerViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    var timerLoop: TimerLoop = TimerLoop()
    var holders: ArrayList<TimerViewHolder> = arrayListOf()
    private var touchHelper: ItemTouchHelper

    init {
        val simpleCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                override fun isLongPressDragEnabled(): Boolean = false

                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    onRowMoved(viewHolder, target)
                    return true
                }

                //TODO: Fix this shit because int won't delete
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    remove(viewHolder.adapterPosition)
                }
            }

        touchHelper = ItemTouchHelper(simpleCallback)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    fun notifyAllDataSetChanged(){
        notifyDataSetChanged()
        holders.forEach {
            if (it is LoopHolder) it.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val viewHolder: TimerViewHolder = when (viewType) {
            ViewType.TIME.ordinal -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.li_set_time, parent, false)
                TimerElemHolder(view, this)
            }
            ViewType.LOOP.ordinal -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.li_loop, parent, false)
                LoopHolder(view, this)
            }
            else -> {
                throw Exception("ViewType not found: $viewType")
            }
        }

        holders.add(viewHolder)
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return when (timerLoop.childrenTimers[position]) {
            is TimerElem -> ViewType.TIME.ordinal
            is TimerLoop -> ViewType.LOOP.ordinal
            else -> -1
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.txtPosition.text = "${position+1}."

        if (holder is TimerElemHolder) {
            holder.edtTxtMin.setText((timerLoop.childrenTimers[position] as TimerElem).getMinutes().toString())
            holder.edtTxtSec.setText((timerLoop.childrenTimers[position] as TimerElem).getSeconds().toString())
        }

        //TODO: consider doing this in constructor of TimerViewHolder
        holder.dragHandle.setOnTouchListener { v, event ->
            if (event.action ==
                MotionEvent.ACTION_DOWN
            ) {
                touchHelper.startDrag(holder)
            }
            false
        }


        //TODO: consider doing this in constructor of TimerViewHolder
        //add RecyclerView to Loop Holder
        if (holder is LoopHolder) {
            holder.adapter = TimesRecViewAdapter(context, holder.recView)
            holder.recView.layoutManager = LinearLayoutManager(context)
            holder.recView.setRecycledViewPool(viewPool)

            //TODO: remove(only for testing)
            holder.adapter!!.add(TimerElem(5))
            holder.adapter!!.add(TimerElem(2))
        }
    }

    override fun getItemCount(): Int {
        println("holders: ${holders.size}")
        return timerLoop.childrenTimers.size
    }


    fun add(new: Timer) {
        timerLoop.childrenTimers.add(new)
        notifyItemInserted(timerLoop.childrenTimers.size - 1) //notifyDataSetChanged()
    }

    //TODO: consider removing/replacing cause that shit is buggy
    fun updateTimer(): TimerLoop {
        for (holder in holders) {
            if (holder is TimerElemHolder) {
                println("updating TimerElem...")
                val min: Long = holder.edtTxtMin.text.toString().toLongOrNull() ?: 0
                val sec: Long = holder.edtTxtSec.text.toString().toLongOrNull() ?: 0
                timerLoop.childrenTimers[holder.adapterPosition] = TimerElem((min * 60 + sec) * 1000)
                println("updated TimerElem: ${(min * 60 + sec) * 1000}")
            }
            else if (holder is LoopHolder) {

                println("updating TimerLoop...")
                val adapter: TimesRecViewAdapter = holder.recView.adapter as TimesRecViewAdapter
                timerLoop.childrenTimers[holder.adapterPosition] = adapter.updateTimer()
                (timerLoop.childrenTimers[holder.adapterPosition] as TimerLoop).repeats = holder.editTxtRepeats.text.toString().toIntOrNull()?:1
                println("updated TimerLoop: ${holder.editTxtRepeats.text.toString().toIntOrNull()?:1}")
            }
        }

        return timerLoop
    }


    fun readInput(){
        timerLoop = TimerLoop()
        for(holder in holders) {
            add(holder.readInput())
        }
    }


    fun onRowMoved(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        val fromPosition: Int = viewHolder.layoutPosition
        val toPosition: Int = target.layoutPosition
        println("fromPosition = $fromPosition")
        println("toPosition = $toPosition")
        if (target is TimerElemHolder) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(timerLoop.childrenTimers, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(timerLoop.childrenTimers, i, i - 1)
                }
            }

            notifyItemMoved(fromPosition, toPosition)
        }
        else if (target is LoopHolder) {
            target.adapter!!.add(timerLoop.childrenTimers[fromPosition])
            remove(fromPosition)
            notifyItemRemoved(fromPosition)
        }
    }

    fun remove(position: Int) {
        //TODO: decide if updateTimer should be called
        //updateTimer()
        timerLoop.childrenTimers.removeAt(position)
        notifyItemRemoved(position)
    }
}