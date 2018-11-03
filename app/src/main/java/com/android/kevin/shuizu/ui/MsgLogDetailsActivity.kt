package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.FishKnowledgeRes
import com.android.kevin.shuizu.entities.NEWS_INFO
import com.android.kevin.shuizu.entities.YLZS_INFO
import com.android.kevin.shuizu.entities.getInterface
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_msg_log_details.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/23/023.
 */
class MsgLogDetailsActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg_log_details)
        initActionBar(this, "消息详情")
        getMsgLogDetails(intent.getIntExtra("id", 0))
    }

    private fun getMsgLogDetails(id: Int) {
        val map = mapOf(Pair("id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val fishKnowledgeRes = Gson().fromJson(result, FishKnowledgeRes::class.java)
                msgLogDetailsTitle.text = fishKnowledgeRes.retRes.title
                msgLogDetailsTime.text = CalendarUtil(fishKnowledgeRes.retRes.create_time, true).format(CalendarUtil.YYYY_MM_DD_HH_MM)
                msgLogDetailsContents.loadData(fishKnowledgeRes.retRes.app_contents, "text/html; charset=UTF-8", null)
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

        }, true).postRequest(this as Context, NEWS_INFO.getInterface(Gson().toJson(map)), map)
    }
}