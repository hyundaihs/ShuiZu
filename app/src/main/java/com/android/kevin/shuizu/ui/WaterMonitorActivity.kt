package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CHART_DATA_TYPE
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_WATER_MONITOR_ID
import com.android.kevin.shuizu.utils.DayChartUtil
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.CalendarUtil.YYYYMMDD
import com.android.shuizu.myutillibrary.utils.CustomDialog
import com.github.mikephil.charting.data.Entry
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
    private var deviceId = 0
    private val tips = arrayListOf(R.mipmap.px, R.mipmap.low, R.mipmap.high)
    private val texts = arrayListOf("正常", "偏低", "偏高")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_monitor)
        initActionBar(this, "水质监测器", rightClick = View.OnClickListener {
            val inten = Intent(this@WaterMonitorActivity, WaterMonitorDataSetActivity::class.java)
            inten.putExtra(KEYWORD_WATER_MONITOR_ID, deviceId)
            startActivity(inten)
        })
        deviceId = intent.getIntExtra(KEYWORD_WATER_MONITOR_ID, 0)
        getHistoryData(deviceId)
        tempHistory.setOnClickListener(this)
        phHistory.setOnClickListener(this)
        tdsHistory.setOnClickListener(this)
        deleteDevice.setOnClickListener {
            CustomDialog("提示", "确定要删除设备吗？", positiveClicked = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    deleteDevice(deviceId)
                }
            }, negative = "取消")

        }
    }

    override fun onResume() {
        super.onResume()
        getDatas(deviceId)
    }

    override fun onStop() {
        super.onStop()
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
        waterTempLevel.text = texts[waterMonitor.wd_zl]
        waterPHLevel.text = texts[waterMonitor.ph_zl]
        waterTDSLevel.text = texts[waterMonitor.tds_zl]
        var rightDrawable = resources.getDrawable(tips[waterMonitor.wd_zl])
        rightDrawable.setBounds(0, 0, rightDrawable.minimumWidth, rightDrawable.minimumHeight)
        waterTempLevel.setCompoundDrawablesRelative(rightDrawable, null, null, null)
        rightDrawable = resources.getDrawable(tips[waterMonitor.ph_zl])
        rightDrawable.setBounds(0, 0, rightDrawable.minimumWidth, rightDrawable.minimumHeight)
        waterPHLevel.setCompoundDrawablesRelative(rightDrawable, null, null, null)
        rightDrawable = resources.getDrawable(tips[waterMonitor.tds_zl])
        rightDrawable.setBounds(0, 0, rightDrawable.minimumWidth, rightDrawable.minimumHeight)
        waterTDSLevel.setCompoundDrawablesRelative(rightDrawable, null, null, null)
    }

    private fun deleteDevice(id: Int) {
        val map = mapOf(Pair("id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                context.toast("删除成功")
                finish()
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

        }, false).postRequest(this, SCSB.getInterface(), map)
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

        }, false).postRequest(this, SZJC_SSSJ.getInterface(), map)
    }

    private fun getWaterTemp(id: Int) {
        val calendarUtil = CalendarUtil()
        val map = mapOf(Pair("id", id.toString()), Pair("data_type", "wd"), Pair("days", calendarUtil.format(YYYYMMDD)))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterHistoryDataRes = Gson().fromJson(result, WaterHistoryDataRes::class.java)
                val values = ArrayList<Entry>()
                for (i in 0 until waterHistoryDataRes.retRes.size) {
                    values.add(Entry(waterHistoryDataRes.retRes[i].x.toFloat(), waterHistoryDataRes.retRes[i].y))
                }
                val chartUtil = DayChartUtil(this@WaterMonitorActivity, tempMonitor, ChartDataType.WD)
                chartUtil.setTitle("")
                chartUtil.setData(values)
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

        }, false).postRequest(this, SZJC_TJTSJ.getInterface(), map)
    }

    private fun getWaterPH(id: Int) {
        val calendarUtil = CalendarUtil()
        val map = mapOf(Pair("id", id.toString()), Pair("data_type", "ph"), Pair("days", calendarUtil.format(YYYYMMDD)))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterHistoryDataRes = Gson().fromJson(result, WaterHistoryDataRes::class.java)
                val values = ArrayList<Entry>()
                for (i in 0 until waterHistoryDataRes.retRes.size) {
                    values.add(Entry(waterHistoryDataRes.retRes[i].x.toFloat(), waterHistoryDataRes.retRes[i].y))
                }
                val chartUtil = DayChartUtil(this@WaterMonitorActivity, phMonitor, ChartDataType.PH)
                chartUtil.setTitle("")
                chartUtil.setData(values)
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

        }, false).postRequest(this, SZJC_TJTSJ.getInterface(), map)
    }

    private fun getWaterTDS(id: Int) {
        val calendarUtil = CalendarUtil()
        val map = mapOf(Pair("id", id.toString()), Pair("data_type", "tds"), Pair("days", calendarUtil.format(YYYYMMDD)))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val waterHistoryDataRes = Gson().fromJson(result, WaterHistoryDataRes::class.java)
                val values = ArrayList<Entry>()
                for (i in 0 until waterHistoryDataRes.retRes.size) {
                    values.add(Entry(waterHistoryDataRes.retRes[i].x.toFloat(), waterHistoryDataRes.retRes[i].y))
                }
                val chartUtil = DayChartUtil(this@WaterMonitorActivity, tdsMonitor, ChartDataType.TDS)
                chartUtil.setTitle("")
                chartUtil.setData(values)
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

        }, false).postRequest(this, SZJC_TJTSJ.getInterface(), map)
    }
}