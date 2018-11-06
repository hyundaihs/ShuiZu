package com.android.kevin.shuizu.ui

import android.os.Bundle
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.SZApplication
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import kotlinx.android.synthetic.main.activity_web.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/11/6/006.
 */
class WebActivity:MyBaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initActionBar(this,"水族协议")
        contents.loadData(SZApplication.systemInfo.zc_contents,"text/html; charset=UTF-8", null)
    }
}