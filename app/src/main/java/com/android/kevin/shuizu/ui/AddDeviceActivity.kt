package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.GridDivider
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.toast
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.acitivity_add_device.*
import kotlinx.android.synthetic.main.layout_add_device_list_item.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/25/025.
 */
class AddDeviceActivity : MyBaseActivity() {

    var deviceId = 0
    var deviceTitle = ""
    val myData = ArrayList<MyDevice>()
    val checkedData = ArrayList<MyDevice>()
    val deleteData = ArrayList<MyDevice>()
    private val adapter = AddDeviceAdapter(myData, checkedData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_add_device)
        deviceId = intent.getIntExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, 0)
        deviceTitle = intent.getStringExtra(App_Keyword.KEYWORD_WATER_MONITOR_TITLE)
        initActionBar(this, "编辑鱼缸", rightBtn = "保存", rightClick = View.OnClickListener {

        })
        initView()
    }

    private fun initView() {
        groupName.setText(deviceTitle)
        val gridLayoutManager = GridLayoutManager(this, 2)
        deviceList.layoutManager = gridLayoutManager
        deviceList.addItemDecoration(GridDivider(this, 10, this.resources.getColor(R.color.white)))
        deviceList.itemAnimator = DefaultItemAnimator()
        deviceList.adapter = adapter
        deviceList.isNestedScrollingEnabled = false
        adapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                val device = myData[position]
                if (checkedData.contains(device)) {
                    checkedData.remove(device)
                    deleteData.add(device)
                } else {
                    deleteData.remove(device)
                    checkedData.add(device)
                }
                parent.notifyItemChanged(position)
            }
        }
        getMyDeviceList(0)
        getMyDeviceList(deviceId)
        D("deviceId = $deviceId")
    }

    private class AddDeviceAdapter(val data: ArrayList<MyDevice>, val checkData: ArrayList<MyDevice>) : MyBaseAdapter(R.layout.layout_add_device_list_item) {


        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            val device = data[position]
            holder.itemView.deviceTitle.text = device.title
            when (device.card_type) {
                DeviceType.TR -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_monitor)
                DeviceType.HT -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_pump)
                DeviceType.WP -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_pump)
                DeviceType.PF -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_pump)
            }
            holder.itemView.checked.isChecked = checkData.contains(device)
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
                    myData.addAll(0, checkedData)
                } else {
                    myData.addAll(myDeviceListRes.retRes)
                }
                adapter.notifyDataSetChanged()
                D("myData = $myData")
                D("checkedData = $checkedData")
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

        }).postRequest(this, getInterface(YGSB), map)
    }
}