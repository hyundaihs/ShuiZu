package com.android.kevin.shuizu

import android.app.Application
import cn.jpush.android.api.JPushInterface
import com.android.kevin.shuizu.entities.UserInfo
import com.android.kevin.shuizu.utils.SdCardUtil
import kotlin.properties.Delegates

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class SZApplication : Application() {

//    val api: IWXAPI by lazy {
//        WXAPIFactory.createWXAPI(this, APP_ID, false)
//    }

    companion object {
        //        var isLogged: Boolean = false //是否登录
        var instance: SZApplication by Delegates.notNull()
        var userInfo: UserInfo by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SdCardUtil()
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
    }
}

enum class UserID {
    WORKER, BOSS
}