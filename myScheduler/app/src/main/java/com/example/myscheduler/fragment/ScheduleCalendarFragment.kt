package com.example.myscheduler.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myscheduler.R
import com.example.myscheduler.activity.MainActivity
import com.example.myscheduler.adapter.ScheduleAdapter
import com.example.myscheduler.data.Schedule
import com.example.myscheduler.databinding.FragmentScheduleCalendarBinding
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

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
    ): View? {
        _binding = FragmentScheduleCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //按下返回按钮回到Schedule列表页面
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        (activity as? MainActivity)?.setFabVisible(View.VISIBLE)
        binding.list.layoutManager = LinearLayoutManager(context)

        //取得当前日历显示的时间
        var dateTime = Calendar.getInstance().apply {
            timeInMillis = binding.calendarView.date
        }

        //传入日历的日期查找对应的schedule
        findSchedule(dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH))
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            findSchedule(year, month, dayOfMonth)
        }
    }

    /**
     * 通过日期查找出对应的schedule并显示
     */
    private fun findSchedule(year: Int, month: Int, dayOfMonth: Int) {
        var selectDate = Calendar.getInstance().apply {
            clear()
            set(year, month, dayOfMonth)
        }
        //通过日期查找schedule
        val schedules = realm.where<Schedule>().between("date",selectDate.time, selectDate.apply {
            add(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }.time).findAll().sort("date")
        val adapter = ScheduleAdapter(schedules)

        //查找到的schedule在列表中进行显示
        binding.list.adapter = adapter

        //当触碰schedule列表的子项目的时候跳转到编辑页面并且显示该项目内容
        adapter.setOnItemClickListener { id -> id?.let {
            val action = ScheduleListFragmentDirections.actionToScheduleEditFragment(it)
            findNavController().navigate(action)
        } }

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