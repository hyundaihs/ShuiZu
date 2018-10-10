package com.android.kevin.shuizu.ui

import android.net.Uri
import android.os.Bundle
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.utils.SdCardUtil
import com.android.shuizu.myutillibrary.D
import com.jph.takephoto.app.TakePhotoActivity
import com.jph.takephoto.model.CropOptions
import com.jph.takephoto.model.TResult
import kotlinx.android.synthetic.main.activity_my_take_photo.*
import org.jetbrains.anko.toast
import java.io.File

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/9/009.
 */
class MyTakePhotoActivity : TakePhotoActivity() {

    var newPhoto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_take_photo)
        initViews()
    }

    private fun initViews() {
        capture.setOnClickListener {
            val options = CropOptions.Builder().create()
            takePhoto.onPickFromCaptureWithCrop(Uri.fromFile(initFile()), options)
        }
        galley.setOnClickListener {
            val options = CropOptions.Builder().create()
            takePhoto.onPickFromGalleryWithCrop(Uri.fromFile(initFile()), options)
        }
        cancel.setOnClickListener {
            finish()
        }
        done.setOnClickListener {
            finish()
        }
    }

    private fun initFile():File{
        val file = File(SdCardUtil.IMAGE + System.currentTimeMillis() +".jpg")
        if(file.exists()){
            file.delete()
        }
        return file
    }

    override fun takeSuccess(result: TResult?) {
        super.takeSuccess(result)
        D("takeSuccess")
        if (result == null) {
            toast("选取失败,请重新选取")
        } else {
            newPhoto = result.image.originalPath
            photo.setImageURI(Uri.fromFile(File(newPhoto)))
        }
    }

    override fun takeFail(result: TResult?, msg: String?) {
        super.takeFail(result, msg)
        D("takeFail")
        if (msg == null) {
            toast("选取失败,请重新选取")
        } else {
            toast(msg)
        }
    }

    override fun takeCancel() {
        super.takeCancel()
        D("takeCancel")
    }
}