package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_CURR
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_FLOAT_NUM
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_ISFLOAT
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_MAX
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_MIN
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_TITLE
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_data_set.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * Created by kevin on 2018/9/1.
 */
class WaterMonitorDataSetActivity : MyBaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if(isTouched){
            switchWarn.isEnabled = false
            saveBaoJinBJ()
        }
    }

    companion object {
        const val SEND_DATA = "发送数据"
        const val VERIFING = "正在校验"
        const val VERIFY_SUCCESS = "校验通过"
        const val VERIFY_FAILED = "校验失败"
        const val BAOJIN_SW = 0
        const val BAOJIN_PH = 1
        const val BAOJIN_TDS = 2
        const val BAOJIN_BJ = 3
        const val VERIFY_PH = 4
        val BAOJIN_REL = arrayOf("未设置", "正在设置", "设置成功", "设置失败")
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.high_h_valueBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "水温极高值")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 60)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 0)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, high_h_value.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_SW_H_HIGH_REQUEST)
            }
            R.id.high_valueBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "水温高值")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 60)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 0)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, high_value.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_SW_HIGH_REQUEST)
            }
            R.id.low_valueBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "水温低值")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 60)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 0)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, low_value.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_SW_LOW_REQUEST)
            }
            R.id.low_l_valueBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "水温极低值")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 60)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 0)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, low_l_value.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_SW_L_LOW_REQUEST)
            }
            R.id.high_ph_valueBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "PH高值")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 12)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 0)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_FLOAT_NUM, 2)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, high_ph_value.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_PH_HIGH_REQUEST)
            }
            R.id.low_ph_valueBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "PH低值")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 12)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 0)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_FLOAT_NUM, 2)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, low_ph_value.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_PH_LOW_REQUEST)
            }
            R.id.tdsSetBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "TDS基准值")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 1500)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 500)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_ISFLOAT, false)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, tdsSet.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_TDS_REQUEST)
            }
            R.id.firstBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "校验数据(1)")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 12)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 0)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_FLOAT_NUM, 2)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, high_ph_value.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_PH_FIRST_REQUEST)
            }
            R.id.secondBtn -> {
                val intent = Intent(this, CircleSelectorActivity::class.java)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_TITLE, "校验数据(2)")
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MAX, 12)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_MIN, 0)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_FLOAT_NUM, 2)
                intent.putExtra(KEYWORD_CIRCLE_SELECTOR_CURR, high_ph_value.text.toString().toFloat())
                startActivityForResult(intent, App_Keyword.KEYWORD_CIRCLE_SELECTOR_PH_SECOND_REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == App_Keyword.KEYWORD_RESULT_OK && data != null) {
            val value = data.getFloatExtra("data", 0f)
            when (requestCode) {
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_SW_H_HIGH_REQUEST -> {
                    high_h_value.text = value.toString()
                    isSWChange = true
                }
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_SW_HIGH_REQUEST -> {
                    high_value.text = value.toString()
                    isSWChange = true
                }
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_SW_LOW_REQUEST -> {
                    low_value.text = value.toString()
                    isSWChange = true
                }
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_SW_L_LOW_REQUEST -> {
                    low_l_value.text = value.toString()
                    isSWChange = true
                }
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_PH_HIGH_REQUEST -> {
                    high_ph_value.text = value.toString()
                    isPHChange = true
                }
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_PH_LOW_REQUEST -> {
                    low_ph_value.text = value.toString()
                    isPHChange = true
                }
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_TDS_REQUEST -> {
                    tdsSet.text = value.toInt().toString()
                    isTDSChange = true
                }
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_PH_FIRST_REQUEST -> {
                    firstData.text = value.toString()
                    isPHVerifyChange = true
                }
                App_Keyword.KEYWORD_CIRCLE_SELECTOR_PH_SECOND_REQUEST -> {
                    secondData.text = value.toString()
                    isPHVerifyChange = true
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private var deviceId = 0
    private var isSWChange = false
    private var isPHChange = false
    private var isTDSChange = false
    private var isPHVerifyChange = false
    private var isTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_set)
        initActionBar(this, "数据设置", rightBtn = "保存", rightClick = View.OnClickListener {
            if (!isSWChange && !isPHChange && !isTDSChange && !isPHVerifyChange) {
                toast("没有任何修改")
            } else {
                if (isSWChange) {
                    high_h_valueBtn.isEnabled = false
                    high_valueBtn.isEnabled = false
                    low_valueBtn.isEnabled = false
                    low_l_valueBtn.isEnabled = false
                    saveBaoJinSW()
                }
                if (isPHChange) {
                    high_ph_valueBtn.isEnabled = false
                    low_ph_valueBtn.isEnabled = false
                    saveBaoJinPH()
                }
                if (isTDSChange) {
                    tdsSetBtn.isEnabled = false
                    saveBaoJinTDS()
                }
                if (isPHVerifyChange) {
                    switchWarn.isEnabled = false
                    savePHVerify()
                }
            }
        })
        deviceId = intent.getIntExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, 0)
        getBaoJinDataState(BAOJIN_SW)
        getBaoJinDataState(BAOJIN_PH)
        getBaoJinDataState(BAOJIN_TDS)
        getBaoJinDataState(BAOJIN_BJ)
        getBaoJinDataState(VERIFY_PH)
        initViews()
    }

    private fun initViews() {
        high_h_valueBtn.setOnClickListener(this)
        high_valueBtn.setOnClickListener(this)
        low_l_valueBtn.setOnClickListener(this)
        low_valueBtn.setOnClickListener(this)
        high_ph_valueBtn.setOnClickListener(this)
        low_ph_valueBtn.setOnClickListener(this)
        tdsSetBtn.setOnClickListener(this)
        firstBtn.setOnClickListener(this)
        secondBtn.setOnClickListener(this)
        switchWarn.setOnTouchListener { _, _ ->
            isTouched = true
            false
        }
        switchWarn.setOnCheckedChangeListener(this@WaterMonitorDataSetActivity)
    }

    private fun savePHVerify() {
        val map = mapOf(Pair("id", deviceId.toString()),
                Pair("v_1", firstData.text.toString()),
                Pair("v_2", secondData.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getBaoJinDataState(VERIFY_PH)
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

        }, false).postRequest(this@WaterMonitorDataSetActivity, PHJZ.getInterface(Gson().toJson(map)), map)
    }

    private fun saveBaoJinSW() {
        val map = mapOf(Pair("id", deviceId.toString()),
                Pair("v_1", low_l_value.text.toString()),
                Pair("v_2", low_value.text.toString()),
                Pair("v_3", high_value.text.toString()),
                Pair("v_4", high_h_value.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getBaoJinDataState(BAOJIN_SW)
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

        }, false).postRequest(this@WaterMonitorDataSetActivity, SZ_BJWD.getInterface(Gson().toJson(map)), map)
    }

    private fun saveBaoJinPH() {
        val map = mapOf(Pair("id", deviceId.toString()),
                Pair("v_1", low_ph_value.text.toString()),
                Pair("v_2", high_ph_value.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getBaoJinDataState(BAOJIN_PH)
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

        }, false).postRequest(this@WaterMonitorDataSetActivity, SZ_BJPH.getInterface(Gson().toJson(map)), map)
    }

    private fun saveBaoJinTDS() {
        val map = mapOf(Pair("id", deviceId.toString()),
                Pair("v_1", tdsSet.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getBaoJinDataState(BAOJIN_TDS)
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

        }, false).postRequest(this@WaterMonitorDataSetActivity, SZ_BJTDS.getInterface(Gson().toJson(map)), map)
    }

    private fun saveBaoJinBJ() {
        val map = mapOf(Pair("id", deviceId.toString()),
                Pair("v_1", if (switchWarn.isChecked) "1" else "0"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getBaoJinDataState(BAOJIN_BJ)
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

        }, false).postRequest(this@WaterMonitorDataSetActivity, SZ_BJKG.getInterface(Gson().toJson(map)), map)
    }

    private fun getBaoJinDataState(type: Int) {
        val inter = when (type) {
            BAOJIN_SW -> BJWD_INFO
            BAOJIN_PH -> PH_INFO
            BAOJIN_TDS -> TDS_INFO
            BAOJIN_BJ -> KG_INFO
            VERIFY_PH -> PHJZ_INFO
            else -> BJWD_INFO
        }
        val map = mapOf(Pair("id", deviceId.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val baoJin_InfoRes = Gson().fromJson(result, BaoJin_InfoRes::class.java)
                val baoJinInfo = baoJin_InfoRes.retRes
                when (type) {
                    BAOJIN_SW -> {
                        swResult.text = BAOJIN_REL[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            isSWChange = false
                            high_h_valueBtn.isEnabled = true
                            high_valueBtn.isEnabled = true
                            low_valueBtn.isEnabled = true
                            low_l_valueBtn.isEnabled = true

                            high_h_value.text = baoJinInfo.v_4.toString()
                            high_value.text = baoJinInfo.v_3.toString()
                            low_value.text = baoJinInfo.v_2.toString()
                            low_l_value.text = baoJinInfo.v_1.toString()
                        }
                    }
                    BAOJIN_PH -> {
                        phResult.text = BAOJIN_REL[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            isPHChange = false
                            high_ph_valueBtn.isEnabled = true
                            low_ph_valueBtn.isEnabled = true

                            high_ph_value.text = baoJinInfo.v_2.toString()
                            low_ph_value.text = baoJinInfo.v_1.toString()
                        }
                    }
                    BAOJIN_TDS -> {
                        tdsResult.text = BAOJIN_REL[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            isTDSChange = false
                            tdsSetBtn.isEnabled = true
                            tdsSet.text = baoJinInfo.v_1.toString()
                        }
                    }
                    BAOJIN_BJ -> {
                        warnResult.text = BAOJIN_REL[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            switchWarn.isChecked = baoJinInfo.v_1 == 1f
                            switchWarn.isEnabled = true
                            isTouched = false
                        }
                    }
                    VERIFY_PH -> {
                        verifyResult.text = BAOJIN_REL[baoJinInfo.sz_status]
                        if (baoJinInfo.sz_status != 1) {
                            isPHVerifyChange = false
                            firstBtn.isEnabled = true
                            secondBtn.isEnabled = true
                            firstData.text = baoJinInfo.v_1.toString()
                            secondData.text = baoJinInfo.v_2.toString()
                        }
                    }
                }
                if (baoJinInfo.sz_status == 1 && !isFinishing) {
                    doAsync {
                        Thread.sleep(5000)
                        getBaoJinDataState(type)
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

        }, false).postRequest(this@WaterMonitorDataSetActivity, inter.getInterface(Gson().toJson(map)), map)
    }

}