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
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.widget.SwipeRefreshAndLoadLayout
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_fish_log.*
import kotlinx.android.synthetic.main.layout_fish_log_list_item.view.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/14/014.
 */
class FishLogListActivity : MyBaseActivity() {

    val fishLogData = ArrayList<FishLog>()
    private val fishLogAdapter = FishLogAdapter(fishLogData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fish_log)
        initActionBar(this, "养鱼日志", rightBtn = "写日志", rightClick = View.OnClickListener {
            startActivityForResult(Intent(it.context, CreateFishLogActivity::class.java), 10)
        })
        initViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getFishLogs(fishLogSwipe.currPage, true)
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(this)
        fishLogList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        fishLogList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        fishLogList.itemAnimator = DefaultItemAnimator()
        fishLogList.isNestedScrollingEnabled = false
        fishLogSwipe.setOnRefreshListener(object : SwipeRefreshAndLoadLayout.OnRefreshListener {
            override fun onRefresh() {
                getFishLogs(fishLogSwipe.currPage, true)
            }

            override fun onLoadMore(currPage: Int, totalPages: Int) {
                getFishLogs(currPage)
            }
        })
        fishLogList.adapter = fishLogAdapter
        fishLogAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                val intent = Intent(view.context, FishLogDetailsActivity::class.java)
                intent.putExtra("id", fishLogData[position].id)
                startActivity(intent)
            }
        }
        getFishLogs(fishLogSwipe.currPage, true)
    }

    private class FishLogAdapter(val data: ArrayList<FishLog>) : MyBaseAdapter(R.layout.layout_fish_log_list_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val fishLog = data[position]
//            Picasso.with(holder.itemView.context).load(fishLog.file_url.getImageUrl())
//                    .into(holder.itemView.fishLogItemImage)
            holder.itemView.fishLogItemName.text = fishLog.title
            val calendarUtil = CalendarUtil(fishLog.create_time, true)
            holder.itemView.fishLogItemTime.text = calendarUtil.format(CalendarUtil.YYYY_MM_DD_HH_MM)
            holder.itemView.fishLogItemTitle.text = fishLog.sub_title
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getFishLogs(page: Int, isRefresh: Boolean = false) {
        val map = mapOf(Pair("page", page.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val fishLogListRes = Gson().fromJson(result, FishLogListRes::class.java)
                fishLogSwipe.totalPages = if (fishLogListRes.retCounts % 20 == 0) fishLogListRes.retCounts / 20
                else fishLogListRes.retCounts / 20 + 1
                if (isRefresh) {
                    fishLogData.clear()
                }
                fishLogData.addAll(fishLogListRes.retRes)
                fishLogAdapter.notifyDataSetChanged()
                fishLogSwipe.isRefreshing = false
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

        }, false).postRequest(this as Context, YYRZ.getInterface(), map)
    }
}