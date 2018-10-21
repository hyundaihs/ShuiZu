package com.android.kevin.shuizu.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.SZApplication
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.utils.SdCardUtil
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.BottomDialog
import com.android.shuizu.myutillibrary.utils.CustomDialog
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
//import com.jph.takephoto.app.TakePhotoActivity
//import com.jph.takephoto.model.CropOptions
//import com.jph.takephoto.model.TResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_change_user_info.*
import kotlinx.android.synthetic.main.layout_take_photo.view.*
import org.jetbrains.anko.toast
import java.io.File

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/9/009.
 */
class ChangeUserInfoActivity : MyBaseActivity() {

    lateinit var userTemp: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_info)
        userTemp = SZApplication.userInfo.copy()
        initActionBar(this, "修改信息")
        initViews()
    }

    private fun initViews() {
        Picasso.with(this).load(SZApplication.userInfo.file_url.getImageUrl()).into(photo)
        photo.setOnClickListener { itView ->
            PictureSelector.create(this@ChangeUserInfoActivity)
                    .openGallery(PictureMimeType.ofImage())
                    .enableCrop(true)
                    .compress(true)
                    .withAspectRatio(1, 1)
                    .minimumCompressSize(100)
                    .freeStyleCropEnabled(true)
                    .maxSelectNum(1)
                    //.minSelectNum(1)
                    .forResult(PictureConfig.CHOOSE_REQUEST)
        }
        name.setText(SZApplication.userInfo.title)
        warnMsg.isChecked = SZApplication.userInfo.ts_status == 1
        warnMsg.setOnCheckedChangeListener { buttonView, isChecked ->
            userTemp.ts_status = if (isChecked) 1 else 0
        }
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userTemp.title = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        submitUserInfo.setOnClickListener {
            submit()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun finish() {
        if (isSaved()) {
            super.finish()
        } else {
            CustomDialog("提示", "您做出的修改还未保存，是否保存？", "保存",
                    positiveClicked = DialogInterface.OnClickListener { _, _ ->
                        submit()
                    }, negative = "不保存",
                    negativeClicked = DialogInterface.OnClickListener { _, _ ->
                        super.finish()
                    })
        }
    }

    private fun isSaved(): Boolean {
        return userTemp == SZApplication.userInfo
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (selectList.size > 0) {
                        userTemp.file_url = selectList[0].compressPath
                        photo.setImageURI(Uri.fromFile(File(userTemp.file_url)))
                    }
                }
            }
        }
    }

    private fun submit() {
        if (userTemp.file_url.equals(SZApplication.userInfo.file_url)) {
            submitInfo()
        } else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        val list = ArrayList<String>()
        list.add(userTemp.file_url)
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val uploadInfoRes = Gson().fromJson(result, UploadInfoRes::class.java)
                userTemp.file_url = uploadInfoRes.retRes.file_url
                submitInfo()
            }

            override fun onError(context: Context, error: String) {
                context.toast(error)
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }
            //

        }, true).uploadFile(this, UPFILE.getInterface(), list)
    }

    private fun submitInfo() {
        val map = mapOf(Pair("title", userTemp.title), Pair("file_url", userTemp.file_url),
                Pair("ts_status", userTemp.ts_status.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("修改成功")
                SZApplication.userInfo = userTemp.copy()
                finish()
            }

            override fun onError(context: Context, error: String) {
                context.toast(error)
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, true).postRequest(this as Context, SET_INFO.getInterface(), map)
    }
}