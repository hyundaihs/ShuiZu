package com.android.kevin.shuizu.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.App_Keyword
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import kotlinx.android.synthetic.main.activity_date_choose.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/9/5/005.
 */
class DateChooseActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_choose)
        initActionBar(this, "时间筛选")
        init()
    }

    private fun init() {
        val start = intent.getStringExtra(App_Keyword.KEYWORD_START_DATE)
        val end = intent.getStringExtra(App_Keyword.KEYWORD_END_DATE)
        if (start != null && end != null && !start.isEmpty() && !end.isEmpty()) {
            startDate.text = start
            endDate.text = end
        } else {
            val calendarUtil = CalendarUtil(System.currentTimeMillis())
            startDate.text = calendarUtil.format(CalendarUtil.YYYY_MM_DD)
            endDate.text = calendarUtil.format(CalendarUtil.YYYY_MM_DD)
        }
        startDate.setOnClickListener {
            chooseStart(startDate)
        }
        endDate.setOnClickListener {
            chooseStart(endDate)
        }
        submitDate.setOnClickListener {
            val intent = Intent()
            intent.putExtra(App_Keyword.KEYWORD_START_DATE, startDate.text.toString())
            intent.putExtra(App_Keyword.KEYWORD_END_DATE, endDate.text.toString())
            setResult(App_Keyword.KEYWORD_RESULT_OK, intent)
            finish()
        }
    }

    private fun chooseStart(view: TextView) {
        val builder = TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            if (null != date) {
                val calendarUtil = CalendarUtil(date.time)
                view.text = calendarUtil.format(CalendarUtil.YYYY_MM_DD)
            }
        })
        val calendarUtil1 = CalendarUtil(2018, 1, 1)
        val calendarUtil2 = CalendarUtil(System.currentTimeMillis())
        builder.setRangDate(calendarUtil1.c, calendarUtil2.c).setDate(CalendarUtil(view.text.toString()).c)
        //时间选择器
        val pvTime = builder.build()
        pvTime.show()
    }

}