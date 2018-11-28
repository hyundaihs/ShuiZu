package com.android.kevin.shuizu.entities

import android.text.TextUtils
import android.util.Log
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/21/021.
 */
const val TEST_DEVICE_ID = "TR-005D16660016402020E42DE4"

const val ROOT_URL = "http://szx.yshdszx.com"
const val INTERFACE_INDEX = "/api.php/Index/"
const val FROM = "/from/android"
const val KEY_STR = "/keystr/"

fun String.getImageUrl(): String {
    return if (this.contains("http")) this else ROOT_URL + "/" + this
}

fun String.getInterface(jsonStr: String): String {
    val keyStr = getKeyStr(jsonStr, this)
    Log.d("md",keyStr)
    return ROOT_URL + INTERFACE_INDEX + this + FROM + KEY_STR + keyStr
}

fun getKeyStr(jsonStr: String, inter: String): String {
    return md5(jsonStr + "nimdaae" + inter + CalendarUtil(System.currentTimeMillis()).format(CalendarUtil.YYYYMMDD))
}

private fun md5(string: String): String {
    if (TextUtils.isEmpty(string)) {
        return ""
    }
    val md5: MessageDigest = MessageDigest.getInstance("MD5")
    val bytes = md5.digest(string.toByteArray())
    var result = ""
    val c = 0xff
    for (i in bytes.indices) {
        var temp = Integer.toHexString(c and bytes[i].toInt())
        if (temp.length == 1) {
            temp = "0$temp"
        }
        result += temp
    }
    return result
}

const val UPFILE = "upfile"//文件上传
const val UPFILE_LISTS = "upfilelists"//文件上传
/*phone：手机号码*/
const val SEND_VERF = "sendverf" //发送短信验证码

/*account：手机号码
verf:短信验证码
password：密码*/
const val REG = "reg" //注册

/*account：手机号码
password：密码*/
const val LOGIN = "login" //登录

/*login_verf：自动登录密码*/
const val VERF_LOGIN = "verflogin" //自动登录密码登录

/*account：手机号
verf：短信验证码
password：密码*/
const val RESET_PASS = "resetpass" //修改密码

const val USER_INFO = "userinfo" //用户信息

/*title：昵称
file_url：头像文件路径（为空时不修改，通过文件上传接口得到）
ts_status:推送接收状态（0：关闭，1：开启）*/
const val SET_INFO = "setinfo" //修改用户信息

const val SYS_INFO = "sysinfo" //获取系统介绍信息

const val INDEX_INFO = "indexinfo" //获取首页时间

const val YG_LISTS = "yglists" //鱼缸列表

/*id：鱼缸id*/
const val YG_INFO = "yginfo" //鱼缸详情

/*acccardtype_id：分组id（0为常用设备）*/
const val YGSB = "ygsb" //我的设备列表

/*id：设备id*/
const val SZJC_SSSJ = "szjcsssj" //水质监测实时数据

/*id：设备id
data_type:类型（wd：温度，ph:PH，tds：TDS）
days：日期（20180507）*/
const val SZJC_TJTSJ = "szjctjtsj" //水质监测统计图数据

/*acccardtype_id：分组id（0为常用设备）
ids：设备id数组（[1,2,3]）*/
const val TJSB_DYG = "tjsbdyg" //水质监测统计图数据

const val SCSB = "scsb"//删除设备

/*id：鱼缸id*/
const val SCYG = "scyg" //删除鱼缸

/*title:名称*/
const val TJYG = "tjyg" //添加鱼缸

/*id：鱼缸id
title:名称*/
const val XGYG = "xgyg" //修改鱼缸

/*id：设备id
ph_value：PH值（大于等于2并且小于等于12  保留2位小数）
num：次数（1：第一次，2：第二次  第一次校准成功后发送第二次校准，两次校准的值不能相等）*/
const val PHJZ = "phjz" //发送ph校准

/*id：设备id*/
const val PHJZ_INFO = "phjzinfo" //当前PH校准状态

/*id：设备id*/
const val BJWD_INFO = "bjwdinfo" //温度报警详情

/*id：设备id
v_1：极度低温值,
v_2：低温值,
v_3：高温值,
v_4：极度高温值*/
const val SZ_BJWD = "szbjwd" //设置报警温度

/*id：设备id*/
const val PH_INFO = "phinfo" //PH报警详情

/*id：设备id
v_1：低,
v_2：高*/
const val SZ_BJPH = "szbjph" //设置报警PH值

/*id：设备id*/
const val TDS_INFO = "tdsinfo" //TDS报警详情

/*id：设备id
v_1：报警值*/
const val SZ_BJTDS = "szbjtds" //设置报警TDS值

/*id：设备id*/
const val KG_INFO = "kginfo" //报警开关详情

