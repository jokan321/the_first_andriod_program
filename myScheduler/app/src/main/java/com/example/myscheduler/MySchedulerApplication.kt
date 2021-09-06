package com.example.myscheduler

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MySchedulerApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        //初始化Realm数据库
        Realm.init(this)
        //设置Realm数据库
        val config = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true).build()
        Realm.setDefaultConfiguration(config)
    }
}