package com.android.kevin.shuizu.entities

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/21/021.
 */
const val TEST_DEVICE_ID = "TR-005D16660016402020E42DE4"

const val ROOT_URL = "http://szx.yshdszx.com"
const val INTERFACE_INDEX = "/api.php/Index/"
const val FROM = "/from/android"
const val KEY_STR = "/keystr/defualtencryption"

fun String.getImageUrl(): String {
    return if (this.contains("http")) this else ROOT_URL + "/" + this
}

fun String.getInterface(): String {
    return ROOT_URL + INTERFACE_INDEX + this + FROM + KEY_STR
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


