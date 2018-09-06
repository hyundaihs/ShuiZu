package com.android.kevin.shuizu.utils

import com.android.shuizu.myutillibrary.D
import org.jetbrains.anko.doAsync
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/9/6/006.
 */
class SocketUtil(host: String, port: Int) {
    var socket: Socket? = null
    var mBufferedReaderClient: InputStream? = null
    var mPrintWriterClient: OutputStream? = null

    init {
        doAsync {
            socket = Socket(host, port)
            if (socket!!.isConnected) {
                mBufferedReaderClient = socket!!.getInputStream()
                mPrintWriterClient = socket!!.getOutputStream()
                D("socket is connected")
            }
        }
    }

    public fun sendMsg(byteArray: ByteArray) {
        doAsync {
            mPrintWriterClient!!.write(byteArray)//发送数据
            mPrintWriterClient!!.flush()//清空数据缓冲区
        }
    }

    public fun openReceiver() {
        doAsync {
            val byteArray = ByteArray(256)
            mBufferedReaderClient!!.read(byteArray)//将接收到的数据存放在buffer数组中
            D(String(byteArray))
        }
    }

}