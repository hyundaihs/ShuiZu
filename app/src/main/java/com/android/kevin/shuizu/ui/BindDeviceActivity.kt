package com.android.kevin.shuizu.ui

import android.app.AlertDialog
import android.content.*
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.WIFI_STATE_ENABLED
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.BDSB
import com.android.kevin.shuizu.entities.getInterface
import com.android.kevin.shuizu.ui.BindDeviceActivity.MyAdapter.MyOnItemClickListener
import com.android.kevin.shuizu.utils.CharUtil
import com.android.kevin.shuizu.utils.SocketUtil
import com.android.kevin.shuizu.utils.SocketUtil.OnMsgComing
import com.android.shuizu.myutillibrary.*
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.utils.charToHexStr
import com.android.shuizu.myutillibrary.utils.makeChecksum
import kotlinx.android.synthetic.main.activity_bind_device.*
import kotlinx.android.synthetic.main.layout_wifi_list_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/25/025.
 */
class BindDeviceActivity : MyBaseActivity() {

    companion object {
        val WIFI_SSID = "JIASONG"
    }

    enum class InfoType {
        SUCCESS, FAILED, PROGRESS
    }

    val mWifiMangaer: WifiManager by lazy {
        application.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    var socketUtil: SocketUtil? = null
    private var deviceIDS = ""
    val options1Items = arrayListOf<String>("水质监测器", "加热棒", "水泵", "断电报警器", "水位报警")
    val scanResults = ArrayList<ScanResult>()
    private val mAdapter = MyAdapter(scanResults)
    var checkDevice: String = "WL"
    var state = ConnectState.SEARCHING

    enum class ConnectState {
        SEARCHING, SEARCHED, CONNECTING, CONNECTED, CREATE_SOCKET, INIT_SOCKET, SENDING_CLOSE
    }

    private val receiver = MyBroadCastReceiver()
    private var isCallEnable = false
    private var isCallResult = false
    private var isCallStateChanged = false

    inner class MyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                    if (isCallEnable) {
                        return
                    }
                    isCallEnable = true
                    val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
                    if (wifiState == WIFI_STATE_ENABLED && state == ConnectState.SEARCHING) {
                        mWifiMangaer.startScan()
                    }
                    isCallEnable = false
                }
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                    if (isCallResult) {
                        return
                    }
                    isCallResult = true
                    scanResults.clear()
                    scanResults.addAll(mWifiMangaer.scanResults)
                    mAdapter.notifyDataSetChanged()
                    val rel = isFind(scanResults)
                    if (rel >= 0) {
                        //找到设备热点,并且状态为搜索中,置为已搜索到
                        if (state == ConnectState.SEARCHING) {
                            state = ConnectState.SEARCHED
                            D("找到热点")
                            connectWifi(scanResults[rel].SSID, 1)
                        }
                    } else {
                        //如果没有设备热点,并且状态不在搜索中,置为搜索中
                        E("失去热点")
                        if (state != ConnectState.SEARCHING && state != ConnectState.SENDING_CLOSE) {
                            restartProcess(1)
                        }
                    }
                    isCallResult = false
                }
                WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                    if (isCallStateChanged || state != ConnectState.CONNECTED) {
                        return
                    }
                    isCallStateChanged = true
                    //拿到NetworkInfo
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                    when (networkInfo.state) {
                        NetworkInfo.State.CONNECTED -> {
                            E("CONNECTED")
                            val wifiInfo = intent.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO)
                            if (wifiInfo != null && wifiInfo.ssid.replace("\"", "") == WIFI_SSID) {
                                doAsync {
                                    Thread.sleep(15000)
                                    uiThread {
                                        connectDevice()
                                    }
                                }
                            } else {
                                E("CONNECTED = ${wifiInfo.ssid.replace("\"", "")}   state = $state")
                                restartProcess(2)
                            }
                        }
                        else -> {
                            E("else")
                        }
                    }
                    isCallStateChanged = false
                }
                WifiManager.SUPPLICANT_STATE_CHANGED_ACTION -> {
                }
            }
        }
    }

    private fun restartProcess(type: Int) {
        setLoadText("正在搜索设备")
        D("restart $type")
        state = ConnectState.SEARCHING
    }

    private class MyAdapter(val data: List<ScanResult>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


        override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
            val scanResult = data[position]
            holder.itemView.wifiName.text = scanResult.SSID
            holder.itemView.setOnClickListener {
                myOnItemClickListener?.onItemClick(this@MyAdapter, holder.itemView, position)
            }
        }

        override fun getItemCount(): Int = data.size

        var myOnItemClickListener: MyOnItemClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_wifi_list_item, parent, false))
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        interface MyOnItemClickListener {
            fun onItemClick(parent: MyAdapter, view: View, position: Int)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_device)
        initActionBar(this, "添加新设备")

        val layoutManager = LinearLayoutManager(this)
        wifiList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        wifiList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        wifiList.itemAnimator = DefaultItemAnimator()
        wifiList.isNestedScrollingEnabled = false
        wifiList.adapter = mAdapter
        mAdapter.myOnItemClickListener = object : MyOnItemClickListener {
            override fun onItemClick(parent: MyAdapter, view: View, position: Int) {
                //点击后弹出窗口填写密码,并发送wifi信息
                val builder = AlertDialog.Builder(view.context).setTitle("请输入${scanResults[position].SSID}的密码")
                val editText = EditText(view.context)
                builder.setView(editText)
                builder.setPositiveButton("确定") { _, _ ->
                    setLoadText("正在发送Wifi信息")
                    sendData(createMsg(scanResults[position].SSID, editText.text.toString()))
                }
                builder.setNegativeButton("取消", null)
                builder.create()
                builder.show()
            }
        }
        searchWifi()
    }

    private fun setLoadText(str: String) {
        bind_progressBar.visibility = View.VISIBLE
        bind_info.visibility = View.VISIBLE
        bind_info.text = str
    }

    private fun hideLoading() {
        bind_progressBar.visibility = View.GONE
        bind_info.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun sendData(byteArray: ByteArray) {
        if (socketUtil!!.isOpened) {
            socketUtil!!.sendMsg(byteArray)
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)//是否可用
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)//搜索结果
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)//连接状态改变
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)//连接中各种状态
        registerReceiver(receiver, intentFilter)

    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private fun connectDevice() {
        if (state != ConnectState.CONNECTED) {
            return
        }
        setLoadText("正在与设备建立连接")
        state = ConnectState.CREATE_SOCKET
        if (null == socketUtil) {
            socketUtil = SocketUtil("192.168.4.1", 8899, object : OnMsgComing {
                override fun onInitSocket(isSuccess: Boolean) {
                    if (isSuccess) {
                        toast("设备连接成功,请选择Wifi连接")
                        state = ConnectState.INIT_SOCKET
                        wifiList.visibility = View.VISIBLE
                        hideLoading()
                    } else {
                        toast("设备建立连接失败")
                        restartProcess(3)
                    }
                }

                override fun onMsgCome(byteArray: ByteArray) {
                    if (state != ConnectState.INIT_SOCKET) {
                        return
                    }
                    val temp = String(byteArray)
                    deviceIDS = temp.substring(0, 3) + temp.substring(4, temp.lastIndex)
                    setLoadText("发送关闭热点信息")
                    doAsync {
                        state = ConnectState.SENDING_CLOSE
                        sendData(getCloseWifiMsg())
                        Thread.sleep(1000)
                        socketUtil?.release()
                        mWifiMangaer.disconnect()
                        Thread.sleep(10000)
                        uiThread {
                            setLoadText("注册设备信息")
                        }
                        uiThread {
                            registerDevice(deviceIDS)
                        }
                    }
                }
            })
        }
        if (!socketUtil!!.isOpened) {
            socketUtil!!.init()
        }
    }

    private fun isFind(scanResults: List<ScanResult>): Int {
        for (i in 0 until scanResults.size) {
            if (scanResults[i].SSID == WIFI_SSID) {
                return i
            }
        }
        return -1
    }

    private var isCallSearchResult = false

    private fun connectWifi(SSID: String, type: Int) {
        if (isCallSearchResult || state != ConnectState.SEARCHED) {
            return
        }
        doAsync {
            D("call connectWifi $type")
            isCallSearchResult = true
            state = ConnectState.CONNECTING
            uiThread {
                setLoadText("正在连接设备热点")
            }
            var config = checkSavedWifiSSID()
            val netWorkId = if (config != null) {
                D("连接已存在的JIASONG")
                config.networkId
            } else {
                config = createWifiInfo(SSID, "", 1)
                mWifiMangaer.addNetwork(config)
            }
            if (!mWifiMangaer.enableNetwork(netWorkId, true)) {
                uiThread {
                    E("连接设备热点失败,重新搜索")
                    restartProcess(4)
                }
            } else {
                D("热点连接成功")
                state = ConnectState.CONNECTED
            }
            Thread.sleep(3000)
            isCallSearchResult = false
        }
    }

    var count = 0

    fun registerDevice(id: String) {
        val map = mapOf(Pair("acccardtype_id", 0.toString()),
                Pair("card_id", id))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("设备注册成功")
                hideLoading()
                finish()
            }

            override fun onError(context: Context, error: String) {
                if (count >= 10) {
                    context.toast(error)
                    hideLoading()
                    finish()
                    count = 0
                } else {
                    count++
                }
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    hideLoading()
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(this@BindDeviceActivity, BDSB.getInterface(), map)

    }

    private fun checkSavedWifiSSID(): WifiConfiguration? {
        // 获取已保存wifi配置链表
        val configs = mWifiMangaer.configuredNetworks
        // 显示输出
        for (config in configs) {
            if (config.SSID.replace("\"", "") == WIFI_SSID) {
                return config
            }
        }
        return null
    }

    private fun searchWifi() {
        setLoadText("正在搜索设备")
        if (!mWifiMangaer.isWifiEnabled) {
            mWifiMangaer.isWifiEnabled = true
        } else {
            mWifiMangaer.startScan()
        }
    }

    private fun getCloseWifiMsg(): ByteArray {
        val sb = StringBuffer()
        sb.append(charToHexStr(checkDevice[0]))
        sb.append(charToHexStr(checkDevice[1]))
        sb.append(charToHexStr('-'))
        sb.append(charToHexStr('1'))
        sb.append(makeChecksum(sb.toString()))
        D("closeWifi = $sb")
        return CharUtil.string2bytes(sb.toString())
    }


    private fun createMsg(wifiAccount: String, wifiPassword: String): ByteArray {
        val sb = StringBuffer()
        sb.append(charToHexStr(checkDevice[0]))
        sb.append(charToHexStr(checkDevice[1]))
        sb.append(charToHexStr('-'))
        sb.append(charToHexStr('0'))
        for (i in 0 until wifiAccount.length) {
            sb.append(charToHexStr(wifiAccount[i]))
        }
        sb.append("0d")
        for (i in 0 until wifiPassword.length) {
            sb.append(charToHexStr(wifiPassword[i]))
        }
        sb.append("0d")
        sb.append(getCheckSum(wifiAccount, wifiPassword))
        D("createMsg = $sb")
        return CharUtil.string2bytes(sb.toString())
    }

    private fun getCheckSum(wifiAccount: String, wifiPassword: String): String {
        val sb = StringBuffer()
        sb.append(charToHexStr(checkDevice[0]))
        sb.append(charToHexStr(checkDevice[1]))
        sb.append(charToHexStr('-'))
        sb.append(charToHexStr('0'))
        for (i in 0 until wifiAccount.length) {
            sb.append(charToHexStr(wifiAccount[i]))
        }
        sb.append("0d")
        for (i in 0 until wifiPassword.length) {
            sb.append(charToHexStr(wifiPassword[i]))
        }
        sb.append("0d")
        return makeChecksum(sb.toString())
    }

    // 查看以前是否也配置过这个网络
    private fun isExsits(SSID: String): WifiConfiguration? {
        val existingConfigs = mWifiMangaer.configuredNetworks
        for (existingConfig in existingConfigs) {
            if (existingConfig.SSID == "\"" + SSID + "\"") {
                return existingConfig
            }
        }
        return null
    }

    private fun createWifiInfo(SSID: String, Password: String, Type: Int): WifiConfiguration {
        E("create = $SSID")
        val configuration = WifiConfiguration()
        configuration.allowedAuthAlgorithms.clear()
        configuration.allowedGroupCiphers.clear()
        configuration.allowedKeyManagement.clear()
        configuration.allowedPairwiseCiphers.clear()
        configuration.allowedProtocols.clear()
        configuration.SSID = "\"" + SSID + "\""

        val tempConfig = this.isExsits(SSID)
        if (tempConfig != null) {
            D("remove Exsits")
            mWifiMangaer.removeNetwork(tempConfig.networkId)
        }

        when (Type) {
            1//不加密
            -> {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
            2//wep加密
            -> {
                configuration.hiddenSSID = true
                configuration.wepKeys[0] = "\"" + Password + "\""
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
            3 //wpa加密
            -> {

                configuration.preSharedKey = "\"" + Password + "\""
                configuration.hiddenSSID = true
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                configuration.status = WifiConfiguration.Status.ENABLED
            }
        }
        return configuration
    }
}