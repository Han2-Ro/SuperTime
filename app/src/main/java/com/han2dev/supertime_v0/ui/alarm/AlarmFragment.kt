package com.han2dev.supertime_v0.ui.alarm

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.han2dev.supertime_v0.R
import com.han2dev.supertime_v0.SavesManager
import com.han2dev.supertime_v0.TimerElemData
import com.han2dev.supertime_v0.TimerLoopData
import com.han2dev.supertime_v0.databinding.FragmentTimerBinding
import com.han2dev.supertime_v0.ui.timer.NewTimerDialog
import com.han2dev.supertime_v0.ui.timer.TimerSelectRecViewAdapter
import com.han2dev.supertime_v0.ui.timer.TimerViewModel

class AlarmFragment : Fragment(), NewTimerDialog.NewTimerDialogListener {

    private var _binding: FragmentTimerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimerSelectRecViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //super.onCreateView(inflater, container, savedInstanceState)
        val timerViewModel =
            ViewModelProvider(this)[TimerViewModel::class.java]

        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recViewTimerSelect)
        adapter = TimerSelectRecViewAdapter(requireActivity())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter.refresh()

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.timer_selection_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                when (menuItem.itemId) {
                    R.id.optAddTimer -> NewTimerDialog(this@AlarmFragment, SavesManager.convertToAvailableFilename(requireContext(),"untitled")).show(parentFragmentManager, "NewTimerDialog")

                    R.id.optDeleteAll -> {
                        SavesManager.deleteAllTimers(requireActivity().applicationContext)
                        adapter.refresh()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //actually new Alarm
    override fun addNewTimer(name: String) {
        val timer = TimerLoopData(name, childrenTimers =  listOf(TimerElemData(durationMillis = 10000)), repeats =  1)

        if (SavesManager.save(requireActivity().applicationContext, timer, false)) {
            adapter.add(name)
        }
        else {
            Toast.makeText(requireActivity().applicationContext, "Failed to save timer.", Toast.LENGTH_SHORT).show()
        }
    }
}