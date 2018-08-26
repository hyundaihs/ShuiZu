package com.android.shuizu.myutillibrary.utils

import android.content.Context
import android.os.Environment

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/6/16/016.
 */
class AppPath(context: Context) {
    companion object {
        val SDCARD:String = Environment.getExternalStorageDirectory().path
        val ROOT = "$SDCARD/LeZu"
        val APK = "$ROOT/apk/"
    }

    init {
        if(FileUtil.initPath(SDCARD)){

        }
        if(FileUtil.initPath(ROOT)){

        }
        if(FileUtil.initPath(APK)){

        }
    }
}