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
        const val LOGIN_ACCOUNT = "login_account"
        const val IS_SET_ALIAS = "isSetAlias"
//const val ACCOUNT = "account"
//const val PASSWORD = "password"

        const val KEYWORD_WATER_MONITOR = "water_monitor"
        const val KEYWORD_WATER_MONITOR_ID = "water_monitor_id"
        const val KEYWORD_WATER_MONITOR_TITLE = "water_monitor_title"
        const val KEYWORD_CHART_DATA_TYPE = "chart_data_type"
        const val KEYWORD_START_DATE = "start_date"
        const val KEYWORD_END_DATE = "end_date"

        const val KEYWORD = "key_word"
        const val KEYWORD_EDIT_GROUP = "edit_group"
        const val KEYWORD_ADD_GROUP = "add_group"

        const val KEYWORD_CIRCLE_SELECTOR_TITLE = "selector_title"
        const val KEYWORD_CIRCLE_SELECTOR_MAX = "selector_max"
        const val KEYWORD_CIRCLE_SELECTOR_MIN = "selector_min"
        const val KEYWORD_CIRCLE_SELECTOR_ISFLOAT = "selector_isfloat"
        const val KEYWORD_CIRCLE_SELECTOR_CURR = "selector_curr"
        const val KEYWORD_CIRCLE_SELECTOR_FLOAT_NUM = "selector_float_num"
        const val KEYWORD_CIRCLE_SELECTOR_DATA_REQUEST = 10 //数据刷新频率
        const val KEYWORD_CIRCLE_SELECTOR_SW_H_HIGH_REQUEST = 11 //水温基准值设置
        const val KEYWORD_CIRCLE_SELECTOR_SW_HIGH_REQUEST = 12 //水温基准值设置
        const val KEYWORD_CIRCLE_SELECTOR_SW_L_LOW_REQUEST = 13 //水温基准值设置
        const val KEYWORD_CIRCLE_SELECTOR_SW_LOW_REQUEST = 14 //水温基准值设置
        const val KEYWORD_CIRCLE_SELECTOR_PH_HIGH_REQUEST = 15 //PH基准值设置
        const val KEYWORD_CIRCLE_SELECTOR_PH_LOW_REQUEST = 16 //PH基准值设置
        const val KEYWORD_CIRCLE_SELECTOR_TDS_REQUEST = 17 //TDS基准值设置
        const val KEYWORD_TIME_DATE_CHOOSER_REQUEST = 18 //TDS基准值设置
        const val KEYWORD_RESULT_OK = 9
    }
}

/*“TR”:水质监测，“HT”:加热棒，“WP”：水泵，“PF”：断电报警器,"WL"：水位报警*/
enum class DeviceType {
    TR, HT, WP, PF, WL
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
[create_time] => 创建时间（时间戳）
[counts] => 设备数量
*/
data class YGInfo(val id: Int, val title: String, val create_time: Long, val counts: Int)

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

/*acccardtype_id：分组id（0为常用设备）
ids：设备id数组（[1,2,3]）*/
data class PostDeviceIds(val acccardtype_id: Int, val ids: ArrayList<Int>)

/*[phjz_status] => 状态（0：未校准，1：开始校准，2：第一次校准成功，3：第二次开始校准，4：校准成功，5：校准失败）
[phjz_time] => 校准时间（时间戳）*/
data class PHJZ_Info(val phjz_status: Int, val title: String, val phjz_time: Long)

data class PHJZ_InfoRes(val retRes: PHJZ_Info) : RequestResult()

/*[sz_status] => 设置状态（0:未设置，1：正在设置，2：设置成功，3：设置失败）
[sz_time] => 设置时间
[v_1] => 极度低温值
[v_2] => 低温值
[v_3] => 高温值
[v_4] => 极度高温值*/
data class BaoJin_Info(val sz_status: Int, val sz_time: Long, val v_1: Float, val v_2: Float, val v_3: Float, val v_4: Float)

data class BaoJin_InfoRes(val retRes: BaoJin_Info) : RequestResult()

/*[id] => 日志id
[true_value] => 报警值
[create_time] => 时间（时间戳）
[title] => 标题（鱼缸水温达到极限26.2摄氏度）*/
data class WarnLog(val id: Int, val true_value: Float, val create_time: Long, val title: String)

data class WarnLogListRes(val retRes: ArrayList<WarnLog>) : RequestResult()