package com.example.myscheduler.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myscheduler.R
import com.example.myscheduler.activity.MainActivity
import com.example.myscheduler.adapter.ScheduleAdapter
import com.example.myscheduler.databinding.FragmentScheduleCalendarBinding
import com.example.myscheduler.model.Schedule
import io.realm.Realm
import io.realm.kotlin.where
import java.time.Year
import java.util.*

/**
 * 根据日历搜寻schedule页面
 */
class ScheduleCalendarFragment : Fragment() {

    private var _binding: FragmentScheduleCalendarBinding? = null

    private val binding get() = _binding!!
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_ScheduleCalenderFragment_to_ScheduleListFragment)
        }

        (activity as? MainActivity)?.setFabVisible(View.VISIBLE)
        binding.list.layoutManager = LinearLayoutManager(context)
        val dateTime = Calendar.getInstance().apply { timeInMillis = binding.calendarView.date }
        findSchedule(
            dateTime.get(Calendar.YEAR),
            dateTime.get(Calendar.MONTH),
            dateTime.get(Calendar.DAY_OF_MONTH)
        )
        binding.calendarView.setOnDateChangeListener{ _, year, month, dayOfMonth -> findSchedule(year, month, dayOfMonth)}
    }

    private fun findSchedule(year: Int, month: Int, dayOfMonth: Int) {
        val selectDate = Calendar.getInstance().apply {
            clear()
            set(year, month, dayOfMonth)
        }

        val schedules = realm.where<Schedule>().between("date", selectDate.time, selectDate.apply { add(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MILLISECOND, -1) }.time).findAll().sort("date")
        val adapter = ScheduleAdapter(schedules)
        binding.list.adapter = adapter
        adapter.setOnItemClickListener { id ->
            id?.let {
                val action = ScheduleCalendarFragmentDirections.actionScheduleCalenderFragmentToScheduleEditFragment(it)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}