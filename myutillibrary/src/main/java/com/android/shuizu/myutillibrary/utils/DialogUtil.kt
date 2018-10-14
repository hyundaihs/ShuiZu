package com.android.shuizu.myutillibrary.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import com.android.shuizu.myutillibrary.R
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.squareup.picasso.Picasso
import java.io.File


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/3/31/031.
 */

fun Context.LoginErrDialog(positiveClicked: DialogInterface.OnClickListener) {
    this.CustomDialog(message = "需要重新登录验证身份", positiveClicked = positiveClicked)
}

fun Any.MyProgressDialog(context: Context): AlertDialog {
    val builder = AlertDialog.Builder(context)
    val progressBar = ProgressBar(context)
    progressBar.setPadding(10, 10, 10, 10)
    builder.setView(progressBar).create()
    builder.setCancelable(false)
    return builder.show()
}

fun Context.CustomDialog(title: String = "提示", message: String, positive: String = "确定", positiveClicked: DialogInterface.OnClickListener? = null,
                         negative: String? = null, negativeClicked: DialogInterface.OnClickListener? = null, cancelable: Boolean = true): AlertDialog {
    val builder = AlertDialog.Builder(this)
            .setCancelable(cancelable)
            .setTitle(title)
            .setMessage(message)
    builder.setPositiveButton(positive, android.content.DialogInterface.OnClickListener { dialog, which ->
        positiveClicked?.onClick(dialog, which)
    })
    if (null != negative) {
        builder.setNegativeButton(negative, android.content.DialogInterface.OnClickListener { dialog, which ->
            negativeClicked?.onClick(dialog, which)
        })
    }
    return builder.show()
}

fun Context.ShowImageDialog(url: String) {
    val dialog = Dialog(this)
    dialog.setCancelable(true)
    val imageView = ImageView(this)
    imageView.setBackgroundColor(Color.TRANSPARENT)
    dialog.setContentView(imageView)
    Picasso.with(this).load(url).into(imageView)
    dialog.show()
}

fun Context.ShowImageDialog(file: File) {
    val dialog = Dialog(this)
    dialog.setCancelable(true)
    val imageView = ImageView(this)
    imageView.setBackgroundColor(Color.TRANSPARENT)
    dialog.setContentView(imageView)
    Picasso.with(this).load(file).into(imageView)
    dialog.show()
}

fun Context.BottomDialog(view: View): Dialog {
    val dialog = Dialog(this, R.style.MyDialog)
    dialog.setCancelable(true)
    dialog.setContentView(view)
    val window = dialog.getWindow()
    window.setGravity(Gravity.BOTTOM)
    val params = window.getAttributes()
    params.width = WindowManager.LayoutParams.MATCH_PARENT
    view.measure(0, 0);
    params.height = view.getMeasuredHeight()
    window.setAttributes(params)
    dialog.show()
    return dialog
}