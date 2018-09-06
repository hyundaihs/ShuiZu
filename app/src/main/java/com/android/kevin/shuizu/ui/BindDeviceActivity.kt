package com.android.kevin.shuizu.ui

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.utils.SocketUtil
import com.android.kevin.shuizu.utils.SocketUtil.OnMsgComing
import com.android.shuizu.myutillibrary.E
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.utils.charToHexStr
import com.android.shuizu.myutillibrary.utils.makeChecksum
import kotlinx.android.synthetic.main.activity_bind_device.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.*
import java.net.Socket
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

    data class BindDeviceInfo(val infoName: String, val isSuccess: InfoType = InfoType.PROGRESS)

    private val infoData = ArrayList<BindDeviceInfo>()

    val mWifiMangaer: WifiManager by lazy {
        application.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    var socketUtil: SocketUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_device)
        initActionBar(this, "添加新设备", rightBtn = "添加", rightClick = View.OnClickListener {
            dealResult()
        })
//        dealResult()
        sendWifi.setOnClickListener {
            sendData(createMsg(wifiAccount.text.toString(), wifiPassword.text.toString()))
        }
        dealResult()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun sendData(byteArray: ByteArray) {
        loadLayout.visibility = View.VISIBLE
        socketUtil!!.openReceiver()
        socketUtil!!.onMsgComing = object : OnMsgComing {
            override fun onMsgCome(byteArray: ByteArray) {
                val sb = StringBuffer()
                for (i in byteArray.indices) {
                    sb.append(byteArray[i].toInt())
                }
                appendInfo("服务器返回数据")
                appendInfo(sb.toString())
                socketUtil!!.release()
            }
        }
        socketUtil!!.sendMsg(byteArray)
    }

    private fun appendInfo(string: String) {
        bind_device_info.append(string + "\n")
    }

    private fun dealResult() {
        loadLayout.visibility = View.VISIBLE
        bind_device_info.text = ""
        bind_info.text = "正在搜索设备"

        if (checkConnectWifiSSID()) {
            appendInfo("设备连接成功")
            loadLayout.visibility = View.GONE
            sendWifiLayout.visibility = View.VISIBLE
            return
        }
        mWifiMangaer.startScan()
        doAsync {
            val list = mWifiMangaer.scanResults
            var isFined = false
            for (i in 0 until list.size) {
                if (list[i].SSID == WIFI_SSID) {
                    isFined = true
                    uiThread {
                        appendInfo("已找到设备")
                        bind_info.text = "正在连接设备"
                    }
                    var netWorkId = checkSavedWifiSSID()
                    if (netWorkId == -1) {
                        netWorkId = mWifiMangaer.addNetwork(createWifiInfo(list[i].SSID, "", 1))
                    }
                    var isSuccess = false
                    var flag = false
                    uiThread {
                        bind_info.text = "正在添加设备信息"
                    }
                    if (netWorkId >= 0) {
                        mWifiMangaer.disconnect()
                        uiThread {
                            appendInfo("设备信息添加成功")
                            bind_info.text = "正在连接设备"
                        }
                        while (!flag && !isSuccess) {
                            mWifiMangaer.enableNetwork(netWorkId, true)
                            try {
                                Thread.sleep(2000)
                            } catch (e1: InterruptedException) {
                                e1.printStackTrace()
                            }
                            var currSSID = mWifiMangaer.connectionInfo.ssid
                            if (currSSID != null)
                                currSSID = currSSID.replace("\"", "")
                            val currIp = mWifiMangaer.connectionInfo.ipAddress
                            if (currSSID != null && currSSID == WIFI_SSID && currIp != 0) {
                                isSuccess = true
                                Thread.sleep(2000)
                                uiThread {
                                    appendInfo("设备连接成功")
                                    loadLayout.visibility = View.GONE
                                    sendWifiLayout.visibility = View.VISIBLE
                                    socketUtil = SocketUtil("192.168.4.1", 8899)
                                }
                            } else {
                                flag = true
                            }
                        }
                    } else {
                        uiThread {
                            appendInfo("设备信息添加失败")
                            loadLayout.visibility = View.GONE
                        }
                    }
                }
            }
            if (!isFined) {
                uiThread {
                    appendInfo("未找到可用设备")
                    loadLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun checkSavedWifiSSID(): Int {
        // 获取已保存wifi配置链表
        val configs = mWifiMangaer.configuredNetworks
        // 显示输出
        for (config in configs) {
            if (config.SSID == WIFI_SSID) {
                return config.networkId
            }
        }
        return -1
    }

    private fun checkConnectWifiSSID(): Boolean {
        val wifiInfo = mWifiMangaer.connectionInfo
        return wifiInfo.ssid == WIFI_SSID
    }


    private fun createMsg(wifiAccount: String, wifiPassword: String): ByteArray {
        val sb = StringBuffer()
        sb.append('T')
        sb.append('R')
        sb.append('-')
        sb.append('0')
        for (i in 0 until wifiAccount.length) {
            sb.append(wifiAccount[i])
        }
        sb.append(13.toChar())
        for (i in 0 until wifiPassword.length) {
            sb.append(wifiPassword[i])
        }
        sb.append(13.toChar())
        sb.append(Integer.valueOf(getCheckSum(wifiAccount, wifiPassword), 16).toChar())
        return sb.toString().toByteArray()
    }

    private fun getCheckSum(wifiAccount: String, wifiPassword: String): String {
        val sb = StringBuffer()
        sb.append(charToHexStr('T'))
        sb.append(charToHexStr('R'))
        sb.append(charToHexStr('-'))
        sb.append(charToHexStr('0'))
        for (i in 0 until wifiAccount.length) {
            sb.append(charToHexStr(wifiAccount[i]))
        }
        sb.append("0d")
        for (i in 0 until wifiPassword.length) {
            sb.append(charToHexStr(wifiPassword[i]))
        }
        sb.append("0d" + " ")
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