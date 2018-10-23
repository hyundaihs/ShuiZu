package com.android.shuizu.myutillibrary.request

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.utils.MyProgressDialog
import com.google.gson.Gson
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


/**
 * LeZu
 * Created by 蔡雨峰 on 2018/3/26.
 */

public class MySimpleRequest(var callback: RequestCallBack? = null, val getProgress: Boolean = true) {


    private val mOkHttpClient: OkHttpClient by lazy {
        val httpBuilder = OkHttpClient.Builder()
        httpBuilder
                //设置超时
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .build()
    }

    companion object {
        var sessionId: String = ""

        private val MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded charset=utf-8")//mdiatype 这个需要和服务端保持一致

        const val LOGINERR = "loginerr"//需要重新登录错误信息


    }

    fun getRequest(context: Context, url: String) {
        var dialog: AlertDialog? = null
        if (getProgress) {
            dialog = MyProgressDialog(context)
        }
        context.doAsync {
            val request = Request.Builder().url(url).addHeader("cookie", sessionId).build()
            val call = mOkHttpClient.newCall(request)
            //请求加入调度
            val response = call.execute()
            if (response.isSuccessful) {
                val string = response.body().string()
                getSession(response)
                val res = Gson().fromJson(string, RequestResult::class.java)
                if (res.retInt == 1) {
                    uiThread {
                        if (getProgress) {
                            dialog?.dismiss()
                        }
                        callback?.onSuccess(context, string)
                    }
                } else {
                    if (res.retErr == LOGINERR) {
                        uiThread {
                            if (getProgress) {
                                dialog?.dismiss()
                            }
                            callback?.onLoginErr(context)
                        }
                    } else {
                        uiThread {
                            if (getProgress) {
                                dialog?.dismiss()
                            }
                            callback?.onError(context, res.retErr)
                        }
                    }
                }
            } else {
                uiThread {
                    if (getProgress) {
                        dialog?.dismiss()
                    }
                    callback?.onError(context, response.message())
                }
            }
        }
    }

