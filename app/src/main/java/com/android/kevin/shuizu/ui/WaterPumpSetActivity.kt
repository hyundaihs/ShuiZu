package com.android.kevin.shuizu.ui

import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/30/030.
 */
class WaterPumpSetActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_pump)
        initActionBar(this, "智能水泵", rightBtn = "提交", rightClick = View.OnClickListener {

        })

    }
}