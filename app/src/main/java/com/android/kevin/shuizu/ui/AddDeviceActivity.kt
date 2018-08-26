package com.android.kevin.shuizu.ui

import android.os.Bundle
import com.android.kevin.shuizu.entities.App_Keyword
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/25/025.
 */
class AddDeviceActivity : MyBaseActivity(){
    var deviceId = 0
    var deviceTitle = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceId = intent.getIntExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, 0)
        deviceTitle = intent.getStringExtra(App_Keyword.KEYWORD_WATER_MONITOR_TITLE)
        initActionBar(this, deviceTitle, isAdd = true)
    }
}