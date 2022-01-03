package com.example.myscheduler

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MySchedulerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化Realm数据库
        Realm.init(this)
        //设定Realm数据库:允许在UI线程往数据库写入数据，数据的写入是一个比较耗费时间的处理，所以在初期状态的时候都是默认不许可的
        //本次因为写入数据较少，将其变更为允许在UI线程当中写入数据也是没有什么问题的
        val config = RealmConfiguration.Builder().
            allowWritesOnUiThread(true).build()
        Realm.setDefaultConfiguration(config)
    }
}