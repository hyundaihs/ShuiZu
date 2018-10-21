package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.BWXX
import com.android.kevin.shuizu.entities.MemoSetInfoListRes
import com.android.kevin.shuizu.entities.TJBWXX
import com.android.kevin.shuizu.entities.getInterface
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_memoset.*
import kotlinx.android.synthetic.main.activity_memoset.*
import org.jetbrains.anko.toast


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/21/021.
 */
class AddMemoSetActivity : MyBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memoset)
        initActionBar(this, "备忘设置", rightBtn = "提交", rightClick = View.OnClickListener {
            if (check()) {
                submit()
            }
        })
        addMemoSetTime.setOnClickListener {
            val pvTime = TimePickerBuilder(this, OnTimeSelectListener { date, v ->
                val calendarUtil = CalendarUtil(date.time)
                addMemoSetTime.text = calendarUtil.format(CalendarUtil.YYYY_MM_DD_HH_MM)
            }).setType(booleanArrayOf(true, true, true, true, true, true)).build().show()
        }
    }

    private fun submit() {
        val map = mapOf(
                Pair("title", addMemoSetTitle.text.toString()),
                Pair("contents", addMemoSetContent.text.toString()),
                Pair("tx_time", addMemoSetTime.text.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("提交成功")
                finish()
            }

            override fun onError(context: Context, error: String) {
                context.toast(error)
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, TJBWXX.getInterface(), map)
    }

    private fun check(): Boolean {
        if (addMemoSetTitle.text.isEmpty()) {
            addMemoSetTitle.error = "请填写备忘标题"
            return false
        }
        if (addMemoSetContent.text.isEmpty()) {
            addMemoSetContent.error = "请填写备忘内容"
            return false
        }
        if (addMemoSetTime.text.isEmpty() || addMemoSetTime.text.toString() == "点击获取") {
            toast("请选择提醒时间")
            return false
        }
        return true
    }


}