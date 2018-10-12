package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.widget.SwipeRefreshAndLoadLayout
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_reservation.*
import kotlinx.android.synthetic.main.layout_reservation_list_item.view.*
import org.jetbrains.anko.toast

/**
 * ShuiZu
 * Created by 蔡雨峰 on 2018/10/12.
 */
class ReservationActivity : MyBaseActivity() {

    val yyData = ArrayList<YYZJInfo>()
    private val yyAdapter = ReservationAdapter(yyData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)
        initActionBar(this, "专家列表")
        initViews()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(this)
        reservationList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
//        actionRecycler.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        reservationList.itemAnimator = DefaultItemAnimator()
        reservationList.isNestedScrollingEnabled = false
        reservationSwipe.setOnRefreshListener(object : SwipeRefreshAndLoadLayout.OnRefreshListener {
            override fun onRefresh() {
                getYYZJ(reservationSwipe.currPage, true)
            }

            override fun onLoadMore(currPage: Int, totalPages: Int) {
                getYYZJ(currPage)
            }
        })
        reservationList.adapter = yyAdapter
        yyAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                val intent = Intent(view.context, ReservationDetailsActivity::class.java)
                intent.putExtra("id", yyData[position].id)
                startActivity(intent)
            }
        }
        getYYZJ(reservationSwipe.currPage, true)
    }

    private class ReservationAdapter(val data: ArrayList<YYZJInfo>) : MyBaseAdapter(R.layout.layout_reservation_list_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val yyzjInfo = data[position]
            Picasso.with(holder.itemView.context).load(yyzjInfo.file_url.getImageUrl())
                    .into(holder.itemView.reservationItemImage)
            holder.itemView.reservationItemName.text = yyzjInfo.title
            holder.itemView.reservationItemTitle.text = yyzjInfo.sub_title
        }

        override fun getItemCount(): Int = data.size
    }

    //获取养鱼专家列表
    private fun getYYZJ(page: Int, isRefresh: Boolean = false) {
        val map = mapOf(Pair("page", page.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val yYZJInfoListRes = Gson().fromJson(result, YYZJInfoListRes::class.java)
                reservationSwipe.totalPages = if (yYZJInfoListRes.retCounts % 20 == 0) yYZJInfoListRes.retCounts / 20 else yYZJInfoListRes.retCounts / 20 + 1
                if (isRefresh) {
                    yyData.clear()
                }
                yyData.addAll(yYZJInfoListRes.retRes)
                yyAdapter.notifyDataSetChanged()
                reservationSwipe.isRefreshing = false
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

        }, false).postRequest(this as Context, YYZJ.getInterface(), map)
    }
}