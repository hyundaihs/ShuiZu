package com.android.kevin.shuizu.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.ui.DateChooseActivity
import com.android.kevin.shuizu.ui.LoginActivity
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.widget.SwipeRefreshAndLoadLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_date_choose.*
import kotlinx.android.synthetic.main.fragment_action_log.*
import kotlinx.android.synthetic.main.layout_warn_log_list_item.view.*
import org.jetbrains.anko.toast

/**
 * Created by kevin on 2018/9/2.
 */
class WarnLogFragment : BaseFragment(){
    val myData = ArrayList<WarnLog>()
    private val mAdapter = MyAdapter(myData)
    var startDate = ""
    var endDate = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_action_log, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }


    private fun initViews() {
        val layoutManager = LinearLayoutManager(activity)
        actionRecycler.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
//        actionRecycler.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        actionRecycler.itemAnimator = DefaultItemAnimator()
        actionRecycler.isNestedScrollingEnabled = false
        actionSwipe.setOnRefreshListener(object : SwipeRefreshAndLoadLayout.OnRefreshListener {
            override fun onRefresh() {
                getLog(actionSwipe.currPage, true)
            }

            override fun onLoadMore(currPage: Int, totalPages: Int) {
                getLog(currPage)
            }
        })
        actionRecycler.adapter = mAdapter
        getLog(actionSwipe.currPage, true)
        addOrTime.setOnClickListener {
            val intent = Intent(activity, DateChooseActivity::class.java)
            intent.putExtra(App_Keyword.KEYWORD_START_DATE, startDate)
            intent.putExtra(App_Keyword.KEYWORD_END_DATE, endDate)
            startActivityForResult(intent, App_Keyword.KEYWORD_TIME_DATE_CHOOSER_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == App_Keyword.KEYWORD_TIME_DATE_CHOOSER_REQUEST && resultCode == App_Keyword.KEYWORD_RESULT_OK && data != null) {
            startDate = data.getStringExtra(App_Keyword.KEYWORD_START_DATE)
            endDate = data.getStringExtra(App_Keyword.KEYWORD_END_DATE)
            actionSwipe.currPage = 1
            getLog(actionSwipe.currPage, true)
        }
    }

    private class MyAdapter(val data: ArrayList<WarnLog>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


        override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
            val warnLog = data[position]
            val cal = CalendarUtil(warnLog.create_time, true)
            holder.itemView.warnLogTime.text = cal.format(CalendarUtil.YYYY_MM_DD_HH_MM)
            holder.itemView.warnLogContent.text = warnLog.title
            holder.itemView.setOnClickListener {
                myOnItemClickListener?.onItemClick(this@MyAdapter, holder.itemView, position)
            }
        }

        override fun getItemCount(): Int = data.size

        var myOnItemClickListener: MyOnItemClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_warn_log_list_item, parent, false))
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        interface MyOnItemClickListener {
            fun onItemClick(parent: MyAdapter, view: View, position: Int)
        }
    }

    private fun getLog(page: Int, isRefresh: Boolean = false) {
        val map = mapOf(Pair("page", page.toString()),
                Pair("page_size", "20"),
                Pair("type_id", "1"),
                Pair("start_date", startDate.replace("_","")),
                Pair("end_date", endDate.replace("_","")))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val warnLogListRes = Gson().fromJson(result, WarnLogListRes::class.java)
                actionSwipe.totalPages = if (warnLogListRes.retCounts % 20 == 0) warnLogListRes.retCounts / 20 else warnLogListRes.retCounts / 20 + 1
                if (isRefresh) {
                    myData.clear()
                }
                myData.addAll(warnLogListRes.retRes)
                mAdapter.notifyDataSetChanged()
                actionSwipe.isRefreshing = false
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

        }, false).postRequest(activity as Context, BJ_LOG.getInterface(), map)
    }
}