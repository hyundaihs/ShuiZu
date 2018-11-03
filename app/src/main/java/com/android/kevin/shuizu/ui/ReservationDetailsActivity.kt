package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.YYZJInfoRes
import com.android.kevin.shuizu.entities.YYZJ_INFO
import com.android.kevin.shuizu.entities.getImageUrl
import com.android.kevin.shuizu.entities.getInterface
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_reservation_details.*
import org.jetbrains.anko.toast

/**
 * ShuiZu
 * Created by 蔡雨峰 on 2018/10/12.
 */
class ReservationDetailsActivity : MyBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_details)
        initActionBar(this, "专家介绍")
        val id = intent.getIntExtra("id", 0)
        getYYZJDetails(id)
    }

    private fun getYYZJDetails(id: Int) {
        val map = mapOf(Pair("id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val yYZJInfoRes = Gson().fromJson(result, YYZJInfoRes::class.java)
                Picasso.with(context).load(yYZJInfoRes.retRes.file_url.getImageUrl()).into(detailsImage)
                detailsName.text = yYZJInfoRes.retRes.title
                detailsZhuanYe.text = yYZJInfoRes.retRes.zhuanye
                detailsTitle.text = yYZJInfoRes.retRes.biaoqian
                webDetails.loadData(yYZJInfoRes.retRes.app_contents, "text/html; charset=UTF-8", null)
                detailsCallzj.setOnClickListener {
                    val intent = Intent(context, CallZJActivity::class.java)
                    intent.putExtra("id", yYZJInfoRes.retRes.id)
                    startActivity(intent)
                }
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

        }, true).postRequest(this as Context, YYZJ_INFO.getInterface(Gson().toJson(map)), map)
    }
}
