package com.android.kevin.shuizu.entities

import android.os.Parcelable
import com.android.shuizu.myutillibrary.request.RequestResult
import kotlinx.android.parcel.Parcelize

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/21/021.
 */

class App_Actions {
    companion object {
        const val ACTION_WATER_MONITOR = "action.water_monitor"
    }
}

class App_Keyword {
    companion object {
        const val LOGIN_VERF = "login_verf"
//const val ACCOUNT = "account"
//const val PASSWORD = "password"

        const val KEYWORD_WATER_MONITOR = "water_monitor"
        const val KEYWORD_WATER_MONITOR_ID = "water_monitor_id"
        const val KEYWORD_WATER_MONITOR_TITLE = "water_monitor_title"
        const val KEYWORD_CHART_DATA_TYPE = "chart_data_type"
    }
}

/*“TR”:水质监测，“HT”:加热棒，“WP”：水泵，“PF”：断电报警器*/
enum class DeviceType {
    TR, HT, WP, PF
}

@Parcelize
class ChartDataType : Parcelable {
    companion object {
        const val WD = 0
        const val PH = 1
        const val TDS = 2
    }
}

/*[login_verf] => 自动登录密码*/
data class LoginInfo(val login_verf: String)

data class LoginInfoRes(val retRes: LoginInfo) : RequestResult()

/*[v_title] => 版本名称
[v_num] => 版本号码
[http_url] => 下载地址*/
data class VersionInfo(val v_title: String, val v_num: Int, val http_url: String)

data class VersionInfoRes(val retRes: VersionInfo) : RequestResult()

/*[id] => 鱼缸id
[title] => 名称
[create_time] => 创建时间（时间戳）*/
data class YGInfo(val id: Int, val title: String, val create_time: Long)

data class YGInfoRes(val retRes: YGInfo) : RequestResult()

data class YGInfoListRes(val retRes: ArrayList<YGInfo>) : RequestResult()

val DEVICE_IS_ONLINE = arrayOf("离线", "在线")

/*[id] => 设备id
[acccardtype_id] => 鱼缸id（0为常用设备）
[card_type] => 设备类型（“TR”:水质监测，“HT”:加热棒，“WP”：水泵，“PF”：断电报警器）
[title] => 设备名称
[create_time] => 绑定时间（时间戳）
[is_online] => 设备是否在线（0：离线，1：在线）*/
data class MyDevice(val id: Int, val acccardtype_id: Int, val card_type: DeviceType,
                    val title: String, val create_time: Long, val is_online: Int)

data class MyDeviceListRes(val retRes: ArrayList<MyDevice>) : RequestResult()

val WATER_LEVEL = arrayOf("无", "极优", "优良", "良", "一般", "差")

/*[is_online] => 设备是否在线（0：离线，1：在线）
[wd] => 温度
[ph] => PH
[tds] => TDS
[zl] => 水质级别（0:无，1：极优，2：优良，3:良，4：一般，5：差）*/
@Parcelize
data class WaterMonitorInfo(val is_online: Int, val wd: Float, val ph: Float, val tds: Int, val zl: Int) : Parcelable

data class WaterMonitorInfoRes(val retRes: WaterMonitorInfo) : RequestResult()

/*[x] => x轴（时间戳）
[y] => y轴（数值）*/
data class WaterHistoryData(val x: Long, val y: Float)

data class WaterHistoryDataRes(val retRes: ArrayList<WaterHistoryData>) : RequestResult()