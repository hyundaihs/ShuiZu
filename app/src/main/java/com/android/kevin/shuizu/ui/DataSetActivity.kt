package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_CURR
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_FLOAT_NUM
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_ISFLOAT
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_MAX
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_MIN
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_TDS_REQUEST
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
class DataSetActivity : MyBaseActivity(), View.OnClickListener {
    companion object {
        const val SEND_DATA = "发送数据"
        const val VERIFING = "正在校验"
        const val VERIFY_SUCCESS = "校验通过"
        const val VERIFY_FAILED = "校验失败"
        const val BAOJIN_SW = 0
        const val BAOJIN_PH = 1
        const val BAOJIN_TDS = 2
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
                if (firstBtn.text == SEND_DATA) {
                    firstData.verifyData()
                }
            }
            R.id.secondBtn -> {
                if (secondBtn.text == SEND_DATA) {
                    secondData.verifyData()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == App_Keyword.KEYWORD_CIRCLE_SELECTOR_RESULT && data != null) {
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
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private var deviceId = 0
    private var isFirst = true
    private var isSWChange = false
    private var isPHChange = false
    private var isTDSChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_set)
        initActionBar(this, "数据设置", rightBtn = "保存", rightClick = View.OnClickListener {
            if (isSWChange) {
                saveBaoJinSW()
            }
            if (isPHChange) {
                saveBaoJinPH()
            }
            if (isTDSChange) {
                saveBaoJinTDS()
            }
        })
        deviceId = intent.getIntExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, 0)
        initViews()
        getVerifyDataState()
        getBaoJinDataState(BAOJIN_SW)
        getBaoJinDataState(BAOJIN_PH)
        getBaoJinDataState(BAOJIN_TDS)
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
        firstData.addTextChangedListener(MyWatcher(firstBtn))
        secondData.addTextChangedListener(MyWatcher(secondBtn))
    }

    inner class MyWatcher(val btn: TextView) : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            btn.setSendBtnText(SEND_DATA)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    }

    private fun TextView.setSendBtnText(str: String) {
        text = str
        setBackgroundResource(when (text) {
            SEND_DATA -> R.drawable.blue_tip
            VERIFING -> R.drawable.yellow_tip
            VERIFY_SUCCESS -> R.drawable.green_tip
            VERIFY_FAILED -> R.drawable.red_tip
            else -> R.drawable.blue_tip
        })
    }

    private fun EditText.verifyData() {
        if (text.trim().isEmpty()) {
            toast("请填写校验数据")
            return
        }
        val map = mapOf(Pair("id", deviceId.toString()), Pair("ph_value", text.toString()), Pair("num", if (isFirst) "1" else "2"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                if (isFirst) {
                    firstBtn.setSendBtnText(VERIFING)
                    getVerifyDataState()
                } else {
                    secondBtn.setSendBtnText(VERIFING)
                    getVerifyDataState()
                }
            }

            override fun onError(context: Context, error: String) {
                toast(error)
                if (isFirst) {
                    firstBtn.setSendBtnText(SEND_DATA)
                } else {
                    secondBtn.setSendBtnText(SEND_DATA)
                }
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(this@DataSetActivity, getInterface(PHJZ), map)
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

        }, false).postRequest(this@DataSetActivity, getInterface(SZ_BJWD), map)
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

        }, false).postRequest(this@DataSetActivity, getInterface(SZ_BJPH), map)
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

        }, false).postRequest(this@DataSetActivity, getInterface(SZ_BJTDS), map)
    }

    private fun getBaoJinDataState(type: Int) {
        val inter = when (type) {
            BAOJIN_SW -> BJWD_INFO
            BAOJIN_PH -> PH_INFO
            else -> TDS_INFO
        }
        val map = mapOf(Pair("id", deviceId.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val baoJin_InfoRes = Gson().fromJson(result, BaoJin_InfoRes::class.java)
                val baoJinInfo = baoJin_InfoRes.retRes
                when (type) {
                    BAOJIN_SW -> {
                        swResult.text = BAOJIN_REL[baoJinInfo.sz_status]
                        high_h_value.text = baoJinInfo.v_4.toString()
                        high_value.text = baoJinInfo.v_3.toString()
                        low_value.text = baoJinInfo.v_2.toString()
                        low_l_value.text = baoJinInfo.v_1.toString()
                    }
                    BAOJIN_PH -> {
                        phResult.text = BAOJIN_REL[baoJinInfo.sz_status]
                        high_ph_value.text = baoJinInfo.v_2.toString()
                        low_ph_value.text = baoJinInfo.v_1.toString()
                    }
                    else -> {
                        tdsResult.text = BAOJIN_REL[baoJinInfo.sz_status]
                        tdsSet.text = baoJinInfo.v_1.toString()
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

        }, false).postRequest(this@DataSetActivity, getInterface(inter), map)
    }

    private fun getVerifyDataState() {
        val map = mapOf(Pair("id", deviceId.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val phjz_InfoRes = Gson().fromJson(result, PHJZ_InfoRes::class.java)
                val phjz_Info = phjz_InfoRes.retRes
                verifyResult.text = phjz_Info.title
                when (phjz_Info.phjz_status) {
                    0 -> {//未校准
                        if (isFirst) {
                            firstBtn.setSendBtnText(SEND_DATA)
                        } else {
                            secondBtn.setSendBtnText(SEND_DATA)
                        }
                    }
                    1 -> {//正在校准
                        doAsync {
                            Thread.sleep(3000)
                            getVerifyDataState()
                        }
                    }
                    2 -> {//第一次校准成功
                        firstBtn.setSendBtnText(VERIFY_SUCCESS)
                        second_layout.visibility = View.VISIBLE
                        second_layout_line.visibility = View.VISIBLE
                        isFirst = false
                    }
                    3 -> {//第二次开始校准
                        doAsync {
                            Thread.sleep(3000)
                            getVerifyDataState()
                        }
                    }
                    4 -> {//校准成功
                        secondBtn.setSendBtnText(VERIFY_SUCCESS)
                        verifyResult.visibility = View.VISIBLE
                    }
                    5 -> {//校准失败
                        if (isFirst) {
                            firstBtn.setSendBtnText(VERIFY_FAILED)
                        } else {
                            secondBtn.setSendBtnText(VERIFY_FAILED)
                        }
                    }
                }
            }

            override fun onError(context: Context, error: String) {
                toast(error)
                if (isFirst) {
                    firstBtn.setSendBtnText(VERIFY_FAILED)
                } else {
                    secondBtn.setSendBtnText(VERIFY_FAILED)
                }
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(this@DataSetActivity, getInterface(PHJZ_INFO), map)
    }
}