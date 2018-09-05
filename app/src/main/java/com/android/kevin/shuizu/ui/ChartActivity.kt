package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.utils.ChartUtil
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chart.*
import kotlinx.android.synthetic.main.activity_water_monitor.*
import kotlinx.android.synthetic.main.fragment_action_log.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/23/023.
 */
class ChartActivity : MyBaseActivity() {
    var chartData = ArrayList<WaterHistoryData>()
    var type = ChartDataType.WD
    private var deviceId = 0
    var startDate = ""
    var endDate = ""
    var chartUtil: ChartUtil? = null
    private var currPage = 0
    private var isMax = true
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
        initActionBar(this, title, rightBtn = "时间筛选", rightClick = View.OnClickListener { _ ->
            val intent = Intent(this, DateChooseActivity::class.java)
            intent.putExtra(App_Keyword.KEYWORD_START_DATE, startDate)
            intent.putExtra(App_Keyword.KEYWORD_END_DATE, endDate)
            startActivityForResult(intent, App_Keyword.KEYWORD_TIME_DATE_CHOOSER_REQUEST)
        })
        startDate = CalendarUtil(System.currentTimeMillis()).format(CalendarUtil.YYYY_MM_DD)
        getChartData(deviceId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == App_Keyword.KEYWORD_TIME_DATE_CHOOSER_REQUEST && resultCode == App_Keyword.KEYWORD_RESULT_OK && data != null) {
            startDate = data.getStringExtra(App_Keyword.KEYWORD_START_DATE)
            endDate = data.getStringExtra(App_Keyword.KEYWORD_END_DATE)
            getChartData(deviceId)
        }
    }

    private fun getChartData(id: Int) {
        var date = ""
        if (currPage == 0) {
            date = startDate
        } else {
            val c = CalendarUtil(CalendarUtil(startDate).timeInMillis + (24 * 60 * 60 * 1000 * currPage))
            val temp = c.format(CalendarUtil.YYYY_MM_DD)
            isMax = temp == endDate
            date = temp
        }
        val map = when (type) {
            ChartDataType.WD -> {
                mapOf(Pair("id", id.toString()), Pair("data_type", "wd"), Pair("days", date.replace("_", "")), Pair("time_limit", 20.toString()))
            }
            ChartDataType.PH -> {
                mapOf(Pair("id", id.toString()), Pair("data_type", "ph"), Pair("days", date.replace("_", "")), Pair("time_limit", 20.toString()))
            }
            ChartDataType.TDS -> {
                mapOf(Pair("id", id.toString()), Pair("data_type", "tds"), Pair("days", date.replace("_", "")), Pair("time_limit", 20.toString()))
            }
            else -> {
                mapOf(Pair("id", id.toString()), Pair("data_type", "wd"), Pair("days", date.replace("_", "")), Pair("time_limit", 20.toString()))
            }
        }
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterHistoryDataRes = Gson().fromJson(result, WaterHistoryDataRes::class.java)
                if (currPage == 0) {
                    chartData.clear()
                }
                chartData.addAll(waterHistoryDataRes.retRes)
                if (null == chartUtil) {
                    chartUtil = ChartUtil(this@ChartActivity, chartMonitor, chartData, type, object : ChartUtil.OnEdgeListener {
                        override fun edgeLoad(x: Float, left: Boolean) {
                            if (left) {
                                if (currPage <= 0) {
                                    toast("前方没有更多了")
                                } else {
                                    currPage--
                                    getChartData(deviceId)
                                }
                            } else {
                                if (isMax) {
                                    toast("后方没有更多了")
                                } else {
                                    currPage++
                                    getChartData(deviceId)
                                }
                            }
                        }
                    })
                    chartUtil?.show()
                } else {
                    chartUtil?.setData(chartData)
                    chartUtil?.notifyDataSetChanged()
                }
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