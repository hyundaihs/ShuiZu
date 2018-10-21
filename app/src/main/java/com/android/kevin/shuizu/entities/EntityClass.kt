package com.android.kevin.shuizu.entities

import com.android.shuizu.myutillibrary.request.RequestResult

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
        const val KEYWORD_CIRCLE_SELECTOR_PH_FIRST_REQUEST = 19 //TDS基准值设置
        const val KEYWORD_CIRCLE_SELECTOR_PH_SECOND_REQUEST = 20 //TDS基准值设置
        const val KEYWORD_RESULT_OK = 9
    }
}

/*“TR”:水质监测，“HT”:加热棒，“WP”：水泵，“PF”：断电报警器,"WL"：水位报警*/
enum class DeviceType {
    TR, HT, WP, PF, WL
}


class ChartDataType {
    companion object {
        const val WD = 0
        const val PH = 1
        const val TDS = 2
    }
}

data class DateInfo(val dates: String)

data class DateInfoRes(val retRes: DateInfo) : RequestResult()

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
[wd_zl] => 温度质量（0：正常，1：偏低，2：偏高）
[ph_zl] => PH质量（0：正常，1：偏低，2：偏高）
[tds_zl] => TDS质量（0：正常，1：偏低，2：偏高）
[zl] => 水质级别（0:无，1：极优，2：优良，3:良，4：一般，5：差）*/

data class WaterMonitorInfo(val is_online: Int, val wd: Float, val ph: Float, val tds: Int, val wd_zl: Int, val ph_zl: Int, val tds_zl: Int, val zl: Int)

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

/*[id] => 用户id
[account] => 账号
[title] => 昵称
[file_url] => 头像
[ts_status] => 推送接收状态（0：关闭，1：开启）*/
data class UserInfo(var id: Int, var account: String, var title: String, var file_url: String, var ts_status: Int)

data class UserInfoRes(val retRes: UserInfo) : RequestResult()

data class UploadInfo(val file_url: String)

data class UploadInfoRes(val retRes: UploadInfo) : RequestResult()

data class UploadInfoListRes(val retRes: ArrayList<String>) : RequestResult()

/*[id] => ID
[title] => 标题
[file_url] => 图片
[sub_title] => 简介
[biaoqian] => 标签（医龄：6年）
[zhuanye] => 专业（鱼类专家
[app_contents] => 详情（html代码）*/
data class YYZJInfo(val id: Int, val title: String, val file_url: String, val sub_title: String
                    , val biaoqian: String, val zhuanye: String, val app_contents: String, val tags: String)

data class YYZJInfoRes(val retRes: YYZJInfo) : RequestResult()

data class YYZJInfoListRes(val retRes: ArrayList<YYZJInfo>) : RequestResult()

/*[id] => ID
[title] => 标题
[sub_title] => 简介
[contents] => 详情
[create_time] => 时间（时间戳）
 [img_file_urls] => 图片数组['public/1.jpg','public/2.jpg']*/
data class FishLog(val id: Int, val title: String, val sub_title: String, val contents: String, val create_time: Long
                   , val file_url: String, val img_file_urls: ArrayList<String>)

data class FishLogRes(val retRes: FishLog) : RequestResult()

data class FishLogListRes(val retRes: ArrayList<FishLog>) : RequestResult()

/*[id] => ID
[title] => 标题
[file_url] => 图片
[sub_title] => 简介
[app_contents] => 详情（html代码）*/
data class FishKnowledge(val id: Int, val title: String, val file_url: String, val sub_title: String, val app_contents: String)

data class FishKnowledgeRes(val retRes: FishKnowledge) : RequestResult()

data class FishKnowledgeListRes(val retRes: ArrayList<FishKnowledge>) : RequestResult()

/*[id] => ID
[contents] => 内容
[tx_time] => 提醒时间戳
[create_time] => 创建时间戳*/
data class MemoSetInfo(val id: Int, val title: String, val contents: String, val tx_time: Long, val create_time: Long)

data class MemoSetInfoRes(val retRes: MemoSetInfo) : RequestResult()

data class MemoSetInfoListRes(val retRes: ArrayList<MemoSetInfo>) : RequestResult()


