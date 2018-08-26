package com.android.kevin.shuizu

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.android.kevin.shuizu.entities.App_Actions.Companion.ACTION_WATER_MONITOR
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_WATER_MONITOR
import com.android.kevin.shuizu.entities.SZJC_SSSJ
import com.android.kevin.shuizu.entities.WaterMonitorInfoRes
import com.android.kevin.shuizu.entities.getInterface
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.google.gson.Gson
import org.jetbrains.anko.doAsync

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/22/022.
 */
class MonitorService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null


    override fun onCreate() {
        super.onCreate()

    }
}