    fun postRequest(context: Context, url: String, map: Map<String, Any> = mapOf(Pair("", ""))) {
        var dialog: AlertDialog? = null
        if (getProgress) {
            dialog = MyProgressDialog(context)
        }
        D(url + "\n" + Gson().toJson(map).toString())
        context.doAsync {
            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, Gson().toJson(map))
            val request = Request.Builder().url(url).post(requestBody).addHeader("cookie", sessionId).build()
            try {
                val response = mOkHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val string = response.body().string()
                    getSession(response)
                    val res: RequestResult = Gson().fromJson(string, RequestResult::class.java)
                    D("requestResult = $string")
                    if (res.retInt == 1) {
                        uiThread {
                            if (getProgress) {
                                dialog?.dismiss()
                            }
                            callback?.onSuccess(context, string)
                        }
                    } else {
                        if (res.retErr == LOGINERR) {
                            uiThread {
                                if (getProgress) {
                                    dialog?.dismiss()
                                }
                                callback?.onLoginErr(context)
                            }
                        } else {
                            uiThread {
                                if (getProgress) {
                                    dialog?.dismiss()
                                }
                                callback?.onError(context, res.retErr)
                            }
                        }
                    }
                } else {
                    uiThread {
                        if (getProgress) {
                            dialog?.dismiss()
                        }
                        callback?.onError(context, response.message())
                    }
                }
            } catch (e: Exception) {
                uiThread {
                    if (getProgress) {
                        dialog?.dismiss()
                    }
                    callback?.onError(context, e.toString())
                }
            }
        }
    }

    fun postRequest(context: Context, url: String, str: String) {
        var dialog: AlertDialog? = null
        if (getProgress) {
            dialog = MyProgressDialog(context)
        }
        D(url + "\n" + str)
        context.doAsync {
            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, str)
            val request = Request.Builder().url(url).post(requestBody).addHeader("cookie", sessionId).build()
            try {
                val response = mOkHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val string = response.body().string()
                    getSession(response)
                    val res: RequestResult = Gson().fromJson(string, RequestResult::class.java)
                    D("requestResult = $string")
                    if (res.retInt == 1) {
                        uiThread {
                            if (getProgress) {
                                dialog?.dismiss()
                            }
                            callback?.onSuccess(context, string)
                        }
                    } else {
                        if (res.retErr == LOGINERR) {
                            uiThread {
                                if (getProgress) {
                                    dialog?.dismiss()
                                }
                                callback?.onLoginErr(context)
                            }
                        } else {
                            uiThread {
                                if (getProgress) {
                                    dialog?.dismiss()
                                }
                                callback?.onError(context, res.retErr)
                            }
                        }
                    }
                } else {
                    uiThread {
                        if (getProgress) {
                            dialog?.dismiss()
                        }
                        callback?.onError(context, response.message())
                    }
                }
            } catch (e: Exception) {
                uiThread {
                    if (getProgress) {
                        dialog?.dismiss()
                    }
                    callback?.onError(context, e.toString())
                }
            }
        }
    }

    fun uploadFile(context: Context, url: String, files: ArrayList<String>) {
        var dialog: AlertDialog? = null
        if (getProgress) {
            dialog = MyProgressDialog(context)
        }
        doAsync {
            val name = "uploadedfile[]"
            val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            for (i in 0 until files.size) {
                val file = File(files[i])
                val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
                requestBodyBuilder.addFormDataPart(name, files[i].substring(files[i].lastIndexOf("/")), fileBody)
            }
            val requestBody = requestBodyBuilder.build()
            val request = Request.Builder()
                    .url(url)
                    .post(requestBody).addHeader("cookie", sessionId)
                    .build()
            try {
                val response = mOkHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val string = response.body().string()
                    getSession(response)
                    val res: RequestResult = Gson().fromJson(string, RequestResult::class.java)
                    if (res.retInt == 1) {
                        uiThread {
                            if (getProgress) {
                                dialog?.dismiss()
                            }
                            callback?.onSuccess(context, string)
                        }
                    } else {
                        if (res.retErr == LOGINERR) {
                            uiThread {
                                if (getProgress) {
                                    dialog?.dismiss()
                                }
                                callback?.onLoginErr(context)
                            }
                        } else {
                            uiThread {
                                if (getProgress) {
                                    dialog?.dismiss()
                                }
                                callback?.onError(context, res.retErr)
                            }
                        }
                    }
                } else {
                    uiThread {
                        if (getProgress) {
                            dialog?.dismiss()
                        }
                        callback?.onError(context, "无响应:" + response.message())
                    }
                }
            } catch (e: Exception) {
                uiThread {
                    if (getProgress) {
                        dialog?.dismiss()
                    }
                    callback?.onError(context, "异常:" + e.toString())
                }
            }
        }
    }

    fun downLoadFile(fileUrl: String, destFileDir: String, callBack: ReqProgressCallBack) {
        doAsync {
            val request = Request.Builder().url(fileUrl).addHeader("cookie", sessionId).build()
            mOkHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    uiThread {
                        callBack.onFailed(e.toString())
                    }
                }

                override fun onResponse(call: Call?, response: Response) {
                    var ips: InputStream? = null
                    val buf = ByteArray(2048)
                    var len = 0
                    var fos: FileOutputStream? = null
//                FileUtil.isFileExist(destFileDir)
                    // 储存下载文件的目录
//                val savePath = isExistDir(destFileDir)
                    try {
                        ips = response.body().byteStream()
                        val total = response.body().contentLength()
                        val file = File(destFileDir)
                        fos = FileOutputStream(file)
                        if (ips == null) {
                            uiThread {
                                callBack.onFailed("数据获取失败")
                            }
                            return
                        }
                        var sum = 0
                        while (true) {
                            len = ips.read(buf)
                            if (len == -1) {
                                break
                            }
                            fos.write(buf, 0, len)
                            sum += len
                            val progress = (sum * 1.0f / total * 100).toLong()
                            // 下载中
                            uiThread {
                                callBack.onProgress(total, progress)
                            }
                        }
                        fos.flush()
                        uiThread {
                            callBack.onSuccess(file)
                        }
                    } catch (e: Exception) {
                        uiThread {
                            callBack.onFailed(e.toString())
                        }
                    } finally {
                        try {
                            if (ips != null)
                                ips.close()
                        } catch (e: IOException) {
                        }
                        try {
                            if (fos != null)
                                fos.close()
                        } catch (e: IOException) {
                        }
                    }
                }

            })
        }
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private fun isExistDir(saveDir: String): String {
        // 下载位置
        val downloadFile = File(Environment.getExternalStorageDirectory(), saveDir)
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        return downloadFile.absolutePath
    }

    /*
     @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed()
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null
                byte[] buf = new byte[2048]
                int len = 0
                FileOutputStream fos = null
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir)
                try {
                    is = response.body().byteStream()
                    long total = response.body().contentLength()
                    File file = new File(savePath, getNameFromUrl(url))
                    fos = new FileOutputStream(file)
                    long sum = 0
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len)
                        sum += len
                        int progress = (int) (sum * 1.0f / total * 100)
                        // 下载中
                        listener.onDownloading(progress)
                    }
                    fos.flush()
                    // 下载完成
                    listener.onDownloadSuccess()
                } catch (Exception e) {
                    listener.onDownloadFailed()
                } finally {
                    try {
                        if (is != null)
                        is.close()
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close()
                    } catch (IOException e) {
                    }
                }
            }
     */

    private fun getSession(response: Response) {
        val headers = response.headers()
        val cookies = headers.values("Set-Cookie")
        if (cookies.isEmpty()) {
            return
        }
        val session = cookies[0]
        sessionId = session.substring(0, session.indexOf(";"))
    }

    interface RequestCallBack {
        fun onSuccess(context: Context, result: String)
        fun onError(context: Context, error: String)
        fun onLoginErr(context: Context)
    }

    interface ReqProgressCallBack {
        fun onProgress(total: Long, current: Long)
        fun onSuccess(file: File)
        fun onFailed(e: String)
    }

}

open class RequestResult(val retInt: Int = 0, val retErr: String = "", val retUrl: String = "", val retCounts: Int = 0) {
    override fun toString(): String {
        return "RequestResult(retInt=$retInt, retErr='$retErr', retCounts=$retCounts)"
    }
}