package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_CURR
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_MAX
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_MIN
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_TITLE
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_data_set.*
import kotlinx.android.synthetic.main.activity_water_pump.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import com.android.kevin.shuizu.MainActivity
import com.bigkoo.pickerview.view.OptionsPickerView


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/30/030.
 */
class WaterPumpSetActivity : MyBaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pumpWLevel -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "运行模式")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 100)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 1)
                intent.putExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_ISFLOAT, false)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, pumpWLevel.text.toString().toFloat())
                startActivityForResult(intent, 0)
            }
            R.id.pumpWaveLevel -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "造浪强度")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 100)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 1)
                intent.putExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_ISFLOAT, false)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, pumpWaveLevel.text.toString().toFloat())
                startActivityForResult(intent, 1)
            }
            R.id.pumpModelHigh -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "投食高位")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 135)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 10)
                intent.putExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_ISFLOAT, false)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, pumpModelHigh.text.toString().toFloat())
                startActivityForResult(intent, 2)
            }
            R.id.pumpModelLow -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "投食低位")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 135)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 10)
                intent.putExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_ISFLOAT, false)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, pumpModelLow.text.toString().toFloat())
                startActivityForResult(intent, 3)
            }
            R.id.pumpDeleteDevice -> {
                deleteDevice(deviceId)
            }
            R.id.currModel -> {
                val pvOptions = OptionsPickerBuilder(this, OnOptionsSelectListener { options1, option2, options3, v ->
                    //返回的分别是三个级别的选中位置
                    isModelChange = true
                    currModel.text = models[options1]
                }).build<String>()
                pvOptions.setPicker(models)
                pvOptions.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == App_Keyword.KEYWORD_RESULT_OK && data != null) {
            val value = data.getFloatExtra("data", 1f).toInt()
            when (requestCode) {
                0 -> {
                    isLevelChange = true
                    pumpWLevel.text = value.toString()
                }
                1 -> {
                    isWaveChange = true
                    pumpWaveLevel.text = value.toString()
                }
                2 -> {
                    isEatChange = true
                    pumpModelHigh.text = value.toString()
                }
                3 -> {
                    isEatChange = true
                    pumpModelLow.text = value.toString()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private var deviceId = 0
    private val models = arrayListOf<String>("投食", "运行", "造浪")
    private var isTouched = false
    private var isLevelChange = false
    private var isWaveChange = false
    private var isEatChange = false
    private var isModelChange = false

    companion object {
        private const val ON_OFF = 0
        private const val LEVEL = 1
        private const val WAVE = 2
        private const val EAT = 3
        private const val MODEL = 4
        val STATUS = arrayOf("未设置", "正在设置", "设置成功", "设置失败")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_pump)
        deviceId = intent.getIntExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, 0)
        initActionBar(this, "智能水泵", rightBtn = "提交", rightClick = View.OnClickListener {
            if (!isLevelChange && !isWaveChange && !isEatChange && !isModelChange) {
                toast("没有任何修改")
            } else {
                if (isModelChange) {
                    currModel.isEnabled = false
                    setCurrModel()
                }
                if (isLevelChange) {
                    pumpWLevel.isEnabled = false
                    setRuningLevelState()
                }
                if (isWaveChange) {
                    pumpWaveLevel.isEnabled = false
                    setWaveState()
                }
                if (isEatChange) {
                    pumpModelHigh.isEnabled = false
                    pumpModelLow.isEnabled = false
                    setEatState()
                }
            }
        })
        initViews()
    }

    private fun initViews() {
        pumpSwitchWarn.setOnTouchListener { _, _ ->
            isTouched = true
            false
        }
        pumpSwitchWarn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isTouched) {
                isTouched = false
                pumpSwitchWarn.isEnabled = false
                setOnOffState()
            }
        }
        pumpWLevel.setOnClickListener(this)
        pumpWaveLevel.setOnClickListener(this)
        pumpModelHigh.setOnClickListener(this)
        pumpModelLow.setOnClickListener(this)
        pumpDeleteDevice.setOnClickListener(this)
        currModel.setOnClickListener(this)
        getStatus(MODEL)
        getStatus(ON_OFF)
        getStatus(LEVEL)
        getStatus(WAVE)
        getStatus(EAT)
    }

    private fun setOnOffState() {
        val map = mapOf(Pair("id", deviceId.toString()), Pair("v_1", if (pumpSwitchWarn.isChecked) "1" else "0"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getStatus(ON_OFF)
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

        }, false).postRequest(this@WaterPumpSetActivity, SBKGJ.getInterface(Gson().toJson(map)), map)
    }

    private fun setRuningLevelState() {
        val map = mapOf(Pair("id", deviceId.toString()), Pair("v_1", pumpWLevel.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getStatus(LEVEL)
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

        }, false).postRequest(this@WaterPumpSetActivity, SBYX.getInterface(Gson().toJson(map)), map)
    }

    private fun setWaveState() {
        val map = mapOf(Pair("id", deviceId.toString()), Pair("v_1", pumpWaveLevel.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getStatus(WAVE)
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

        }, false).postRequest(this@WaterPumpSetActivity, SBZL.getInterface(Gson().toJson(map)), map)
    }

    private fun setEatState() {
        val map = mapOf(Pair("id", deviceId.toString()), Pair("v_1", pumpModelHigh.text.toString())
                , Pair("v_2", pumpModelLow.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getStatus(EAT)
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

        }, false).postRequest(this@WaterPumpSetActivity, SBTS.getInterface(Gson().toJson(map)), map)
    }

    private fun setCurrModel() {
        val map = mapOf(Pair("id", deviceId.toString()), Pair("v_1", (models.indexOf(currModel.text.toString()) + 1).toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getStatus(MODEL)
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

        }, false).postRequest(this@WaterPumpSetActivity, SBTS.getInterface(Gson().toJson(map)), map)
    }

    private fun getStatus(type: Int) {
        val inter = when (type) {
            ON_OFF -> SBKGJ_INFO
            LEVEL -> SBYX_INFO
            WAVE -> SBZL_INFO
            EAT -> SBTS_INFO
            MODEL -> SBYXMS_INFO
            else -> SBYXMS_INFO
        }
        val map = mapOf(Pair("id", deviceId.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val baoJin_InfoRes = Gson().fromJson(result, BaoJin_InfoRes::class.java)
                val baoJinInfo = baoJin_InfoRes.retRes
                when (type) {
                    ON_OFF -> {
                        onOffResult.text = STATUS[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            pumpSwitchWarn.isChecked = baoJinInfo.v_1 == 1f
                            pumpSwitchWarn.isEnabled = true
                            isTouched = false
                        }
                    }
                    LEVEL -> {
                        runingResult.text = STATUS[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            isLevelChange = false
                            pumpWLevel.isEnabled = true
                            pumpWLevel.text = baoJinInfo.v_1.toInt().toString()
                        }
                    }
                    WAVE -> {
                        waveResult.text = STATUS[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            isWaveChange = false
                            pumpWaveLevel.isEnabled = true
                            pumpWaveLevel.text = baoJinInfo.v_1.toInt().toString()
                        }
                    }
                    EAT -> {
                        tsResult.text = STATUS[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            isEatChange = false
                            pumpModelHigh.isEnabled = true
                            pumpModelLow.isEnabled = true
                            pumpModelHigh.text = baoJinInfo.v_1.toInt().toString()
                            pumpModelLow.text = baoJinInfo.v_2.toInt().toString()
                        }
                    }
                    MODEL -> {
                        currResult.text = STATUS[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            isModelChange = false
                            currModel.text = models[baoJinInfo.v_1.toInt() - 1]
                            currModel.isEnabled = true
                        }
                    }
                }
                if (baoJinInfo.sz_status == 1 && !isFinishing) {
                    doAsync {
                        Thread.sleep(5000)
                        getStatus(type)
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

        }, false).postRequest(this@WaterPumpSetActivity, inter.getInterface(Gson().toJson(map)), map)
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

        }, false).postRequest(this, SCSB.getInterface(Gson().toJson(map)), map)
    }
}