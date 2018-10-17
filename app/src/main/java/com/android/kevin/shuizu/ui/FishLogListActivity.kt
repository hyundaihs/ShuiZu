package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.View
import android.view.ViewGroup
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
import com.yanzhenjie.recyclerview.swipe.SwipeMenu
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
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
        // 设置监听器。
        fishLogList.setSwipeMenuCreator(object : SwipeMenuCreator {
            override fun onCreateMenu(swipeLeftMenu: SwipeMenu, swipeRightMenu: SwipeMenu, viewType: Int) {
                val deleteItem = SwipeMenuItem(this@FishLogListActivity)
                        .setBackground(R.color.colorAccent)
                        .setText("删除") // 文字。
                        .setTextColor(Color.WHITE) // 文字颜色。
                        .setTextSize(16) // 文字大小。
                        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                swipeRightMenu.addMenuItem(deleteItem) // 在Item左侧添加一个菜单。

            }
        })
        // 菜单点击监听。
        fishLogList.setSwipeMenuItemClickListener(mMenuItemClickListener)
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

    var mMenuItemClickListener: SwipeMenuItemClickListener = SwipeMenuItemClickListener { menuBridge ->
        // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
        menuBridge.closeMenu()

        val direction = menuBridge.direction // 左侧还是右侧菜单。
        val adapterPosition = menuBridge.adapterPosition // RecyclerView的Item的position。
        val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。
        deleteFishLog(adapterPosition)
    }

    private class FishLogAdapter(val data: ArrayList<FishLog>) : MyBaseAdapter(R.layout.layout_fish_log_list_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val fishLog = data[position]
            Picasso.with(holder.itemView.context).load(fishLog.file_url.getImageUrl()).resize(200,200)
                    .into(holder.itemView.fishLogItemImage)
            holder.itemView.fishLogItemName.text = fishLog.title
            val calendarUtil = CalendarUtil(fishLog.create_time, true)
            holder.itemView.fishLogItemTime.text = calendarUtil.format(CalendarUtil.YYYY_MM_DD_HH_MM)
            holder.itemView.fishLogItemTitle.text = fishLog.sub_title
        }

        override fun getItemCount(): Int = data.size
    }

    private fun deleteFishLog(position: Int) {
        val map = mapOf(Pair("id", fishLogData[position].id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                fishLogData.removeAt(position)
                fishLogAdapter.notifyDataSetChanged()
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

        }, false).postRequest(this as Context, DEL_YYRZ.getInterface(), map)
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