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
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.widget.SwipeRefreshAndLoadLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_memoset.*
import kotlinx.android.synthetic.main.memoset_list_item.view.*
import org.jetbrains.anko.toast
import java.util.ArrayList

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/21/021.
 */
class MemoSetListActivity : MyBaseActivity() {

    val myData = ArrayList<MemoSetInfo>()
    private val mAdapter = MyAdapter(myData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memoset)
        initActionBar(this, "备忘设置", rightBtn = "新增", rightClick = View.OnClickListener {
            startActivity(Intent(this, MemoSetAddActivity::class.java))
        })
        initViews()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(this)
        memoSetRecycler.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
//        memoSetRecycler.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        memoSetRecycler.itemAnimator = DefaultItemAnimator()
        memoSetRecycler.isNestedScrollingEnabled = false
        memoSetSwipe.setOnRefreshListener(object : SwipeRefreshAndLoadLayout.OnRefreshListener {
            override fun onRefresh() {
                getMemoSetList(memoSetSwipe.currPage, true)
            }

            override fun onLoadMore(currPage: Int, totalPages: Int) {
                getMemoSetList(currPage)
            }
        })
        memoSetRecycler.adapter = mAdapter
        mAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                val intent = Intent(view.context, MemoSetAddActivity::class.java)
                intent.putExtra("id", myData[position].id)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getMemoSetList(memoSetSwipe.currPage, true)
    }

    private inner class MyAdapter(val data: ArrayList<MemoSetInfo>) : MyBaseAdapter(R.layout.memoset_list_item) {
        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val memoSetInfo = data[position]
            val calendarUtil = CalendarUtil(memoSetInfo.tx_time, true)
            holder.itemView.memoSetTime.text = calendarUtil.format(CalendarUtil.STANDARD)
            holder.itemView.memoSetMessage.text = memoSetInfo.title
            holder.itemView.memoSetDel.setOnClickListener {
                deleteMemoSet(position)
            }
        }
    }

    private fun getMemoSetList(page: Int, isRefresh: Boolean = false) {
        val map = mapOf(Pair("page", page.toString()),
                Pair("page_size", "20")
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val memoSetInfoListRes = Gson().fromJson(result, MemoSetInfoListRes::class.java)
                memoSetSwipe.totalPages = if (memoSetInfoListRes.retCounts % 20 == 0) memoSetInfoListRes.retCounts / 20 else memoSetInfoListRes.retCounts / 20 + 1
                if (isRefresh) {
                    myData.clear()
                }
                myData.addAll(memoSetInfoListRes.retRes)
                mAdapter.notifyDataSetChanged()
                memoSetSwipe.isRefreshing = false
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

        }, false).postRequest(this, BWXX.getInterface(), map)
    }

    private fun deleteMemoSet(position: Int) {
        val map = mapOf(Pair("id", myData[position].id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                myData.removeAt(position)
                mAdapter.notifyDataSetChanged()
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

        }, true).postRequest(this, BWXX_DEL.getInterface(), map)
    }
}