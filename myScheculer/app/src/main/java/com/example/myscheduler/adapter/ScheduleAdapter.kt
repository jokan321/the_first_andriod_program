package com.example.myscheduler.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myscheduler.model.Schedule
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

/**
 * Schedule转换器：数据库当中取出数据反映到页面
 * RealmRecyclerViewAdapter(data: OrderRealmCollection<T>, autoUpdate: Boolean)
 * data:RecyclerView里面表示的data
 * autoUpdate: ture的场合，页面显示则自动更新
 */
class ScheduleAdapter(data: OrderedRealmCollection<Schedule>): RealmRecyclerViewAdapter<Schedule, ScheduleAdapter.ViewHolder>(data, true) {

    private var listener: ((Long?) -> Unit)? = null

    fun setOnItemClickListener(listener:(Long?) -> Unit) {
        this.listener = listener
    }

    init {
        //data当中的一个项目作为固有ID(key)时，参数设定为true
        //对应数据库当中的主键，复写getItemId，使用key之后，更新数据不用刷新整个页面而是只刷新更新的地方
        setHasStableIds(true)
    }

    /**
     *View类代表用户界面中基本的构建块。一个View在屏幕中占据一个矩形区域、并且负责绘制和事件处理。
     *View是所有widgets的基础类，widgets是我们通常用于创建和用户交互的组件，比如按钮、文本输入框等等。子类ViewGroup是所有布局（layout）的基础类。
     *layout是一个不看见的容器，里面堆放着其他的view或者ViewGroup，并且设置他们的布局属性。
     */
    class ViewHolder(cell: View): RecyclerView.ViewHolder(cell) {
        val date: TextView = cell.findViewById(android.R.id.text1)
        val title: TextView = cell.findViewById(android.R.id.text2)
    }

    /**
     * 每当需要cell的时候调用此方法，会内部实例化ViewHolder对象并返回
     * onCreateViewHolder(parent:ViewGroup, viewType: Int)
     * parent: 追加新View的ViewGroup
     * viewType: 新View的ViewType(viewType即是想适用复数的design等场合将其分开使用的设计，本次因为cell的design只有一个，所以不使用)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAdapter.ViewHolder {
        //from方法会从指定的上下文取得LayoutInflater（布局填充器，使用它来把布局转为一个View)的实例
        val inflater = LayoutInflater.from(parent.context)
        //inflate(resource: Int, root: ViewGroup!, attachToRoot: Boolean):View!
        //resource: 使用的layout的XML的id
        //root: 第三个参数为false的场合，将作成的view指定为attach view
        //attachToRoot: 想返回通过XML文件作成的View的时候，此参数设定为false
        //此处指定的(simple_expandable_list_item_2)是安卓SDK自带的layoutXML文件
        val view = inflater.inflate(android.R.layout.simple_expandable_list_item_2, parent, false)
        return ViewHolder(view)
    }

    /**
     * 在有必要与指定位置显示数据的时候，RecyclerView会调用此方法
     * 此方法会对ViewHolder保持对View所实际表示的画像或文字等的内容进行设置
     * holder: 更新对象的ViewHolder
     * position: 更新使用对象的适配器的dataset当中的位置
     */
    override fun onBindViewHolder(holder: ScheduleAdapter.ViewHolder, position: Int) {
        val schedule: Schedule? = getItem(position)
        holder.date.text = DateFormat.format("yyyy/MM/dd HH:mm", schedule?.date)
        holder.title.text = schedule?.title
        //cell所使用的view被触碰之时触发此方法
        holder.itemView.setOnClickListener{
            listener?.invoke(schedule?.id)
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }

}