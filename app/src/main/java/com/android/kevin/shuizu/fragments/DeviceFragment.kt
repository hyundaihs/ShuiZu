package com.android.kevin.shuizu.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_EDIT_GROUP
import com.android.kevin.shuizu.ui.*
import com.android.shuizu.myutillibrary.adapter.GridDivider
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import com.paradoxie.autoscrolltextview.VerticalTextview
import kotlinx.android.synthetic.main.fragment_device.*
import kotlinx.android.synthetic.main.layout_device_list_item.view.*
import kotlinx.android.synthetic.main.layout_yg_list_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class DeviceFragment : BaseFragment() {

    val ygInfoList = ArrayList<YGInfo>()
    val myDeviceList = ArrayList<MyDevice>()
    val warnLogList = ArrayList<WarnLog>()
    private val ygAdapter = GroupAdapter(ygInfoList)
    private val deviceAdapter = DeviceAdapter(myDeviceList)
    var isFirst = true
    var isFlag = false

    companion object {
        var checkedId = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        getDate()
        getYGList()
        getMyDeviceList(0)
    }

    override fun onResume() {
        super.onResume()
        if (!isFirst) {
            oftenCheck.isChecked = true
            getYGList()
            getMyDeviceList(0)
        } else {
            isFirst = false
        }
        isFlag = true
        getWarnLog()
        verticalTextview.startAutoScroll()
    }

    override fun onStop() {
        super.onStop()
        verticalTextview.stopAutoScroll()
        isFlag = false
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
                checkedId = ygAdapter.getCheck().id
                groupName.text = ygAdapter.getCheck().title
                getMyDeviceList(checkedId)
            }
        }

        oftenCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ygAdapter.cleanCheck()
                checkedId = 0
                groupName.text = "常用"
                getMyDeviceList(checkedId)
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
                if (position == parent.itemCount - 1 && checkedId != 0) {
                    val intent = Intent(activity, AddDeviceActivity::class.java)
                    intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, ygAdapter.getCheck().id)
                    intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_TITLE, ygAdapter.getCheck().title)
                    intent.putExtra(KEYWORD, KEYWORD_EDIT_GROUP)
                    startActivity(intent)
                } else {
                    val intent = if (myDeviceList[position].card_type == DeviceType.TR) {
                        Intent(activity, WaterMonitorActivity::class.java)
                    } else {
                        Intent(activity, WaterLevelDataSetActivity::class.java)
                    }
                    intent.putExtra(App_Keyword.KEYWORD_WATER_MONITOR_ID, myDeviceList[position].id)
                    startActivity(intent)

                }
            }
        }
        bindNewDevice.setOnClickListener {
            startActivity(Intent(activity, BindDeviceActivity::class.java))
        }
        groupSet.setOnClickListener {
            startActivity(Intent(activity, GroupActivity::class.java))
        }

        verticalTextview.setTextStillTime(3000)//设置停留时长间隔
        verticalTextview.setAnimTime(300)//设置进入和退出的时间间隔
        verticalTextview.setOnItemClickListener(VerticalTextview.OnItemClickListener { position ->
            activity!!.toast("点击了 : ")
        })
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
            if (position == itemCount - 1 && checkedId != 0) {
                holder.itemView.deviceTitle.visibility = View.GONE
                holder.itemView.deviceImage.setImageResource(R.mipmap.add_device)
            } else {
                holder.itemView.deviceTitle.text = device.title
                holder.itemView.deviceTitle.visibility = View.VISIBLE
                when (device.card_type) {
                    DeviceType.TR -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_monitor)
                    else -> holder.itemView.deviceImage.setImageResource(R.mipmap.water_pump)
                }
            }
            holder.itemView.setOnClickListener {
                myOnItemClickListener?.onItemClick(this@DeviceAdapter, holder.itemView, position)
            }
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getWarnLog() {
        doAsync {
            while (isFlag) {
                val map = if (warnLogList.size > 0) {
                    mapOf(Pair("times", warnLogList[warnLogList.lastIndex].create_time.toString()))
                } else {
                    mapOf(Pair("", ""))
                }
                MySimpleRequest().postRequest(getInterface(NEW_LOG), map, object : MySimpleRequest.RequestCallBackWithOutContext {
                    override fun onSuccess(result: String) {
                        val warnLogListRes = Gson().fromJson(result, WarnLogListRes::class.java)
                        warnLogList.clear()
                        warnLogList.addAll(warnLogListRes.retRes)
                        val titleList = ArrayList<String>()
                        if (warnLogList.size > 0) {
                            warnLogList.indices.mapTo(titleList) { warnLogList[it].title }
                        } else {
                            titleList.add("暂无预警消息")
                        }
                        uiThread {
                            verticalTextview.setTextList(titleList)
                        }
//                verticalTextview.setText(26, 5, Color.RED)//设置属性
                    }

                    override fun onError(error: String) {
                        uiThread {
                            activity?.toast(error)
                        }
                    }

                    override fun onLoginErr() {
                    }

                })
                Thread.sleep(10000)
            }
        }
    }

    private fun getDate() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val dateInfoRes = Gson().fromJson(result, DateInfoRes::class.java)
                deviceTime.text = dateInfoRes.retRes.dates
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

        }).postRequest(activity as Context, getInterface(INDEX_INFO), map)
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
                if (checkedId != 0) {
                    myDeviceList.add(MyDevice(0, 0, DeviceType.HT, "", 0, 0))
                }
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