/*id：设备id
v_1：开关值（0：关，1：开）*/
const val SZ_BJKG = "szbjkg" //设置报警开关

const val NEW_LOG = "newlog" //最新日志（5分钟内)

const val NEW_NEWS = "newnews" //最新消息（5分钟内)

/*title:搜索关键词
page：页数
page_size:每页大小（默认20）*/
const val NEWS = "news" //消息列表

/*id：消息id*/
const val NEWS_INFO = "newsinfo" //消息详情

/*page:页数
page_size：每页条数(默认20)
type_id:类型（1:报警，2：操作）*/
const val BJ_LOG = "bjlog" //设备日志

/*acccardtype_id：分组id（0为常用设备）
card_id：设备编号*/
const val BDSB = "bdsb" //绑定设备

/*page：页数
page_size:每页大小（默认20）*/
const val YYZJ = "yyzj" //养鱼专家列表

/*id：专家id*/
const val YYZJ_INFO = "yyzjinfo" //养鱼专家详情

/*yyzj_id:专家id
link_man：联系人
link_phone：联系电话
contents：详情
img_file_urls：图片列表（['public/1.jpg','public/2.jpg']）地址从上传文件接口得到
video_file_urls:视频列表（['public/1.MP4','public/2.MP4']）地址从上传文件接口得到*/
const val TJYYXX = "tjyyxx"//提交预约信息

/*status:状态（0：所有，1：未处理，2：已完成，3：取消/作废）*/
const val WDYYLB = "wdyylb"//我的预约列表

/*id：预约ID*/
const val QXWDYY = "qxwdyy"//取消我的预约

/*id：预约ID*/
const val SCWDYY = "scwdyy"//取消我的预约

/*page：页数
page_size:每页大小（默认20）*/
const val YLZS = "ylzs" //鱼类知识列表

/*id：鱼类知识id*/
const val YLZS_INFO = "ylzsinfo" //鱼类知识详情

/*page：页数
page_size:每页大小（默认20）*/
const val YYRZ = "yyrz" //养鱼日志列表

/*id：养鱼日志id*/
const val YYRZ_INFO = "yyrzinfo" //养鱼日志详情

/*title：标题
contents：内容*/
const val TJ_YYRZ = "tjyyrz" //发布养鱼日志

/*id：养鱼日志id*/
const val DEL_YYRZ = "delyyrz" //删除养鱼日志

/*lng：经度
lat：纬度
title:搜索关键词
page：页数
page_size:每页大小（默认20）*/
const val WANGDIAN = "wangdian" //网点列表

/*id：网点id
lng：经度
lat：纬度*/
const val WANGDIAN_INFO = "wangdianinfo" //网点详情

/*page：页数
page_size:每页大小（默认20）*/
const val BWXX = "bwxx" //备忘信息列表

/*contents：内容
tx_time:提醒时间（2018-09-07 15:30:00  为空则不提醒）*/
const val TJBWXX = "tjbwxx" //提交备忘信息

/*id：备忘信息id*/
const val BWXX_INFO = "bwxxinfo" //备忘信息详情

/*id：备忘信息id*/
const val BWXX_DEL = "bwxxdel" //备忘信息详情

const val ZNTJ = "zntj" //智能统计

const val CZSM = "czsm" //操作说明列表

/*id：操作说明id*/
const val CZSM_INFO = "czsminfo" //操作说明详情

/*id：设备id*/
const val SBKGJ_INFO = "sbkgjinfo"//水泵开关详情

/*
id：设备id
v_1：开关值（0：关，1：开）*/
const val SBKGJ = "sbkgj"//设置水泵开关机（水泵）

/*id：设备id*/
const val SBYX_INFO = "sbyxinfo"//水泵运行档位设置详情（水泵）

/*id：设备id
v_1：档位值（1-100）*/
const val SBYX = "sbyx"//设置水泵运行档位（水泵）

/*id：设备id*/
const val SBZL_INFO = "sbzlinfo"//水泵造浪设置详情（水泵）

/*id：设备id
v_1：档位值（1-100）*/
const val SBZL = "sbzl"//设置水泵造浪（水泵）

/*id：设备id*/
const val SBTS_INFO = "sbtsinfo"//水泵投食模式设置详情（水泵）

/*id：设备id
v_1：定时高位（10-135）
v_2：定时低位（10-135）*/
const val SBTS = "sbts"//设置水泵投食模式（水泵）

/*id：设备id*/
const val SBYXMS_INFO = "sbyxmsinfo" //水泵工作模式详情

/**
 * id：设备id
v_1：模式（1：投食，2：运行，3：造浪）
 */
const val SBYXMS = "sbyxms" //水泵工作模式设置

