package com.android.kevin.shuizu.ui

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.kevin.shuizu.R
import com.android.shuizu.myutillibrary.E
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import kotlinx.android.synthetic.main.activity_bind_device.*
import org.jetbrains.anko.doAsync
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
//        val WIFI_SSID = "JIASONG"
    }

    private var socket: Socket? = null
    private val _strIp: String? = null
    private val _nPort: Int = 0
    private var _os: OutputStream? = null

    enum class InfoType {
        SUCCESS, FAILED, PROGRESS
    }

    data class BindDeviceInfo(val infoName: String, val isSuccess: InfoType = InfoType.PROGRESS)

    private val infoData = ArrayList<BindDeviceInfo>()

    val mWifiMangaer: WifiManager by lazy {
        application.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

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
    }

    /**发送数据 */
    fun senddata(senddata: ByteArray) {
        socket = Socket("192.168.4.1", 8899)
        try {
            val byteArrayinputstream = ByteArrayInputStream(senddata)
            socket!!.getOutputStream()!!.write(senddata)
            val istream = socket!!.getInputStream()
            val datalength = istream?.available()
            if (datalength!! > 0) {
                val acceptdata1 = ByteArray(datalength)
                istream.read(acceptdata1)
                Log.i("Data", "the lastresult=$acceptdata1")
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun sendData(byteArray: ByteArray) {
        loadLayout.visibility = View.VISIBLE
        doAsync {
            socket = Socket("192.168.4.1", 8899)
            uiThread {
                bind_info.text = "向设备发送信息"
            }
            //向服务器发送数据
            val send = PrintWriter(BufferedWriter(OutputStreamWriter(socket!!.getOutputStream(), "utf-8")))
            send.println(byteArray)
            send.flush()
            uiThread {
                appendInfo("向设备发送信息成功")
                bind_info.text = "等待服务器返回数据"
            }
            //接受服务端数据
            val recv = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            val recvMsg = recv.readLine()
            uiThread {
                loadLayout.visibility = View.GONE
                if (recvMsg != null) {
                    appendInfo("接收到服务器数据$recvMsg")
                } else {
                    appendInfo("Cannot receive data correctly")
                }
            }
            send.close()
            recv.close()
            socket!!.close()
        }
    }

    private fun appendInfo(string: String) {
        bind_device_info.append(string + "\n")
    }

    private fun dealResult() {
        loadLayout.visibility = View.VISIBLE
        bind_device_info.text = ""
        bind_info.text = "正在搜索设备"
        mWifiMangaer.startScan()
        doAsync {
            val list = mWifiMangaer.scanResults
            var isFined = false
            for (i in 0 until list.size) {
                if (list[i].SSID == deviceWifiAccount.text.toString()) {
                    isFined = true
                    uiThread {
                        appendInfo("已找到设备")
                        bind_info.text = "正在连接设备"
                    }
                    mWifiMangaer.disconnect()
                    val netWorkId = mWifiMangaer.addNetwork(createWifiInfo(list[i].SSID, "", 1))
                    var isSuccess = false
                    var flag = false
                    uiThread {
                        bind_info.text = "正在添加设备信息"
                    }
                    if (netWorkId >= 0) {
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
                            if (currSSID != null && currSSID == deviceWifiAccount.text.toString() && currIp != 0) {
                                isSuccess = true
                                uiThread {
                                    appendInfo("设备连接成功")
                                    loadLayout.visibility = View.GONE
                                    sendWifiLayout.visibility = View.VISIBLE
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

    private fun createMsg(wifiAccount: String, wifiPassword: String): ByteArray {
        var index = 0
        val msg = ByteArray(7 + wifiAccount.length + wifiPassword.length)
        msg[index++] = 'T'.toByte()
        msg[index++] = 'R'.toByte()
        msg[index++] = '-'.toByte()
        msg[index++] = '0'.toByte()
        for (i in 0 until wifiAccount.length) {
            msg[index++] = wifiAccount[i].toByte()
        }
        msg[index++] = 0x0d
        for (i in 0 until wifiPassword.length) {
            msg[index++] = wifiPassword[i].toByte()
        }
        msg[index++] = 0x0d
        val msg2 = makeChecksum(msg, 1)
        msg[index] = msg2[0]
        for (i in msg.indices) {
            bind_device_info.append("${msg[i]}_")
        }
        bind_device_info.append("\n")
        appendInfo(String(msg) + "/")
        return msg
    }

    /**
     * 校验和
     *
     * @param msg
     * 需要计算校验和的byte数组
     * @param length
     * 校验和位数
     * @return 计算出的校验和数组
     */
    fun makeChecksum(msg: ByteArray, length: Int): ByteArray {
        var mSum: Long = 0
        val mByte = ByteArray(length)

        /** 逐Byte添加位数和  */
        for (byteMsg in msg) {
            val mNum = if (byteMsg.toLong() >= 0)
                byteMsg.toLong()
            else
                byteMsg.toLong() + 256
            mSum += mNum
        }
        /** end of for (byte byteMsg : msg)  */

        /** 位数和转化为Byte数组  */
        for (liv_Count in 0 until length) {
            mByte[length - liv_Count - 1] = (mSum shr liv_Count * 8 and 0xff).toByte()
        }
        /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++)  */

        return mByte
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