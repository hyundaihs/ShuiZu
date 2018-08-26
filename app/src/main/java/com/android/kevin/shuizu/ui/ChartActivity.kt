package com.android.kevin.shuizu.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.utils.ChartUtil
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chart.*
import kotlinx.android.synthetic.main.activity_water_monitor.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/23/023.
 */
class ChartActivity : MyBaseActivity() {
    var chartData = ArrayList<WaterHistoryData>()
    var type = ChartDataType.WD
    private var deviceId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        type = intent.getIntExtra(App_Keyword.KEYWORD_CHART_DATA_TYPE, 0)
        deviceId = intent.getIntExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, 0)
        val title = when (type) {
            ChartDataType.WD -> {
                "水温历史记录"
            }
            ChartDataType.PH -> {
                "PH值历史记录"
            }
            ChartDataType.TDS -> {
                "TDS值历史记录"
            }
            else -> {
                "水温历史记录"
            }
        }
        initActionBar(this, title, rightBtn = "时间筛选", rightClick = View.OnClickListener{ _ ->

        })
        getChartData(deviceId)
    }

    private fun getChartData(id: Int) {
        val calendarUtil = CalendarUtil()
        val map = when (type) {
            ChartDataType.WD -> {
                mapOf(Pair("id", id.toString()), Pair("data_type", "wd"), Pair("days", calendarUtil.format(CalendarUtil.YYYYMMDD)))
            }
            ChartDataType.PH -> {
                mapOf(Pair("id", id.toString()), Pair("data_type", "ph"), Pair("days", calendarUtil.format(CalendarUtil.YYYYMMDD)))
            }
            ChartDataType.TDS -> {
                mapOf(Pair("id", id.toString()), Pair("data_type", "tds"), Pair("days", calendarUtil.format(CalendarUtil.YYYYMMDD)))
            }
            else -> {
                mapOf(Pair("id", id.toString()), Pair("data_type", "wd"), Pair("days", calendarUtil.format(CalendarUtil.YYYYMMDD)))
            }
        }
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterHistoryDataRes = Gson().fromJson(result, WaterHistoryDataRes::class.java)
                chartData.clear()
                chartData.addAll(waterHistoryDataRes.retRes)
                val chartUtil = ChartUtil(this@ChartActivity, chartMonitor, chartData, type)
                chartUtil.show()
            }

            override fun onError(context: Context, error: String) {
                context.toast(error)
            }

            override fun onLoginErr(context: Context) {
//                    context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
//                        val intent = Intent(context, LoginActivity::class.java)
//                        startActivity(intent)
//                    })
            }

        }, false).postRequest(this, getInterface(SZJC_TJTSJ), map)
    }
}