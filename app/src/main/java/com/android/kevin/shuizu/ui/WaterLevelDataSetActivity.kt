package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CustomDialog
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_water_level_data_set.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * Created by kevin on 2018/9/22.
 */
class WaterLevelDataSetActivity : MyBaseActivity() {

    private var deviceId = 0
    private var isCodeChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_level_data_set)
        initActionBar(this, "水位报警设置")
        deviceId = intent.getIntExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, 0)
        levelSwitchWarn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isCodeChecked) {
                isCodeChecked = true
                levelSwitchWarn.isEnabled = false
                saveBaoJinBJ()
            }
        }
        levelDeleteDevice.setOnClickListener {
            CustomDialog("提示", "确定要删除设备吗？", positiveClicked = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    deleteDevice(deviceId)
                }
            }, negative = "取消")
        }
        getBaoJinDataState()
    }

    private fun saveBaoJinBJ() {
        val map = mapOf(Pair("id", deviceId.toString()),
                Pair("v_1", if (levelSwitchWarn.isChecked) "1" else "0"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getBaoJinDataState()
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(this@WaterLevelDataSetActivity, getInterface(SZ_BJKG), map)
    }

    private fun getBaoJinDataState() {
        val map = mapOf(Pair("id", deviceId.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val baoJin_InfoRes = Gson().fromJson(result, BaoJin_InfoRes::class.java)
                val baoJinInfo = baoJin_InfoRes.retRes
                levelWarnResult.text = WaterMonitorDataSetActivity.BAOJIN_REL[baoJinInfo.sz_status]
                if (baoJinInfo.sz_status != 1) {
                    isCodeChecked = false
                    levelSwitchWarn.isEnabled = true
                    levelSwitchWarn.isChecked = baoJinInfo.v_1 == 1f
                }
                if (baoJinInfo.sz_status == 1 && !isFinishing) {
                    doAsync {
                        Thread.sleep(5000)
                        getBaoJinDataState()
                    }
                }
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(this@WaterLevelDataSetActivity, getInterface(KG_INFO), map)
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

        }, false).postRequest(this, getInterface(SCSB), map)
    }
}