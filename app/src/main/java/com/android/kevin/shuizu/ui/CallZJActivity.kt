package com.android.kevin.shuizu.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.GridDivider
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.dp2px
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.utils.RoundTransform
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_call_zj.*
import kotlinx.android.synthetic.main.layout_callzj_image_item.view.*
import kotlinx.android.synthetic.main.layout_callzj_video_item.view.*
import kotlinx.android.synthetic.main.layout_create_fish_log_images.view.*
import org.jetbrains.anko.toast
import java.io.File


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/20/020.
 */
class CallZJActivity : MyBaseActivity() {

    val imagesUpload = ArrayList<String>()
    val videosUpload = ArrayList<String>()
    val imageData = ArrayList<String>()
    val videoData = ArrayList<String>()
    private val imageAdapter = ImageAdapter(imageData)
    private val videoAdapter = VideoAdapter(videoData)
    private var id = 0

    companion object {
        private const val maxImage = 9
        private const val maxVideo = 1
        private const val IMAGE_REQUEST = 10
        private const val VIDEO_REQUEST = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_zj)
        initActionBar(this, "预约专家", rightBtn = "提交", rightClick = View.OnClickListener {
            if (check()) {
                uploadImages()
            }
        })
        id = intent.getIntExtra("id", 0)
        initViews()
        getZjInfo(id)
    }

    private fun check(): Boolean {
        if (callZjMessage.text.isEmpty()) {
            callZjMessage.error = "请填写留言信息"
            return false
        }
        if (callZjLinkMan.text.isEmpty()) {
            callZjLinkMan.error = "请填写联系姓名"
            return false
        }
        if (callZjLinkPhone.text.isEmpty()) {
            callZjLinkMan.error = "请填写联系电话"
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initViews() {
        val gridLayoutManager1 = GridLayoutManager(this, 3)
        callZjImages.layoutManager = gridLayoutManager1
        //水平分割线
        callZjImages.addItemDecoration(GridDivider(this, dp2px(10f).toInt(), 3))
        callZjImages.itemAnimator = DefaultItemAnimator()
        callZjImages.adapter = imageAdapter
        callZjImages.isNestedScrollingEnabled = false

        val gridLayoutManager2 = GridLayoutManager(this, 3)
        callZjVideos.layoutManager = gridLayoutManager2
        //水平分割线
        callZjVideos.addItemDecoration(GridDivider(this, dp2px(10f).toInt(), 3))
        callZjVideos.itemAnimator = DefaultItemAnimator()
        callZjVideos.adapter = videoAdapter
        callZjVideos.isNestedScrollingEnabled = false

        imageAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                if (position == imageData.size && position < maxImage) {
                    PictureSelector.create(this@CallZJActivity)
                            .openGallery(PictureMimeType.ofImage())
                            .enableCrop(true)
                            .compress(true)
                            .withAspectRatio(1, 1)
                            .minimumCompressSize(100)
                            .freeStyleCropEnabled(true)
                            .maxSelectNum(maxImage - imageData.size)
                            .minSelectNum(1)
                            .forResult(IMAGE_REQUEST)
                } else {
                    //ShowImageDialog(File(images[position]))
                }
            }
        }

        videoAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                if (position == videoData.size && position < maxVideo) {
                    PictureSelector.create(this@CallZJActivity)
                            .openGallery(PictureMimeType.ofVideo())
                            .maxSelectNum(maxVideo - videoData.size)
                            .videoMaxSecond(10)
                            .recordVideoSecond(10)
                            .forResult(VIDEO_REQUEST)
                } else {
                    //ShowImageDialog(File(images[position]))
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            when (requestCode) {
                IMAGE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    for (loc in selectList) {
                        imageData.add(loc.compressPath)
                    }
                    imageAdapter.notifyDataSetChanged()
                }
                VIDEO_REQUEST -> {
                    for (loc in selectList) {
                        videoData.add(loc.path)
                    }
                    videoAdapter.notifyDataSetChanged()
                }
            }
        }
    }

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

        }, true).postRequest(this as Context, YYZJ_INFO.getInterface(Gson().toJson(map)), map)
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
                Picasso.with(holder.itemView.context).load(File(data[position])).resize(300, 300)
                        .transform(RoundTransform(holder.itemView.context)).into(holder.itemView.callzjImage)
                holder.itemView.callzjDelete.visibility = View.VISIBLE
            }
            holder.itemView.callzjDelete.setOnClickListener {
                data.removeAt(position)
                notifyDataSetChanged()
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
                //通过ThumbnailUtils获取视频的第一帧。
                //第一个参数为我视频列表中最新一个视频的绝对路径
                //第二个分辨率设置，可用MINI_KIND或MICRO_KIND
                val tempBitmap = ThumbnailUtils.createVideoThumbnail(data[position], MediaStore.Images.Thumbnails.MINI_KIND)

                //将获取的bitmap进行压缩。第四个参数为回收资源。也有无第四个参数的方法
                val previewBitmap = ThumbnailUtils.extractThumbnail(tempBitmap, 300, 300, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
//                val imageUrl = data[position].path
//                Picasso.with(holder.itemView.context).load(File(imageUrl)).resize(300, 300)
//                        .transform(RoundTransform(holder.itemView.context)).into(holder.itemView.callzjVideo)
                holder.itemView.callzjVideo.setImageBitmap(previewBitmap)
                holder.itemView.callzjVideoDelete.visibility = View.VISIBLE
            }
            holder.itemView.callzjVideoDelete.setOnClickListener {
                data.removeAt(position)
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int = if (data.size < maxVideo) data.size + 1 else data.size
    }


    private fun uploadImages() {
        if (imageData.size <= 0) {
            uploadVideos()
            return
        }
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val uploadInfoListRes = Gson().fromJson(result, UploadInfoListRes::class.java)
                imagesUpload.addAll(uploadInfoListRes.retRes)
                D("图片上传成功${imagesUpload}")
                uploadVideos()
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
        }).uploadFile(this, UPFILE_LISTS.getInterface(""), imageData)
    }

    private fun uploadVideos() {
        if (videoData.size <= 0) {
            submit()
            return
        }
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val uploadInfoListRes = Gson().fromJson(result, UploadInfoListRes::class.java)
                videosUpload.addAll(uploadInfoListRes.retRes)
                D("视频上传成功${videosUpload}")
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
        }).uploadFile(this, UPFILE_LISTS.getInterface(""), videoData)
    }

    private fun submit() {
        val map = mapOf(Pair("yyzj_id", id.toString())
                , Pair("link_man", callZjLinkMan.text.toString())
                , Pair("link_phone", callZjLinkPhone.text.toString())
                , Pair("contents", callZjMessage.text.toString())
                , Pair("img_file_urls", imagesUpload)
                , Pair("video_file_urls", videosUpload)
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("发布成功")
                //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
                PictureFileUtils.deleteCacheDirFile(context)
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

        }).postRequest(this, TJYYXX.getInterface(Gson().toJson(map)), map)
    }
}