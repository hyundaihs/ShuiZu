package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.CZSM
import com.android.kevin.shuizu.entities.CZSM_INFO
import com.android.kevin.shuizu.entities.InstructionsRes
import com.android.kevin.shuizu.entities.getInterface
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_instructions_details.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/25/025.
 */
class InstructionsDetailsActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions_details)
        initActionBar(this,"使用详情")
        getInstructions(intent.getIntExtra("id", 0))
    }

    private fun getInstructions(id: Int) {
        val map = mapOf(Pair("id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val instructionsRes = Gson().fromJson(result, InstructionsRes::class.java)
                instructionsDetailsTitle.text = instructionsRes.retRes.title
                instructionsDetailsWeb.loadData(instructionsRes.retRes.app_contents, "text/html; charset=UTF-8", null)
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

        }, false).postRequest(this as Context, CZSM_INFO.getInterface(), map)
    }

}