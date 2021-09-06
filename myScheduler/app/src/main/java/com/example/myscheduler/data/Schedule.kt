package com.example.myscheduler.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Schedule : RealmObject() {
    //ScheduleID
    @PrimaryKey
    var id: Long = 0
    //Schedule日期时间
    var date: Date = Date()
    //Schedule标题
    var title: String = ""
    //Schedule细节
    var detail : String = ""
}