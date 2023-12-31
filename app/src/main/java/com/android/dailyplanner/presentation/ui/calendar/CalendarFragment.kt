package com.android.dailyplanner.presentation.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.android.dailyplanner.presentation.ui.calendar.create.CreateEventFragment
import com.android.dailyplanner.presentation.ui.calendar.details.DetailEventFragment
import com.android.dailyplanner.presentation.ui.calendar.details.EventListAdapter
import com.android.dailyplanner.presentation.view_models.calendar.CalendarViewModel
import com.android.dailyplanner.R
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()

    private var calendarView: CalendarView? = null
    private var eventRecyclerView: RecyclerView? = null
    private var plusIcon: AppCompatButton? = null

    private val eventListAdapter by lazy {
        EventListAdapter(
            object : EventViewHolder.Listener {
                override fun onItemClick(eventId: Long) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.main_activity__fragment_container,
                            DetailEventFragment.newInstance(eventId)
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initObservers()
        setOnClickListeners()
        initFirstState()
    }

    private fun initViews(view: View) {
        view.apply {
            calendarView = findViewById(R.id.fragment_calendar__calendar_view)
            eventRecyclerView = findViewById(R.id.fragment_calendar__recycler_view)
            plusIcon = findViewById(R.id.fragment_calendar__plus_icon)
        }

        eventRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = eventListAdapter
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.eventList.collect {
                    eventListAdapter.submitList(it)
                }
            }
        }
    }

    private fun setOnClickListeners() {

        var date = ""
        calendarView?.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventsDates: EventDay) {
                date = (eventsDates.calendar.timeInMillis / 1000).toString()
                viewModel.getEventListByDate(date)
            }
        })
//        calendarView?.setOnDayClickListener { eventsDates ->
//            date = (eventsDates.calendar.timeInMillis / 1000).toString()
//            viewModel.getEventListByDate(date)
//        }

        plusIcon?.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.main_activity__fragment_container,
                    CreateEventFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initFirstState() {
        viewModel.initFirstState()
    }
}
