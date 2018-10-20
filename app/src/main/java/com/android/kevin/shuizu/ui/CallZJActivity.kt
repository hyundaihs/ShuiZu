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
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.GridDivider
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.dp2px
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.BottomDialog
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.utils.RoundTransform
import com.android.shuizu.myutillibrary.utils.ShowImageDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_call_zj.*
import kotlinx.android.synthetic.main.activity_create_fish_log.*
import kotlinx.android.synthetic.main.layout_callzj_image_item.view.*
import kotlinx.android.synthetic.main.layout_callzj_video_item.view.*
import kotlinx.android.synthetic.main.layout_create_fish_log_images.view.*
import kotlinx.android.synthetic.main.layout_take_photo.view.*
import org.jetbrains.anko.toast
import java.io.File

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/20/020.
 */
class CallZJActivity : MyBaseActivity() {

    val imageData = ArrayList<String>()
    val videoData = ArrayList<String>()
    private val imageAdapter = ImageAdapter(imageData)
    private val videoAdapter = VideoAdapter(videoData)

    companion object {
        private val maxImage = 9
        private val maxVideo = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_zj)
        initActionBar(this, "预约专家", rightBtn = "提交", rightClick = View.OnClickListener {
            submit()
        })
        initViews()
        getZjInfo(intent.getIntExtra("id", 0))
    }

    private fun initViews() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        callZjImages.layoutManager = gridLayoutManager
        //水平分割线
        callZjImages.addItemDecoration(GridDivider(this, dp2px(10f).toInt(), 3))
        callZjImages.itemAnimator = DefaultItemAnimator()
        callZjImages.adapter = imageAdapter
        callZjImages.isNestedScrollingEnabled = false

        callZjVideos.adapter = videoAdapter
        callZjVideos.isNestedScrollingEnabled = false

        imageAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                if (position == imageData.size && position < maxImage) {
//                    val mView = LayoutInflater.from(view.context).inflate(R.layout.layout_take_photo, null, false)
//                    val dialog = BottomDialog(mView)
//                    mView.capture.setOnClickListener {
//                        dialog.dismiss()
//                        MyOnClickListener().onClick(it)
//                    }
//                    mView.galley.setOnClickListener {
//                        dialog.dismiss()
//                        MyOnClickListener().onClick(it)
//                    }
//                    mView.cancel.setOnClickListener {
//                        dialog.dismiss()
//                        MyOnClickListener().onClick(it)
//                    }
                } else {
                    //ShowImageDialog(File(images[position]))
                }
            }
        }

        videoAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                if (position == videoData.size && position < maxVideo) {
//                    val mView = LayoutInflater.from(view.context).inflate(R.layout.layout_take_photo, null, false)
//                    val dialog = BottomDialog(mView)
//                    mView.capture.setOnClickListener {
//                        dialog.dismiss()
//                        MyOnClickListener().onClick(it)
//                    }
//                    mView.galley.setOnClickListener {
//                        dialog.dismiss()
//                        MyOnClickListener().onClick(it)
//                    }
//                    mView.cancel.setOnClickListener {
//                        dialog.dismiss()
//                        MyOnClickListener().onClick(it)
//                    }
                } else {
                    //ShowImageDialog(File(images[position]))
                }
            }
        }
    }
//
//    inner class MyOnClickListener : View.OnClickListener {
//        override fun onClick(v: View?) {
//            when (v?.id) {
//                R.id.capture -> {
//                    takePhoto.onPickFromCapture(Uri.fromFile(initFile()))
//                }
//                R.id.galley -> {
//                    takePhoto.onPickFromGallery()
//                }
//                R.id.cancel -> {
//
//                }
//            }
//        }
//
//    }

    private fun getZjInfo(id: Int) {
        val map = mapOf(Pair("id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val yYZJInfoRes = Gson().fromJson(result, YYZJInfoRes::class.java)
                refreshViews(yYZJInfoRes.retRes)
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

        }, true).postRequest(this as Context, YYZJ_INFO.getInterface(), map)
    }

    private fun refreshViews(zjInfo: YYZJInfo) {
        Picasso.with(this).load(zjInfo.file_url.getImageUrl()).into(callZjImage)
        callZjName.text = zjInfo.title
        callZjZhuanYe.text = zjInfo.zhuanye
        callZjAge.text = zjInfo.biaoqian
        callZjTitle.text = zjInfo.sub_title
    }

    private class ImageAdapter(val data: ArrayList<String>) : MyBaseAdapter(R.layout.layout_callzj_image_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            if (position >= data.size && position < maxImage) {
                holder.itemView.callzjImage.setImageResource(R.mipmap.add_device)
                holder.itemView.callzjDelete.visibility = View.GONE
            } else {
                val imageUrl = data[position]
                Picasso.with(holder.itemView.context).load(File(imageUrl)).resize(300, 300)
                        .transform(RoundTransform(holder.itemView.context)).into(holder.itemView.callzjImage)
                holder.itemView.callzjDelete.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int = if (data.size < maxImage) data.size + 1 else data.size
    }

    private class VideoAdapter(val data: ArrayList<String>) : MyBaseAdapter(R.layout.layout_callzj_video_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            if (position >= data.size && position < maxVideo) {
                holder.itemView.callzjVideo.setImageResource(R.mipmap.add_device)
                holder.itemView.callzjVideoDelete.visibility = View.GONE
            } else {
                val imageUrl = data[position]
                Picasso.with(holder.itemView.context).load(File(imageUrl)).resize(300, 300)
                        .transform(RoundTransform(holder.itemView.context)).into(holder.itemView.callzjVideo)
                holder.itemView.callzjVideoDelete.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int = if (data.size < maxVideo) data.size + 1 else data.size
    }


    private fun submit() {

    }
}