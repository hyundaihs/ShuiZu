package com.android.kevin.shuizu.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.text.TextUtils
import com.android.kevin.shuizu.entities.VersionInfo
import com.android.kevin.shuizu.entities.VersionInfoRes
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.E
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.security.auth.callback.Callback

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/6/16/016.
 */
class VersionUtil(val context: Activity) {

    val localVersionCode by lazy {
        context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    }
    val localVersionName by lazy {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    fun check(versionCallBack: VersionCallBack) {
        getServiceVersion(versionCallBack)
    }

    private fun getServiceVersion(versionCallBack: VersionCallBack) {
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val versionInfoRes = Gson().fromJson(result, VersionInfoRes::class.java)
                val versionInfo = versionInfoRes.retRes
                match(versionInfo, versionCallBack)
            }

            override fun onError(context: Context, error: String) {
                versionCallBack.noNewVersion(localVersionName, localVersionCode)
            }

            override fun onLoginErr(context: Context) {

            }

        }).postRequest(context, "", mapOf(Pair("app", "android")))
    }

    private fun match(versionInfo: VersionInfo, versionCallBack: VersionCallBack) {
        if (localVersionCode < versionInfo.v_num) {
            versionCallBack.hasNewVersion(versionInfo.v_title, versionInfo.v_num, versionInfo.http_url, localVersionName, localVersionCode)
        } else {
            versionCallBack.noNewVersion(localVersionName, localVersionCode)
        }
    }

    interface VersionCallBack {
        fun hasNewVersion(versionName: String, versionCode: Int, url: String, oldVerName: String, oldVerCode: Int)

        fun noNewVersion(versionName: String, versionCode: Int)
    }

    fun installApk(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            val apkUri = FileProvider.getUriForFile(context, "com.lezu.fileprovider", file)
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }

        context.startActivity(intent)
    }

    fun installApk(apkPath: String) {
        if (TextUtils.isEmpty(apkPath)) {
            return
        }
        val file = File(apkPath)
        installApk(file)
    }
}
