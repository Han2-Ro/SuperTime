package com.han2dev.supertime_v0.ui.timer

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.han2dev.supertime_v0.R

class NewTimerDialog(private val listener: NewTimerDialogListener) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.dialog_new_timer, null)
        val editTitle: EditText = view.findViewById(R.id.editTitle)

        builder.setView(view)
            .setPositiveButton("OK"
            ) { dialog, id ->
                listener.addNewTimer(editTitle.text.toString())
            }
            .setNegativeButton("Cancel"
            ) { dialog, id ->
                dialog.cancel()
            }

        return builder.create()
    }

    interface NewTimerDialogListener {
        fun addNewTimer(title: String)
    }
}