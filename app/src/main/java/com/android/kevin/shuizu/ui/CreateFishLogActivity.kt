package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.TJ_YYRZ
import com.android.kevin.shuizu.entities.UPFILE_LISTS
import com.android.kevin.shuizu.entities.UploadInfoListRes
import com.android.kevin.shuizu.entities.getInterface
import com.android.kevin.shuizu.utils.SdCardUtil
import com.android.shuizu.myutillibrary.adapter.GridDivider
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.dp2px
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.BottomDialog
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.utils.RoundTransform
import com.android.shuizu.myutillibrary.utils.ShowImageDialog
import com.google.gson.Gson
import com.jph.takephoto.app.TakePhotoActivity
import com.jph.takephoto.model.TResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_fish_log.*
import kotlinx.android.synthetic.main.layout_create_fish_log_images.view.*
import kotlinx.android.synthetic.main.layout_take_photo.view.*
import org.jetbrains.anko.toast
import java.io.File


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/14/014.
 */
class CreateFishLogActivity : TakePhotoActivity(), View.OnClickListener {

    val images = ArrayList<String>()
    val imagesUpload = ArrayList<String>()
    private val mAdapter = MyImageAdapter(images)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_fish_log)
        initViews()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.page_back -> {
                finish()
            }
            R.id.publish -> {
                if (checkData()) {
                    uploadImages()
                }
            }
        }
    }

    private fun initViews() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        logImages.layoutManager = gridLayoutManager
        //水平分割线
        logImages.addItemDecoration(GridDivider(this, dp2px(10f).toInt(), 3))
        logImages.itemAnimator = DefaultItemAnimator()
        logImages.adapter = mAdapter
        logImages.isNestedScrollingEnabled = false
        mAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                if (position == images.size && position < 9) {
                    val mView = LayoutInflater.from(view.context).inflate(R.layout.layout_take_photo, null, false)
                    val dialog = BottomDialog(mView)
                    mView.capture.setOnClickListener {
                        dialog.dismiss()
                        MyOnClickListener().onClick(it)
                    }
                    mView.galley.setOnClickListener {
                        dialog.dismiss()
                        MyOnClickListener().onClick(it)
                    }
                    mView.cancel.setOnClickListener {
                        dialog.dismiss()
                        MyOnClickListener().onClick(it)
                    }
                } else {
                    ShowImageDialog(File(images[position]))
                }
            }
        }
    }

    private fun checkData(): Boolean {
        if (logTitle.text.isEmpty()) {
            logTitle.error = "日志标题不能为空"
            return false
        }
        if (logContent.text.isEmpty()) {
            logContent.error = "日志正文不能为空"
            return false
        }
        if (images.size <= 0) {
            toast("最少上传一张图片")
            return false
        }
        return true
    }

    private fun uploadImages() {
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val uploadInfoListRes = Gson().fromJson(result, UploadInfoListRes::class.java)
                imagesUpload.addAll(uploadInfoListRes.retRes)
                submit()
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

        }).uploadFile(this, UPFILE_LISTS.getInterface(), images)
    }

    private fun submit() {
        val map = mapOf(Pair("title", logTitle.text.toString())
                , Pair("contents", logContent.text.toString())
                , Pair("img_file_urls", imagesUpload)
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("发布成功")
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

        }).postRequest(this, TJ_YYRZ.getInterface(), map)
    }

    inner class MyOnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.capture -> {
                    takePhoto.onPickFromCapture(Uri.fromFile(initFile()))
                }
                R.id.galley -> {
                    takePhoto.onPickFromGallery()
                }
                R.id.cancel -> {

                }
            }
        }

    }

    private fun initFile(): File {
        val file = File(SdCardUtil.IMAGE + System.currentTimeMillis() + ".jpg")
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    override fun takeSuccess(result: TResult?) {
        super.takeSuccess(result)
        if (result == null) {
            toast("选取失败,请重新选取")
        } else {
            //userTemp.file_url = result.image.originalPath
            images.add(result.image.originalPath)
            //photo.setImageURI(Uri.fromFile(File(userTemp.file_url)))
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun takeFail(result: TResult?, msg: String?) {
        super.takeFail(result, msg)
        if (msg == null) {
            toast("选取失败,请重新选取")
        } else {
            toast(msg)
        }
    }

    override fun takeCancel() {
        super.takeCancel()
    }

    private class MyImageAdapter(val data: ArrayList<String>) : MyBaseAdapter(R.layout.layout_create_fish_log_images) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            if (position >= data.size && position < 9) {
                holder.itemView.image.setImageResource(R.mipmap.add_device)
                holder.itemView.delete.visibility = View.GONE
            } else {
                val images = data[position]
                Picasso.with(holder.itemView.context).load(File(images)).resize(300, 300)
                        .transform(RoundTransform(holder.itemView.context)).into(holder.itemView.image)
                holder.itemView.delete.visibility = View.VISIBLE
            }
            holder.itemView.delete.setOnClickListener {
                data.removeAt(position)
                notifyDataSetChanged()
            }
//            holder.itemView.fishItemTitle.text = fishKnowledge.title
//            holder.itemView.fishItemSubtitle.text = fishKnowledge.sub_title
        }

        override fun getItemCount(): Int = if (data.size >= 9) data.size else data.size + 1
    }
}