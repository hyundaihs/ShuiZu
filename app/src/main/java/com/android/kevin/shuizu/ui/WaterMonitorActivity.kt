package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CHART_DATA_TYPE
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_WATER_MONITOR_ID
import com.android.kevin.shuizu.utils.ChartUtil
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.CalendarUtil.YYYYMMDD
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_water_monitor.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/22/022.
 */
class WaterMonitorActivity : MyBaseActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        val intent = Intent(this, ChartActivity::class.java)
        val bundle = Bundle()
        when (v.id) {
            R.id.tempHistory -> {
                intent.putExtra(KEYWORD_CHART_DATA_TYPE, ChartDataType.WD)
            }
            R.id.phHistory -> {
                intent.putExtra(KEYWORD_CHART_DATA_TYPE, ChartDataType.PH)
            }
            R.id.tdsHistory -> {
                intent.putExtra(KEYWORD_CHART_DATA_TYPE, ChartDataType.TDS)
            }
        }
        intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, deviceId)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    var flag = false
    var waterTempData = ArrayList<WaterHistoryData>()
    var waterPHData = ArrayList<WaterHistoryData>()
    var waterTDSData = ArrayList<WaterHistoryData>()
    private var deviceId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_monitor)
        initActionBar(this, "水质监测器", rightClick = View.OnClickListener {

        })
        deviceId = intent.getIntExtra(KEYWORD_WATER_MONITOR_ID, 0)
        getDatas(deviceId)
        getHistoryData(deviceId)
        tempHistory.setOnClickListener(this)
        phHistory.setOnClickListener(this)
        tdsHistory.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        flag = false
    }

    private fun getDatas(id: Int) {
        flag = true
        doAsync {
            while (flag) {
                uiThread {
                    waterMonitor(id)
                }
                Thread.sleep(10000)
            }
        }
    }

    private fun getHistoryData(id: Int) {
        getWaterTemp(id)
        getWaterPH(id)
        getWaterTDS(id)
    }

    private fun refresh(waterMonitor: WaterMonitorInfo) {
        waterLevel.text = WATER_LEVEL[waterMonitor.zl]
        waterTemp.text = "当前水温 / ${waterMonitor.wd}℃"
        waterPH.text = "当前PH值 / ${waterMonitor.ph}"
        waterTDS.text = "当前TDS值 / ${waterMonitor.tds}"
    }

    /**
     * 水质监测
     */
    private fun waterMonitor(id: Int) {
        val map = mapOf(Pair("id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterMonitorInfoRes = Gson().fromJson(result, WaterMonitorInfoRes::class.java)
                val waterMonitor = waterMonitorInfoRes.retRes
                refresh(waterMonitor)
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

        }, false).postRequest(this, getInterface(SZJC_SSSJ), map)
    }

    private fun getWaterTemp(id: Int) {
        val calendarUtil = CalendarUtil()
        val map = mapOf(Pair("id", id.toString()), Pair("data_type", "wd"), Pair("days", calendarUtil.format(YYYYMMDD)))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterHistoryDataRes = Gson().fromJson(result, WaterHistoryDataRes::class.java)
                waterTempData.clear()
                waterTempData.addAll(waterHistoryDataRes.retRes)
                val chartUtil = ChartUtil(this@WaterMonitorActivity, tempMonitor, waterTempData, ChartDataType.WD)
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

    private fun getWaterPH(id: Int) {
        val calendarUtil = CalendarUtil()
        val map = mapOf(Pair("id", id.toString()), Pair("data_type", "ph"), Pair("days", calendarUtil.format(YYYYMMDD)))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterHistoryDataRes = Gson().fromJson(result, WaterHistoryDataRes::class.java)
                waterPHData.clear()
                waterPHData.addAll(waterHistoryDataRes.retRes)
                val chartUtil = ChartUtil(this@WaterMonitorActivity, phMonitor, waterPHData, ChartDataType.PH)
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

    private fun getWaterTDS(id: Int) {
        val calendarUtil = CalendarUtil()
        val map = mapOf(Pair("id", id.toString()), Pair("data_type", "tds"), Pair("days", calendarUtil.format(YYYYMMDD)))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterHistoryDataRes = Gson().fromJson(result, WaterHistoryDataRes::class.java)
                waterTDSData.clear()
                waterTDSData.addAll(waterHistoryDataRes.retRes)
                val chartUtil = ChartUtil(this@WaterMonitorActivity, tdsMonitor, waterTDSData, ChartDataType.TDS)
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