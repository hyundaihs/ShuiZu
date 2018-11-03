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
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.widget.SwipeRefreshAndLoadLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_msg_log.*
import kotlinx.android.synthetic.main.layout_msg_log_list_item.view.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/23/023.
 */
class MsgLogListActivity : MyBaseActivity(), View.OnClickListener {

    val fishData = ArrayList<FishKnowledge>()
    private val fishAdapter = MsgLogAdapter(fishData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg_log)
        initViews()
    }

    private fun initViews() {
        msgLogBack.setOnClickListener {
            finish()
        }
        val layoutManager = LinearLayoutManager(this)
        msgLogList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        msgLogList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        msgLogList.itemAnimator = DefaultItemAnimator()
        msgLogList.isNestedScrollingEnabled = false
        msgLogSwipe.setOnRefreshListener(object : SwipeRefreshAndLoadLayout.OnRefreshListener {
            override fun onRefresh() {
                getFishKnowledgeList(msgLogSwipe.currPage, true)
            }

            override fun onLoadMore(currPage: Int, totalPages: Int) {
                getFishKnowledgeList(currPage)
            }
        })
        msgLogList.adapter = fishAdapter
        fishAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                val intent = Intent(view.context, MsgLogDetailsActivity::class.java)
                intent.putExtra("id", fishData[position].id)
                startActivity(intent)
            }
        }
        getFishKnowledgeList(msgLogSwipe.currPage, true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.msgLogSearchBtn -> {
                getFishKnowledgeList(msgLogSwipe.currPage, true)
            }
        }
    }

    private class MsgLogAdapter(val data: ArrayList<FishKnowledge>) : MyBaseAdapter(R.layout.layout_msg_log_list_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val msgLog = data[position]
            holder.itemView.msgLogItemTitle.text = msgLog.title
            holder.itemView.msgLogItemTime.text = CalendarUtil(msgLog.create_time, true).format(CalendarUtil.YYYY_MM_DD_HH_MM)
            holder.itemView.msgLogItemSubtitle.text = msgLog.sub_title
            holder.itemView.msgLogItemTip.visibility = if (msgLog.is_read == 0) View.VISIBLE else View.INVISIBLE
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getFishKnowledgeList(page: Int, isRefresh: Boolean = false) {
        val title = if (msgLogSearchText.text.isEmpty()) "" else msgLogSearchText.text.toString()
        val map = mapOf(Pair("page", page.toString()), Pair("title", title))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val msgLogListRes = Gson().fromJson(result, FishKnowledgeListRes::class.java)
                msgLogSwipe.totalPages = if (msgLogListRes.retCounts % 20 == 0)
                    msgLogListRes.retCounts / 20 else msgLogListRes.retCounts / 20 + 1
                if (isRefresh) {
                    fishData.clear()
                }
                fishData.addAll(msgLogListRes.retRes)
                fishAdapter.notifyDataSetChanged()
                msgLogSwipe.isRefreshing = false
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

        }, false).postRequest(this as Context, NEWS.getInterface(Gson().toJson(map)), map)
    }
}