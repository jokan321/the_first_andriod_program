package com.example.myscheduler.fragment

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myscheduler.R
import com.example.myscheduler.activity.MainActivity
import com.example.myscheduler.databinding.FragmentScheduleEditBinding
import com.example.myscheduler.common.ConfirmDialog
import com.example.myscheduler.common.DateDialog
import com.example.myscheduler.common.TimeDialog
import com.example.myscheduler.model.Schedule
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * schedule编辑以及新登陆页面
 */
class ScheduleEditFragment : Fragment() {
    private var _binding: FragmentScheduleEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var realm: Realm

    private val args: ScheduleEditFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()//初始化Realm
    }

    //创建View时调用
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleEditBinding.inflate(inflater, container, false)
        return binding.root
    }
    //onCreateView结束之后调用
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.scheduleId != -1L) {
            //如果schedule已存在则获得其在数据库中的对象
            val schedule = realm.where<Schedule>().equalTo("id", args.scheduleId).findFirst()
            //设置当前页面的date值为该当schedule的date值
            binding.dateEdit.setText(DateFormat.format("yyyy/MM/dd", schedule?.date))
            binding.timeEdit.setText(DateFormat.format("HH:mm", schedule?.date))
            //设置当前页面的title值为该当schedule的title值
            binding.titleEdit.setText(schedule?.title)
            //设置当前页面的detail值为该当schedule的detail值
            binding.detailEdit.setText(schedule?.detail)
            //schedule已存在的时候，delete按钮进行显示
            binding.delete.visibility = View.VISIBLE
        } else {
            //schedule不存在的时候，delete按钮不进行显示
            binding.delete.visibility = View.INVISIBLE
        }
        (activity as? MainActivity)?.setFabVisible(View.INVISIBLE) //进入编辑页面则隐藏悬浮按钮
        //点击保存按钮将当前数据储存到数据库
        binding.save.setOnClickListener {
            val dialog = ConfirmDialog("該当スケジュールを保存しますか",
                "",{ saveSchedule(it) },
                "キャンセル",{
                    Snackbar.make(it,"キャンセルしました",Snackbar.LENGTH_SHORT).show()
                })
            dialog.show(parentFragmentManager, "save_dialog")
        }
        //点击删除按钮在数据库当中删除该schedule
        binding.delete.setOnClickListener {
            val dialog = ConfirmDialog("該当スケジュールを削除しますか",
                "削除",{ deleteSchedule(it) },
                "キャンセル", {
                    Snackbar.make(it,"キャンセルしました", Snackbar.LENGTH_SHORT).show()
                })
            dialog.show(parentFragmentManager, "delete_dialog")
        }
        //点击按钮调出日期选择dialog
        binding.dateButton.setOnClickListener {
            val dialog = DateDialog { date -> binding.dateEdit.setText(date) }
            dialog.show(parentFragmentManager, "date_dialog")
        }
        //点击按钮调出时间选择dialog
        binding.timeButton.setOnClickListener {
            val dialog = TimeDialog { time -> binding.timeEdit.setText(time) }
            dialog.show(parentFragmentManager, "time_dialog")
        }
    }

    /**
     * 保存schedule
     */
    private fun saveSchedule(view: View) {
        Log.d(TAG, "saveSchedule: schedule保存开始")
        when (args.scheduleId) {
            //如果当前schedule不存在则新录入数据库
            -1L -> {
                realm.executeTransaction { db: Realm ->
                    val maxId = db.where<Schedule>().max("id")//寻找当前数据库中schedule的最大ID
                    val nextId = (maxId?.toLong() ?: 0L) + 1L //设定当前要保存的schedule的id
                    val schedule = db.createObject<Schedule>(nextId)//数据库当中创建schedule
                    val date = "${binding.dateEdit} ${binding.timeEdit.text}".toDate()
                    if (date != null) schedule.date = date //如果日期不为空则将当前页面输入的日期设定给该schedule
                    schedule.title = binding.titleEdit.text.toString() //设定当前页面输入的title给该schedule
                    schedule.detail = binding.detailEdit.text.toString() //设定当前页面输入的详细给该schedule
                }
                Snackbar.make(view, this.getString(R.string.added), Snackbar.LENGTH_SHORT)
                    .setAction(this.getString(R.string.previous)){ findNavController().popBackStack() } //SnackBar按钮按下回到上一个页面
                    .setActionTextColor(Color.YELLOW) //设定SnackBar的字体为黄色
                    .show() //显示SnackBar
            } else -> {
                //如果当前schedule已经存在则将当前变更反应到数据库
                realm.executeTransaction { db: Realm ->
                    val schedule = db.where<Schedule>()
                        .equalTo("id", args.scheduleId)
                        .findFirst() //取得数据库中该schedule对象
                    val date = ("${binding.dateEdit.text} ${binding.timeEdit.text}").toDate()
                    if (date != null) schedule?.date = date //页面上的日期反应到该schedule对象中
                    schedule?.title = binding.titleEdit.text.toString() //页面上的title反应到该schedule对象中
                    schedule?.detail = binding.detailEdit.text.toString() //页面上的detail反应到该schedule对象中
                }
                Snackbar.make(view, this.getString(R.string.fixed), Snackbar.LENGTH_SHORT)
                    .setAction(this.getString(R.string.previous)) {findNavController().popBackStack()} //SnackBar按钮按下回到上一个页面
                    .setActionTextColor(Color.YELLOW) //设定SnackBar的字体为黄色
                    .show() //显示SnackBar
            }
        }
    }

    /**
     * 删除schedule
     */
    private fun deleteSchedule(view: View) {
        realm.executeTransaction { db: Realm ->
            db.where<Schedule>().equalTo("id", args.scheduleId)
                ?.findFirst()
                ?.deleteFromRealm()
        }
        Snackbar.make(view, this.getString(R.string.deleted),Snackbar.LENGTH_SHORT)
            .setActionTextColor(Color.YELLOW)
            .show()
        findNavController().popBackStack()
    }

    /**
     * 设定日期的format
     */
    private fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        return try {
            SimpleDateFormat(pattern, Locale.JAPAN).parse(this)
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: ParseException) {
            return null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null//View销毁的时候清除binding
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()//Fragment销毁的时候关闭数据库
    }

}