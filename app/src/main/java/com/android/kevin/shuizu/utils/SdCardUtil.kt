package com.android.kevin.shuizu.utils

import android.os.Environment
import com.android.shuizu.myutillibrary.E
import com.android.shuizu.myutillibrary.utils.FileUtil

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/9/009.
 */
class SdCardUtil{
    companion object {
        val SDCARD:String = Environment.getExternalStorageDirectory().path
        val ROOT = "$SDCARD/shuizu"
        val IMAGE = "$ROOT/image/"
        val APK = "$ROOT/apk/"
    }

    init {
        if(!FileUtil.initPath(SDCARD)){
            E("${SDCARD}初始化失败")
        }
        if(!FileUtil.initPath(ROOT)){
            E("${ROOT}初始化失败")
        }
        if(!FileUtil.initPath(IMAGE)){
            E("${IMAGE}初始化失败")
        }
        if(!FileUtil.initPath(APK)){
            E("${APK}初始化失败")
        }
    }
}