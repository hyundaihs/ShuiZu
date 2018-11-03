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
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.fragments.DeviceFragment
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import org.jetbrains.anko.toast
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import com.yanzhenjie.recyclerview.swipe.*
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.layout_group_list_item.view.*


/**
 * Created by kevin on 2018/8/30.
 */
class GroupActivity : MyBaseActivity() {

    val ygData = ArrayList<YGInfo>()
    private val ygAdapter = GroupAdapter(ygData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initActionBar(this, "鱼缸管理", rightBtn = "新增", rightClick = View.OnClickListener {
            val intent = Intent(this@GroupActivity, AddDeviceActivity::class.java)
            intent.putExtra(App_Keyword.KEYWORD, App_Keyword.KEYWORD_ADD_GROUP)
            startActivity(intent)
        })
        initViews()
    }

    override fun onResume() {
        super.onResume()
        getYGList()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(this)
        groupList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        groupList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        groupList.itemAnimator = DefaultItemAnimator()
        groupList.isNestedScrollingEnabled = false
        groupList.setSwipeItemClickListener(SwipeItemClickListener { view, position ->
            val intent = Intent(this@GroupActivity, AddDeviceActivity::class.java)
            intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, ygData[position].id)
            intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_TITLE, ygData[position].title)
            intent.putExtra(App_Keyword.KEYWORD, App_Keyword.KEYWORD_EDIT_GROUP)
            startActivity(intent)
        })
        // 设置监听器。
        groupList.setSwipeMenuCreator(object : SwipeMenuCreator {
            override fun onCreateMenu(swipeLeftMenu: SwipeMenu, swipeRightMenu: SwipeMenu, viewType: Int) {
                val deleteItem = SwipeMenuItem(this@GroupActivity)
                        .setBackground(R.color.colorAccent)
                        .setText("删除") // 文字。
                        .setTextColor(Color.WHITE) // 文字颜色。
                        .setTextSize(16) // 文字大小。
                        .setWidth(MATCH_PARENT)
                        .setHeight(MATCH_PARENT)
                swipeRightMenu.addMenuItem(deleteItem) // 在Item左侧添加一个菜单。

            }
        })
        // 菜单点击监听。
        groupList.setSwipeMenuItemClickListener(mMenuItemClickListener)
        groupList.adapter = ygAdapter
    }

    private fun getYGList() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val ygInfoListRes = Gson().fromJson(result, YGInfoListRes::class.java)
                ygData.clear()
                ygData.addAll(ygInfoListRes.retRes)
                ygAdapter.notifyDataSetChanged()
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

        }).postRequest(this, YG_LISTS.getInterface(Gson().toJson(map)), map)
    }

    var mMenuItemClickListener: SwipeMenuItemClickListener = SwipeMenuItemClickListener { menuBridge ->
        // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
        menuBridge.closeMenu()

        val direction = menuBridge.direction // 左侧还是右侧菜单。
        val adapterPosition = menuBridge.adapterPosition // RecyclerView的Item的position。
        val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。
        deleteGroup(adapterPosition)
    }

    private fun deleteGroup(pos: Int) {
        val map = mapOf(Pair("id", ygData[pos].id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                ygData.removeAt(pos)
                ygAdapter.notifyItemChanged(pos)
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

        }).postRequest(this, SCYG.getInterface(Gson().toJson(map)), map)
    }

    private class GroupAdapter(val data: ArrayList<YGInfo>) : MyBaseAdapter(R.layout.layout_group_list_item) {


        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val ygInfo = data[position]
            holder.itemView.groupName.text = ygInfo.title
            holder.itemView.deviceNum.text = "${ygInfo.counts}个设备"
        }

        override fun getItemCount(): Int = data.size
    }


}