package com.android.kevin.shuizu.utils

import com.android.shuizu.myutillibrary.D
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/9/6/006.
 */
class SocketUtil(host: String, port: Int, val onMsgComing: OnMsgComing) {
    var socket: Socket? = null
    var mBufferedReaderClient: InputStream? = null
    var mPrintWriterClient: OutputStream? = null
    var isOpened = false

    init {
        doAsync {
            socket = Socket(host, port)
            Thread.sleep(500)
            if (socket!!.isConnected) {
                mBufferedReaderClient = socket!!.getInputStream()
                mPrintWriterClient = socket!!.getOutputStream()
                openReceiver()
                uiThread {
                    onMsgComing.onInitSocket(true)
                }
            } else {
               uiThread {
                   onMsgComing.onInitSocket(false)
               }
            }
        }
    }

    fun sendMsg(byteArray: ByteArray) {
        doAsync {
            mPrintWriterClient!!.write(byteArray)//发送数据
            mPrintWriterClient!!.flush()//清空数据缓冲区
        }
    }

    fun openReceiver() {
        isOpened = true
        doAsync {
            while (isOpened) {
                val byteArray = ByteArray(256)
                mBufferedReaderClient!!.read(byteArray)//将接收到的数据存放在buffer数组中
                D(String(byteArray))
                uiThread {
                    onMsgComing.onMsgCome(byteArray)
                }
            }
            mBufferedReaderClient!!.close()
            mPrintWriterClient!!.close()
            socket!!.close()
        }
    }

    fun release() {
        isOpened = false
    }

    interface OnMsgComing {
        fun onInitSocket(isSuccess: Boolean)
        fun onMsgCome(byteArray: ByteArray)
    }

}