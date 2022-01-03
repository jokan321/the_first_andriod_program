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
import com.example.myscheduler.databinding.FragmentScheduleListBinding
import com.example.myscheduler.model.Schedule
import io.realm.Realm
import io.realm.kotlin.where

/**
 * Schedule一览显示
 */
class ScheduleListFragment : Fragment() {

    private var _binding: FragmentScheduleListBinding? = null
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
        _binding = FragmentScheduleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_ScheduleListFragment_to_ScheduleCalenderFragment)
        }

        //LinearLayoutManager: 项目直列排列
        binding.scheduleList.layoutManager = LinearLayoutManager(context)
        //通过查询语句取得数据库当中所有的Schedule
        val schedules = realm.where<Schedule>().findAll()
        //设定adapter
        val adapter = ScheduleAdapter(schedules)
        binding.scheduleList.adapter = adapter

        //RecyclerView的项目被触碰时所进行的处理
        adapter.setOnItemClickListener { id->
            id?.let {
                val action =
                    ScheduleListFragmentDirections.actionScheduleListFragmentToScheduleEditFragment(it)
                findNavController().navigate(action)
            }
        }
        //于schedule的list页面显示悬浮按钮
        (activity as? MainActivity)?.setFabVisible(View.VISIBLE)
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