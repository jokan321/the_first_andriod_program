package com.example.myscheduler.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myscheduler.activity.MainActivity
import com.example.myscheduler.common.ConfirmDialog
import com.example.myscheduler.common.DateDialog
import com.example.myscheduler.common.TimeDialog
import com.example.myscheduler.data.Schedule
import com.example.myscheduler.databinding.FragmentScheduleEditBinding
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScheduleEditFragment : Fragment() {

    private var _binding: FragmentScheduleEditBinding? = null
    private val binding get() = _binding!!
    private val args: ScheduleEditFragmentArgs by navArgs()

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //如果指定了schedule，就将其在编辑页面表示出来
        if (args.scheduleId != -1L) {
            val schedule = realm.where<Schedule>().equalTo("id",args.scheduleId).findFirst()
            binding.dateEdit.setText(DateFormat.format("yyyy/MM/dd",schedule?.date))
            binding.timeEdit.setText(DateFormat.format("HH:mm",schedule?.date))
            binding.titleEdit.setText(schedule?.title)
            binding.detialEdit.setText(schedule?.detail)
            //指定了schedule的话，显示删除按钮
            binding.deleteButton.visibility = View.VISIBLE
        } else {
            //如果没有指定schedule，删除按钮不允显示
            binding.deleteButton.visibility = View.INVISIBLE
        }
        //进入schedule编辑页面隐藏悬浮按钮
        (activity as? MainActivity)?.setFabVisible(View.INVISIBLE)
        //设定储存按钮
        binding.saveButton.setOnClickListener {
            //实例化保存Schedule用的dialog
            val dialog = ConfirmDialog("保存しますか",
                "保存", { saveSchedule(it) },
                "キャンセル", { Snackbar.make(it, "キャンセルしました", Snackbar.LENGTH_SHORT).show() })
            //调出保存Schedule用的dialog
            dialog.show(parentFragmentManager, "save_dialog")
        }
        //设定删除按钮
        binding.deleteButton.setOnClickListener {
            //实例化删除Schedule用的dialog
            val dialog = ConfirmDialog("削除しますか",
                "削除",{ deleteSchedule(it) },
                "キャンセル",{ Snackbar.make(it, "キャンセルしました", Snackbar.LENGTH_SHORT).show() })
            //调出删除Schedule用的dialog
            dialog.show(parentFragmentManager, "delete_dialog")
        }
        //设定日期按钮
        binding.dateButton.setOnClickListener {
            //调用选择日期dialog
            DateDialog{ date ->
                binding.dateEdit.setText(date)
            }.show(parentFragmentManager, "date_dialog")
        }
        //设定时间按钮
        binding.timeButton.setOnClickListener {
            //调用选择时间dialog
            TimeDialog{ time ->
                binding.timeEdit.setText(time)
            }.show(parentFragmentManager, "time_dialog")
        }
    }

    /**
     * 存储schedule
     */
    private fun saveSchedule(view: View) {
        when (args.scheduleId) {
            //没有选定schedule的数据的话，就新追加schedule的数据到数据库
            -1L -> {
                realm.executeTransaction { db: Realm ->
                    val maxId = db.where<Schedule>().max("id")
                    val nextId = (maxId?.toLong() ?: 0L) + 1L
                    val schedule = db.createObject<Schedule>(nextId)
                    val date = "${binding.dateEdit.text} ${binding.timeEdit.text}".toDate()
                    if (date != null) schedule.date = date
                    schedule.title = binding.timeEdit.text.toString()
                    schedule.detail = binding.detialEdit.text.toString()
                }
                Snackbar.make(view, "追加しました", Snackbar.LENGTH_SHORT).
                setAction("戻る"){
                    findNavController().popBackStack()
                }.setActionTextColor(Color.YELLOW).show()
            }
            //选定了schedule的话，就将其在数据库的内容按照页面编辑的内容修改
            else -> {
                realm.executeTransaction { db: Realm ->
                    val schedule = db.where<Schedule>().equalTo("id",args.scheduleId).findFirst()
                    val date = ("${binding.detialEdit.text}" + "${binding.titleEdit.text}").toDate()
                    if (date != null) schedule?.date = date
                    schedule?.title = binding.titleEdit.text.toString()
                    schedule?.detail = binding.detialEdit.text.toString()
                }
                Snackbar.make(view, "修正しました",Snackbar.LENGTH_SHORT).setAction("戻る　") {
                    findNavController().popBackStack()
                }.setActionTextColor(Color.YELLOW).show()
            }
        }
    }

    /**
     * 删除选定的schedule
     */
    private fun deleteSchedule(view: View) {
        realm.executeTransaction { db: Realm ->
            //根据当前选中的id找到数据库当中的schedule然后删除
            db.where<Schedule>().equalTo("id", args.scheduleId)
                ?.findFirst()
                ?.deleteFromRealm()
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

    private fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        return try {
            SimpleDateFormat(pattern).parse(this)
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e:ParseException) {
            return null
        }
    }
}