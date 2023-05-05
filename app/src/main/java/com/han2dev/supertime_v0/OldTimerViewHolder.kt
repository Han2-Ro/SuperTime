package com.han2dev.supertime_v0

import android.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import java.util.*

// "Do not place Android context classes in static fields (static reference to Timer which has field activity pointing to Activity); this is a memory leak"
// Fixed but TimerLoop has field pointing to Activity
var clipboard: Timer? = null

abstract class TimerViewHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : RecyclerView.ViewHolder(itemView) {
	//lateinit var timer: Timer
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
						//Toast.makeText(itemView.context, "deleted", Toast.LENGTH_SHORT).show()
						true
					}
					R.id.cut -> {
						clipboard = TODO("removed")
						parentAdapter.remove(adapterPosition)
						Toast.makeText(itemView.context, "cut", Toast.LENGTH_SHORT).show()
						true
					}
					R.id.copy -> {
						clipboard = TODO("removed")
						Toast.makeText(itemView.context, "copied", Toast.LENGTH_SHORT).show()
						true
					}
					R.id.paste -> {
						if (clipboard != null) {
							parentAdapter.add(clipboard!!)
							//Toast.makeText(itemView.context, "pasted", Toast.LENGTH_SHORT).show()
						} else {
							Toast.makeText(itemView.context, "Clipboard is empty.", Toast.LENGTH_SHORT).show()
						}
						true
					}
					R.id.setSound -> {
						val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)

						builder.setTitle("Select Sound")
							.setItems(SoundManager.sounds.map { it.name }.toTypedArray()) { _, which ->
								parentAdapter.timerLoop.childrenTimers[adapterPosition].data.endSound = SoundManager.sounds[which]
								Toast.makeText(itemView.context, "set sound: ${SoundManager.sounds[which].name}", Toast.LENGTH_SHORT).show()
							}

						val dialog: AlertDialog = builder.create()
						dialog.show()
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
				Collections.swap(parentAdapter.timerLoop.childrenTimers, currentPos, currentPos - 1)
				parentAdapter.notifyItemMoved(currentPos, currentPos - 1)

			} else {
				Toast.makeText(parentAdapter.context, "already at the top", Toast.LENGTH_SHORT).show()
			}
		}

		//down Button TODO: make it go and out of loops
		btnDown.setOnClickListener {
			val currentPos: Int = adapterPosition
			if (currentPos+1 < parentAdapter.timerLoop.childrenTimers.size) {
				Collections.swap(parentAdapter.timerLoop.childrenTimers, currentPos, currentPos + 1)
				parentAdapter.notifyItemMoved(currentPos, currentPos + 1)
			} else {
				Toast.makeText(parentAdapter.context, "already at the bottom", Toast.LENGTH_SHORT).show()
			}
		}
	}

	abstract fun getTimer(): Timer

	abstract fun setTimer(timer: Timer)
}

class TimerElemHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : TimerViewHolder(itemView, parentAdapter) {
	lateinit var timer: TimerElem

	private val edtTxtMin: EditText = itemView.findViewById(R.id.editTxtMin)
	private val edtTxtSec: EditText = itemView.findViewById(R.id.editTxtSec)

	init {
		edtTxtMin.setOnFocusChangeListener { _, hasFocus ->
			onFocusChanged(hasFocus)
		}

		edtTxtSec.setOnFocusChangeListener { _, hasFocus ->
			onFocusChanged(hasFocus)
		}

		edtTxtMin.addTextChangedListener { onTextChanged() }
		edtTxtSec.addTextChangedListener { onTextChanged() }
	}

	private fun onTextChanged() {
		val min: Long = edtTxtMin.text.toString().toLongOrNull() ?: 0
		val sec: Long = edtTxtSec.text.toString().toLongOrNull() ?: 0
		timer.data.durationMillis = (min * 60 + sec) * 1000
	}

	private fun onFocusChanged(hasFocus: Boolean) {
		if (!hasFocus) {
			val timeStr = formatTime(timer.data.durationMillis)
			edtTxtSec.setText(timeStr.substring(3, 5))
			edtTxtMin.setText(timeStr.substring(0, 2))
		}
	}

	override fun getTimer(): Timer {
		return timer
	}

	override fun setTimer(timer: Timer) {
		this.timer = timer as TimerElem
		val timeStr = formatTime(timer.data.durationMillis)
		edtTxtSec.setText(timeStr.substring(3, 5))
		edtTxtMin.setText(timeStr.substring(0, 2))
	}
}

class LoopHolder(itemView: View, parentAdapter: TimesRecViewAdapter) : TimerViewHolder(itemView, parentAdapter) {
	lateinit var timer: TimerLoop

	private val editTxtRepeats: EditText = itemView.findViewById(R.id.editTxtRepeats)
	val recView: RecyclerView = itemView.findViewById(R.id.recView)
	var adapter: TimesRecViewAdapter? = null
		set(value) {
			recView.adapter = value
			field = value
		}

	init {
		editTxtRepeats.addTextChangedListener {
			timer.data.repeats = editTxtRepeats.text.toString().toIntOrNull()?:1
		}
	}

	override fun getTimer(): Timer {
		return timer
	}

	override fun setTimer(timer: Timer) {
		this.timer = timer as TimerLoop
		editTxtRepeats.setText(timer.data.repeats.toString())
	}
}