package com.example.myscheduler.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myscheduler.R
import com.example.myscheduler.activity.MainActivity
import com.example.myscheduler.data.Schedule
import com.example.myscheduler.adapter.ScheduleAdapter
import com.example.myscheduler.databinding.FragmentScheduleListBinding
import io.realm.Realm
import io.realm.kotlin.where

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
    ): View? {
        _binding = FragmentScheduleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //设置按下日历按钮跳转到日历显示页面
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.list.layoutManager = LinearLayoutManager(context)
        //当前数据库已经保存的schedule数据取出
        val schedule = realm.where<Schedule>().findAll()
        val adapter = ScheduleAdapter(schedule)

        //当前已经保存的schedule数据放到此页面显示
        binding.list.adapter = adapter

        //当触碰schedule列表的子项目的时候跳转到编辑页面并且显示该项目内容
        adapter.setOnItemClickListener { id ->
            id?.let {
                val action = ScheduleListFragmentDirections.actionToScheduleEditFragment(it)
                findNavController().navigate(action)
            }
        }
        Log.d("test","这是2.。。。。")
        //在schedule列表显示的时候「添加schedule」悬浮按钮进行显示
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