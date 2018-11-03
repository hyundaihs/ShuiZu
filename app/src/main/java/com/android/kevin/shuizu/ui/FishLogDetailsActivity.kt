package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_fish_log_details.*
import kotlinx.android.synthetic.main.layout_fish_log_details_images_item.view.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/14/014.
 */
class FishLogDetailsActivity : MyBaseActivity() {

    val images = ArrayList<String>()
    private val imageAdapter = ImageAdapter(images)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fish_log_details)
        val id = intent.getIntExtra("id", 0)
        val layoutManager = LinearLayoutManager(this)
        fishLogDetailsList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        //fishLogDetailsList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        fishLogDetailsList.itemAnimator = DefaultItemAnimator()
        fishLogDetailsList.adapter = imageAdapter
        fishLogDetailsList.isNestedScrollingEnabled = false
        getFishLogDetails(id)
    }

    private fun initViews(fishLog: FishLog) {
        initActionBar(this, fishLog.title)
        fishLogDetailsContent.text = fishLog.contents
        images.addAll(fishLog.img_file_urls)
        imageAdapter.notifyDataSetChanged()
    }

    private class ImageAdapter(val data: ArrayList<String>) : MyBaseAdapter(R.layout.layout_fish_log_details_images_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val image = data[position]
            Picasso.with(holder.itemView.context).load(image.getImageUrl())
                    .into(holder.itemView.detailsImagesItem)
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getFishLogDetails(id: Int) {
        val map = mapOf(Pair("id", id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val fishLogRes = Gson().fromJson(result, FishLogRes::class.java)
                initViews(fishLogRes.retRes)
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

        }).postRequest(this as Context, YYRZ_INFO.getInterface(Gson().toJson(map)), map)
    }
}