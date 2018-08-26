package com.android.kevin.shuizu.entities

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/21/021.
 */
const val TEST_DEVICE_ID = "TR-005D16660016402020E42DE4"

const val ROOT_URL = "http://szx.hyk001.com"
const val INTERFACE_INDEX = "/api.php/Index/"
const val FROM = "/from/android"
const val KEY_STR = "/keystr/defualtencryption"

fun Any.getInterface(interStr: String): String {
    return ROOT_URL + INTERFACE_INDEX + interStr + FROM + KEY_STR
}

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




