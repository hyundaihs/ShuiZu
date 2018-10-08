package com.android.kevin.shuizu.ui

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
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.BDSB
import com.android.kevin.shuizu.entities.DeviceType
import com.android.kevin.shuizu.entities.WarnLog
import com.android.kevin.shuizu.entities.getInterface
import com.android.kevin.shuizu.ui.BindDeviceActivity.MyAdapter.MyOnItemClickListener
import com.android.kevin.shuizu.utils.CharUtil
import com.android.kevin.shuizu.utils.SocketUtil
import com.android.kevin.shuizu.utils.SocketUtil.OnMsgComing
import com.android.shuizu.myutillibrary.*
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.utils.charToHexStr
import com.android.shuizu.myutillibrary.utils.makeChecksum
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import kotlinx.android.synthetic.main.activity_bind_device.*
import kotlinx.android.synthetic.main.layout_warn_log_list_item.view.*
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
    var checkDevice: String = "TR"
    var flag = false
    var state : ConnectState = ConnectState.SEARCHED
    enum class ConnectState{
        SEARCHING,SEARCHED,CONNECTING,CONNECTED,INIT_SOCKET,SENDING_CLOSE
    }

    private val receiver = MyBroadCastReceiver()

    inner class MyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            D("action = $action")
            when (action) {
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                    E("WIFI_STATE_CHANGED_ACTION")
//                    val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
//                    if (wifiState == WIFI_STATE_ENABLED && !isWifiFind) {
//                        mWifiMangaer.startScan()
//                    }
                }
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                    D("SCAN_RESULTS_AVAILABLE_ACTION")
                    scanResults.clear()
                    scanResults.addAll(mWifiMangaer.scanResults)
                    mAdapter.notifyDataSetChanged()
                    if(state == ConnectState.SEARCHING){
                        wifiList.visibility = View.GONE
                        val rel = isFind(scanResults)
                        if (rel >= 0) {
                            state == ConnectState.SEARCHED
                            dealSearchResult(scanResults[rel].SSID)
                        }
                    }

                    if(state == ConnectState.INIT_SOCKET){
                        wifiList.visibility = View.VISIBLE
                    }
                }
                WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                    D("NETWORK_STATE_CHANGED_ACTION")
                    //拿到NetworkInfo
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                    //判断连接上了哈
                    if (null != networkInfo && networkInfo.isConnected) {
                        //连接上了,就把wifi的信息传出去
                        val wifiInfo = intent.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO)
                        if (wifiInfo != null && wifiInfo.ssid.replace("\"", "") == WIFI_SSID) {
                            //把结果回传出去
                            connectDevice()
                        } else {
                            if (state == ConnectState.SENDING_CLOSE) {
                                setLoadText("注册信息 ${wifiInfo.ssid}")
                                socketUtil?.release()
                                registerDevice(deviceIDS)
                            }
                        }
                    }
                }
                WifiManager.SUPPLICANT_STATE_CHANGED_ACTION -> {
                    D("SUPPLICANT_STATE_CHANGED_ACTION")
                }
            }
        }
    }

    /** 
         * 将搜索到的wifi根据信号强度从强到时弱进行排序 
         * @param list  存放周围wifi热点对象的列表 
    //         */
//        private fun sortByLevel(ArrayList<ScanResult> list) {  
//          
//                Collections.sort(list, new Comparator<ScanResult>() {  
//              
//                        @Override  
//                        public int compare(ScanResult lhs, ScanResult rhs) {  
//                                return rhs.level - lhs.level;  
//                            }  
//                    });  
//            } 

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
               // sendData(createMsg(wifiAccount.text.toString(), wifiPassword.text.toString()))
            }
        }
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
        flag = false
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
        searchWifi()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

//    private var connectCount = 0
//    private var waitRegister = false
//
//
//    private var isCallConnect = false

    private fun connectDevice() {
        setLoadText("正在连接设备")
        if (null == socketUtil) {
            socketUtil = SocketUtil("192.168.4.1", 8899, object : OnMsgComing {
                override fun onInitSocket(isSuccess: Boolean) {
                    if (isSuccess) {
                        toast("设备连接成功,请输入Wifi信息")
                        state = ConnectState.INIT_SOCKET
                    } else {
                        toast("设备连接失败,请重新搜索")
                    }
                }

                override fun onMsgCome(byteArray: ByteArray) {
                    setLoadText("发送关闭热点信息")
                    state = ConnectState.SENDING_CLOSE
                    doAsync {
                        uiThread {
                            sendData(getCloseWifiMsg())
                        }
                        val temp = String(byteArray)
                        deviceIDS = temp.substring(0, 3) + temp.substring(4, temp.lastIndex)
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

    private fun dealSearchResult(SSID: String) {
        state = ConnectState.CONNECTING
        doAsync {
            uiThread {
                setLoadText("正在连接设备热点")
            }
            var netWorkId = checkSavedWifiSSID()
            if (netWorkId == -1) {
                netWorkId = mWifiMangaer.addNetwork(createWifiInfo(SSID, "", 1))
            }
            if (netWorkId >= 0) {
                mWifiMangaer.disconnect()
                Thread.sleep(500)
                if (!mWifiMangaer.enableNetwork(netWorkId, true)) {
                    uiThread {
                        toast("设备连接失败")
                        hideLoading()
                    }
                }else{
                    state = ConnectState.CONNECTED
                    hideLoading()
                }
            } else {
                uiThread {
                    toast("设备连接失败")
                    hideLoading()
                }
            }
        }
    }


    fun registerDevice(id: String) {
        doAsync {
            flag = true
            var count = 0
            while (flag) {
                Thread.sleep(5000)
                val map = mapOf(Pair("acccardtype_id", 0.toString()),
                        Pair("card_id", id))
                MySimpleRequest(object : MySimpleRequest.RequestCallBack {
                    override fun onSuccess(context: Context, result: String) {
                        flag = false
                        toast("设备注册成功")
                        finish()
                    }

                    override fun onError(context: Context, error: String) {
                        if (count >= 10) {
                            flag = false
                            context.toast(error)
                            finish()
                        }
                    }

                    override fun onLoginErr(context: Context) {
                        flag = false
                        context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                            val intent = Intent(context, LoginActivity::class.java)
                            startActivity(intent)
                        })
                    }

                }, false).postRequest(this@BindDeviceActivity, getInterface(BDSB), map)
                count++
            }
        }
    }

    private fun checkSavedWifiSSID(): Int {
        // 获取已保存wifi配置链表
        val configs = mWifiMangaer.configuredNetworks
        // 显示输出
        for (config in configs) {
            if (config.SSID.replace("\"", "") == WIFI_SSID) {
                return config.networkId
            }
        }
        return -1
    }

    private fun searchWifi() {
        setLoadText("正在搜索设备")
        if (!mWifiMangaer.isWifiEnabled) {
            mWifiMangaer.isWifiEnabled = true
        }
        doAsync {
            Thread.sleep(2000)
            mWifiMangaer.startScan()
            state = ConnectState.SEARCHING
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

    fun createWifiInfo(SSID: String, Password: String, Type: Int): WifiConfiguration {
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