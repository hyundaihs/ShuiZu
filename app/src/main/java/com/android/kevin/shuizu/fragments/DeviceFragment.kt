package com.android.kevin.shuizu.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.ui.AddDeviceActivity
import com.android.kevin.shuizu.ui.BindDeviceActivity
import com.android.kevin.shuizu.ui.LoginActivity
import com.android.kevin.shuizu.ui.WaterMonitorActivity
import com.android.shuizu.myutillibrary.adapter.GridDivider
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.toast
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_device.*
import kotlinx.android.synthetic.main.layout_device_list_item.view.*
import kotlinx.android.synthetic.main.layout_yg_list_item.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class DeviceFragment : BaseFragment() {

    val ygInfoList = ArrayList<YGInfo>()
    private val ygAdapter = GroupAdapter(ygInfoList)
    val myDeviceList = ArrayList<MyDevice>()
    private val deviceAdapter = DeviceAdapter(myDeviceList)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        getYGList()
        getMyDeviceList(0)
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(activity)
        deviceGroup.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        deviceGroup.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        deviceGroup.itemAnimator = DefaultItemAnimator()
        deviceGroup.adapter = ygAdapter
        deviceGroup.isNestedScrollingEnabled = false
        ygAdapter.onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            oftenCheck.isChecked = false
            if (isChecked) {
                getMyDeviceList(ygAdapter.getCheck().id)
            }
        }

        oftenCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ygAdapter.cleanCheck()
                getMyDeviceList(0)
            }
        }

        val gridLayoutManager = GridLayoutManager(activity, 2)
        deviceDevices.layoutManager = gridLayoutManager
        deviceDevices.addItemDecoration(GridDivider(activity, 10, this.resources.getColor(R.color.white)))
        deviceDevices.itemAnimator = DefaultItemAnimator()
        deviceDevices.adapter = deviceAdapter
        deviceDevices.isNestedScrollingEnabled = false
        deviceAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                if (position == parent.itemCount - 1) {
                    val intent = Intent(activity, AddDeviceActivity::class.java)
                    intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, myDeviceList[position].id)
                    intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_TITLE, myDeviceList[position].title)
                    startActivity(intent)
                } else {
                    val intent = Intent(activity, WaterMonitorActivity::class.java)
                    intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, myDeviceList[position].id)
                    startActivity(intent)
                }
            }
        }
        bindNewDevice.setOnClickListener {
            startActivity(Intent(activity, BindDeviceActivity::class.java))
        }
    }

    private class GroupAdapter(val data: ArrayList<YGInfo>) : MyBaseAdapter(R.layout.layout_yg_list_item) {

        var checkIndex = -1
        var checkYGInfo: YGInfo? = null
        var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null

        fun cleanCheck() {
            val temp = checkIndex
            checkIndex = -1
            notifyItemChanged(temp)
        }

        fun getCheck(): YGInfo {
            return data[checkIndex]
        }

        fun isCheck(): Boolean {
            return checkIndex >= 0
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val ygInfo = data[position]
            holder.itemView.ygListItem.text = ygInfo.title
            holder.itemView.ygListItem.isChecked = checkIndex == position
            holder.itemView.ygListItem.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    val temp = checkIndex
                    checkIndex = position
                    checkYGInfo = ygInfo
                    notifyItemChanged(temp, checkIndex)
                    onCheckedChangeListener?.onCheckedChanged(view, isChecked)
                }
            })
        }

        override fun getItemCount(): Int = data.size
    }

    private class DeviceAdapter(val data: ArrayList<MyDevice>) : MyBaseAdapter(R.layout.layout_device_list_item) {


        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            val device = data[position]
            if (position == itemCount - 1) {
                holder.itemView.deviceTitle.visibility = View.GONE
                holder.itemView.deviceImage.setImageResource(R.mipmap.add_device)
            } else {
                holder.itemView.deviceTitle.text = device.title
                holder.itemView.deviceTitle.visibility = View.VISIBLE
                when (device.card_type) {
                    DeviceType.TR -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_monitor)
                    DeviceType.HT -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_pump)
                    DeviceType.WP -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_pump)
                    DeviceType.PF -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_pump)
                }
            }
            holder.itemView.setOnClickListener {
                myOnItemClickListener?.onItemClick(this@DeviceAdapter, holder.itemView, position)
            }
        }

        override fun getItemCount(): Int = data.size
    }


    private fun getYGList() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val ygInfoListRes = Gson().fromJson(result, YGInfoListRes::class.java)
                ygInfoList.clear()
                ygInfoList.addAll(ygInfoListRes.retRes)
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

        }).postRequest(activity as Context, getInterface(YG_LISTS), map)
    }

    private fun getMyDeviceList(id: Int) {
        val map = mapOf(Pair("acccardtype_id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val myDeviceListRes = Gson().fromJson(result, MyDeviceListRes::class.java)
                myDeviceList.clear()
                myDeviceList.addAll(myDeviceListRes.retRes)
                myDeviceList.add(MyDevice(0, 0, DeviceType.HT, "", 0, 0))
                deviceAdapter.notifyDataSetChanged()
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

        }).postRequest(activity as Context, getInterface(YGSB), map)
    }
}
