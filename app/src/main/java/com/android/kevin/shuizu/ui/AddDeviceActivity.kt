package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.DividerItemDecoration
import com.android.shuizu.myutillibrary.adapter.GridDivider
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.dp2px
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.acitivity_add_device.*
import kotlinx.android.synthetic.main.layout_add_device_list_item.view.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/25/025.
 */
class AddDeviceActivity : MyBaseActivity() {

    private var groupId = 0
    private var groupName = ""
    val myData = ArrayList<MyDevice>()
    val checkedData = ArrayList<MyDevice>()
    private val adapter = AddDeviceAdapter(myData, checkedData)
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_add_device)
        val key_word = intent.getStringExtra(App_Keyword.KEYWORD)
        isEdit = key_word == App_Keyword.KEYWORD_EDIT_GROUP
        if (isEdit) {
            groupId = intent.getIntExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, 0)
            groupName = intent.getStringExtra(App_Keyword.KEYWORD_WATER_MONITOR_TITLE)
        }
        initActionBar(this, "编辑鱼缸", rightBtn = "保存", rightClick = View.OnClickListener {
            if (isEdit) {
                if (group_Name.text.trim().toString() != groupName) {
                    alterGroup()
                }
                addDevice(groupId)
            } else {
                addGroup()
            }
        })
        initView()
    }

    private fun addGroup() {
        if (group_Name.text.trim().isEmpty()) {
            group_Name.error = "鱼缸名称不能为空"
            return
        }
        val map = mapOf(Pair("title", group_Name.text.toString().trim()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val ygInfoRes = Gson().fromJson(result, YGInfoRes::class.java)
                val ygInfo = ygInfoRes.retRes
                addDevice(ygInfo.id)
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

        }).postRequest(this, TJYG.getInterface(Gson().toJson(map)), map)
    }

    private fun alterGroup() {
        if (group_Name.text.trim().isEmpty()) {
            group_Name.error = "鱼缸名称不能为空"
            return
        }
        val map = mapOf(Pair("id", groupId.toString()), Pair("title", group_Name.text.toString().trim()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {

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

        }).postRequest(this, XGYG.getInterface(Gson().toJson(map)), map)
    }

    private fun addDevice(id: Int) {
        val addIds = ArrayList<Int>()
        for (i in checkedData.indices) {
            addIds.add(checkedData[i].id)
        }
        val map = PostDeviceIds(id, addIds)
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                    toast("编辑成功")
                    finish()
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

        }).postRequest(this, TJSB_DYG.getInterface(Gson().toJson(map)), map)
    }

//    private fun deleteDevice(ids: ArrayList<Int>) {
//        val map = PostDeviceIds(0, ids)
//        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
//            override fun onSuccess(context: Context, result: String) {
//                toast("编辑成功")
//                finish()
//            }
//
//            override fun onError(context: Context, error: String) {
//                context.toast(error)
//            }
//
//            override fun onLoginErr(context: Context) {
//                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
//                    val intent = Intent(context, LoginActivity::class.java)
//                    startActivity(intent)
//                })
//            }
//
//        }).postRequest(this, TJSB_DYG.getInterface(Gson().toJson(map)), map)
//    }

    private fun initView() {
        if (isEdit) {
            group_Name.setText(groupName)
        }
        val gridLayoutManager = GridLayoutManager(this, 2)
        deviceList.layoutManager = gridLayoutManager
        deviceList.addItemDecoration(GridDivider(this, dp2px(10f).toInt(), 2))
        deviceList.itemAnimator = DefaultItemAnimator()
        deviceList.adapter = adapter
        deviceList.isNestedScrollingEnabled = false
        adapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                val device = myData[position]
                if (checkedData.contains(device)) {
                    checkedData.remove(device)
                } else {
                    checkedData.add(device)
                }
                parent.notifyItemChanged(position)
            }
        }
        getMyDeviceList(0)
        if (isEdit) {
            getMyDeviceList(groupId)
        }
    }

    private class AddDeviceAdapter(val data: ArrayList<MyDevice>, val checkData: ArrayList<MyDevice>) : MyBaseAdapter(R.layout.layout_add_device_list_item) {


        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            val device = data[position]
            holder.itemView.deviceTitle.text = device.title
            when (device.card_type) {
                DeviceType.TR -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_monitor)
                else -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_pump)
            }
            holder.itemView.checked.setImageResource(if (checkData.contains(device)) R.mipmap.checked else R.mipmap.checkbox)
            holder.itemView.setOnClickListener {
                myOnItemClickListener?.onItemClick(this@AddDeviceAdapter, holder.itemView, position)
            }
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getMyDeviceList(id: Int) {
        val map = mapOf(Pair("acccardtype_id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val myDeviceListRes = Gson().fromJson(result, MyDeviceListRes::class.java)
                if (id != 0) {
                    checkedData.addAll(myDeviceListRes.retRes)
                    myData.addAll(0, myDeviceListRes.retRes)
                } else {
                    myData.addAll(myDeviceListRes.retRes)
                }
                adapter.notifyDataSetChanged()
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

        }).postRequest(this, YGSB.getInterface(Gson().toJson(map)), map)
    }
}