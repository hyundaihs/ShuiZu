package com.android.shuizu.myutillibrary

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.DefaultRationale
import com.android.shuizu.myutillibrary.utils.PermissionSetting
import com.android.shuizu.myutillibrary.utils.WxUtil
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.toast


/**
 * LeZu
 * Created by 蔡雨峰 on 2018/1/4.
 */

fun Any.D(text: String) {
    Log.d("${this.javaClass.simpleName} ", text)
}

fun Any.E(text: String) {
    Log.e("${this.javaClass.simpleName} ", text)
}

fun Any.I(text: String) {
    Log.i("${this.javaClass.simpleName} ", text)
}

fun Any.W(text: String) {
    Log.w("${this.javaClass.simpleName} ", text)
}

fun Any.V(text: String) {
    Log.v("${this.javaClass.simpleName} ", text)
}

val mPermissions = arrayOf(Permission.READ_EXTERNAL_STORAGE,
        Permission.WRITE_EXTERNAL_STORAGE,
        Permission.ACCESS_COARSE_LOCATION,
        Permission.ACCESS_FINE_LOCATION,
        Permission.CAMERA
)

fun Context.dp2px(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.sp2px(sp:Float):Float{
    return TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
}

fun AppCompatActivity.initActionBar(act: AppCompatActivity, title: String, showBack: Boolean = true, rightBtn: String? = null, isAdd: Boolean = false,
                                    rightClick: View.OnClickListener? = null, leftBtn: String? = null,
                                    leftClick: View.OnClickListener? = null) {
    act.setSupportActionBar(toolbar)
    act.supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
    act.toolbarTitle.text = title
    act.title = ""
    if (leftBtn != null) {
        act.toolbarLeftBtn.visibility = View.VISIBLE
        act.toolbarLeftBtn.text = leftBtn
        act.toolbarLeftBtn.setOnClickListener(leftClick)
    } else {
        act.toolbarLeftBtn.visibility = View.GONE
    }
    if (rightBtn != null) {
        act.toolbarRightBtn.visibility = View.VISIBLE
        act.toolbarRightBtn.text = rightBtn
        act.toolbarRightBtn.setOnClickListener(rightClick)
    } else {
        act.toolbarRightBtn.visibility = View.GONE
        if (rightClick != null) {
            if (isAdd) {
                act.toolbarRightImg.setImageResource(R.mipmap.icon_add)
            } else {
                act.toolbarRightImg.setImageResource(R.mipmap.icon_set)
            }
            act.toolbarRightImg.visibility = View.VISIBLE
            act.toolbarRightImg.setOnClickListener(rightClick)
        } else {
            act.toolbarRightImg.visibility = View.GONE
        }
    }
}

fun Context.requestPermission(onAccepted: ((permissions: Array<String>) -> Unit)? = null,
                              onDenied: ((permissions: List<String>) -> Unit)? = null) {
    val mRationale = DefaultRationale()
    val mSetting = PermissionSetting(this)
    AndPermission.with(this)
            .permission(mPermissions)
            .rationale(mRationale)
            .onGranted(Action {
                if (null != onAccepted) {
                    onAccepted(mPermissions)
                } else {
                    toast("权限获取成功")
                }
            })
            .onDenied(Action { permissions ->
                if (AndPermission.hasAlwaysDeniedPermission(this, permissions)) {
                    mSetting.showSetting(permissions, DialogInterface.OnClickListener { _, _ ->
                        if (null != onDenied) {
                            onDenied(permissions)
                        }
                    })
                } else {
                    if (null != onDenied) {
                        onDenied(permissions)
                    } else {
                        toast("权限获取失败")
                    }
                }
            })
            .start()
}

val THUMB_SIZE = 50

fun IWXAPI.sendBitmapToWx(bmp: Bitmap, isTimeLine: Boolean): Boolean {

    val imgObj = WXImageObject(bmp)

    val msg = WXMediaMessage()
    msg.mediaObject = imgObj

    val thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true)
    msg.thumbData = WxUtil.bmpToByteArray(thumbBmp, true)

    val req = SendMessageToWX.Req()
    req.scene = if (isTimeLine) SendMessageToWX.Req.WXSceneTimeline else SendMessageToWX.Req.WXSceneSession
    req.transaction = System.currentTimeMillis().toString()
    req.message = msg

    return sendReq(req)
}


fun IWXAPI.sendToWx(goodsName: String, price1: Double, price2: Double, url: String = "", bmp: Bitmap, isTimeLine: Boolean): Boolean {

    val wxWebpageObject = WXWebpageObject()
    wxWebpageObject.webpageUrl = url
    val msg = WXMediaMessage(wxWebpageObject)
    msg.mediaObject = wxWebpageObject
    msg.title = "乐租精品-$goodsName"
    msg.description = "乐租联盟店精心为您推荐：\n商品价:￥$price2\n租赁最低至:￥$price1"

    val thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true)
    msg.thumbData = WxUtil.bmpToByteArray(thumbBmp, true)

    val req = SendMessageToWX.Req()
    req.scene = if (isTimeLine) SendMessageToWX.Req.WXSceneTimeline else SendMessageToWX.Req.WXSceneSession
    req.transaction = System.currentTimeMillis().toString()
    req.message = msg

    return sendReq(req)
}

fun IWXAPI.sendWyToWx(bmp: Bitmap, isTimeLine: Boolean): Boolean {
    val wxWebpageObject = WXWebpageObject()
    wxWebpageObject.webpageUrl = "https://fir.im/sd4w"
    val wxMediaMessage = WXMediaMessage(wxWebpageObject)
    wxMediaMessage.mediaObject = wxWebpageObject
    wxMediaMessage.title = "茗品雅汇"
    wxMediaMessage.description = "我们不仅仅是商城，更是游戏把玩的福地，购买即可中大奖！"

    val thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true)
    wxMediaMessage.thumbData = WxUtil.bmpToByteArray(thumbBmp, true)

    val req = SendMessageToWX.Req()
    req.transaction = System.currentTimeMillis().toString()
    req.message = wxMediaMessage
    req.scene = if (isTimeLine) SendMessageToWX.Req.WXSceneTimeline else SendMessageToWX.Req.WXSceneSession
    return sendReq(req)
}

fun Any.getTime(time: Long): String {
    val ca = CalendarUtil(time * 1000)
    return ca.format(CalendarUtil.STANDARD)
}
