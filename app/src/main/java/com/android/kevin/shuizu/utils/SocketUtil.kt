package com.android.kevin.shuizu.utils

import com.android.shuizu.myutillibrary.D
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream
import java.io.OutputStream
import java.net.ConnectException
import java.net.Socket

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/9/6/006.
 */
class SocketUtil(val host: String, private val port: Int, private val onMsgComing: OnMsgComing) {
    var socket: Socket? = null
    var mBufferedReaderClient: InputStream? = null
    var mPrintWriterClient: OutputStream? = null
    var isOpened = false

    fun init() {
        doAsync {
            try {
                socket = Socket(host, port)
                Thread.sleep(500)
                if (socket!!.isConnected) {
                    mBufferedReaderClient = socket!!.getInputStream()
                    mPrintWriterClient = socket!!.getOutputStream()
                    uiThread {
                        onMsgComing.onInitSocket(true)
                    }
                    openReceiver()
                } else {
                    uiThread {
                        onMsgComing.onInitSocket(false)
                    }
                }
            } catch (e: ConnectException) {
                uiThread {
                    onMsgComing.onInitSocket(false)
                }
            }
        }
    }

//    fun open(){
//        try {
//            socket = Socket(host, port)
//            if (socket!!.isConnected) {
//                mBufferedReaderClient = socket!!.getInputStream()
//                mPrintWriterClient = socket!!.getOutputStream()
////                uiThread {
//                    onMsgComing.onInitSocket(true)
////                }
//                openReceiver()
//            } else {
////                uiThread {
//                    onMsgComing.onInitSocket(false)
////                }
//            }
//        } catch (e: ConnectException) {
////            uiThread {
//                onMsgComing.onInitSocket(false)
////            }
//        }
//    }

    fun sendMsg(byteArray: ByteArray) {
        doAsync {
            mPrintWriterClient!!.write(byteArray)//发送数据
            mPrintWriterClient!!.flush()//清空数据缓冲区
        }
    }

    private fun openReceiver() {
        isOpened = true
        doAsync {
            val temp = ByteArray(256)
            val length = mBufferedReaderClient!!.read(temp)//将接收到的数据存放在buffer数组中
            val rel = ByteArray(length)
            System.arraycopy(temp, 0, rel, 0, length)
            D("服务器返回数据 = ${String(rel)}")
            uiThread {
                onMsgComing.onMsgCome(rel)
            }
        }
    }

    fun release() {
        mBufferedReaderClient!!.close()
        mPrintWriterClient!!.close()
        socket!!.close()
    }

    interface OnMsgComing {
        fun onInitSocket(isSuccess: Boolean)
        fun onMsgCome(byteArray: ByteArray)
    }

}