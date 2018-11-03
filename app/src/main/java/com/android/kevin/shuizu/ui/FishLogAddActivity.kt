package com.android.kevin.shuizu.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.TJ_YYRZ
import com.android.kevin.shuizu.entities.UPFILE_LISTS
import com.android.kevin.shuizu.entities.UploadInfoListRes
import com.android.kevin.shuizu.entities.getInterface
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
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
//import com.jph.takephoto.app.TakePhotoActivity
//import com.jph.takephoto.model.TResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_fish_log.*
import kotlinx.android.synthetic.main.layout_create_fish_log_images.view.*
import org.jetbrains.anko.toast
import java.io.File


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/14/014.
 */
class FishLogAddActivity : MyBaseActivity(){

    val images = ArrayList<String>()
    val imagesUpload = ArrayList<String>()
    private val mAdapter = MyImageAdapter(images)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_fish_log)
        initActionBar(this,"发布日志",rightBtn = "发布",rightClick = View.OnClickListener {
            if (checkData()) {
                uploadImages()
            }
        })
        initViews()
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
                    PictureSelector.create(this@FishLogAddActivity)
                            .openGallery(PictureMimeType.ofImage())
                            .enableCrop(true)
                            .compress(true)
                            .withAspectRatio(1, 1)
                            .minimumCompressSize(100)
                            .freeStyleCropEnabled(true)
                            .maxSelectNum(9)
                            //.minSelectNum(1)
                            .forResult(PictureConfig.CHOOSE_REQUEST)
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
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    for (loc in selectList) {
                        images.add(loc.compressPath)
                    }
                    mAdapter.notifyDataSetChanged()
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

        }).uploadFile(this, UPFILE_LISTS.getInterface(""), images)
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

        }).postRequest(this, TJ_YYRZ.getInterface(Gson().toJson(map)), map)
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