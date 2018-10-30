package com.android.kevin.shuizu.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.ui.GalleryActivity
import com.android.kevin.shuizu.ui.InstructionsDetailsActivity
import com.android.kevin.shuizu.ui.LoginActivity
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.DisplayUtils
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_reservation_list.*
import kotlinx.android.synthetic.main.layout_callzj_video_item.view.*
import kotlinx.android.synthetic.main.layout_my_reserations_list_item.view.*
import kotlinx.android.synthetic.main.layout_small_images_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/26/026.
 */
class ReservationListFragment : BaseFragment() {

    private val mData = ArrayList<Reservations>()
    private val mAdapter = InstructionsAdapter(mData)
    private var index = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reservation_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            index = arguments!!.getInt("index")
        }
        initViews()
        getReservationsList()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(activity)
        myReservaList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        myReservaList.itemAnimator = DefaultItemAnimator()
        myReservaList.isNestedScrollingEnabled = false
        myReservaList.adapter = mAdapter
        mAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                if (mData[position].video_file_urls.size + mData[position].img_file_urls.size > 0) {
                    val temp = ArrayList<String>()
                    temp.addAll(mData[position].video_file_urls)
                    temp.addAll(mData[position].img_file_urls)
                    val intent = Intent(view.context, GalleryActivity::class.java)
                    intent.putStringArrayListExtra("urls", temp)
                    intent.putExtra("hasVideo", mData[position].video_file_urls.size > 0)
                    startActivity(intent)
                } else {
                    view.context.toast("没有可供查看的图片或视频")
                }
            }

        }
    }

    private fun getReservationsList() {
        val map = mapOf(Pair("status", index.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val reservationsListRes = Gson().fromJson(result, ReservationsListRes::class.java)
                mData.clear()
                mData.addAll(reservationsListRes.retRes)
                mAdapter.notifyDataSetChanged()
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

        }, false).postRequest(activity as Context, WDYYLB.getInterface(), map)
    }

    private inner class InstructionsAdapter(val data: ArrayList<Reservations>) : MyBaseAdapter(R.layout.layout_my_reserations_list_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val reservations = data[position]
            holder.itemView.reserationItemNum.text = "预约编号:${reservations.numbers}"
            holder.itemView.reserationItemTime.text = CalendarUtil(reservations.create_time, true).format(CalendarUtil.YYYY_MM_DD_HH_MM)
            Picasso.with(holder.itemView.context).load(reservations.yyzj_file_url.getImageUrl()).into(holder.itemView.reserationItemPhoto)
            holder.itemView.reserationItemName.text = reservations.yyzj_title
            holder.itemView.reserationItemContent.text = reservations.contents
            holder.itemView.reserationItemCancel.visibility = if (index == 1) View.VISIBLE else View.GONE
            holder.itemView.reserationItemCancel.setOnClickListener {
                cancelReservation(position)
            }
            val layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.itemView.reserationItemImages.layoutManager = layoutManager
            layoutManager.orientation = OrientationHelper.HORIZONTAL
            holder.itemView.reserationItemImages.itemAnimator = DefaultItemAnimator()
            holder.itemView.reserationItemImages.isNestedScrollingEnabled = false
            val isFirstVideo = reservations.video_file_urls.size > 0
            val temp = ArrayList<String>()
            temp.addAll(reservations.video_file_urls)
            temp.addAll(reservations.img_file_urls)
            holder.itemView.reserationItemImages.adapter = ImageAdapter(temp, isFirstVideo)
        }

        override fun getItemCount(): Int = data.size

        private inner class ImageAdapter(val imgData: ArrayList<String>, val isFirstVideo: Boolean) : MyBaseAdapter(R.layout.layout_small_images_item) {

            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                val path = imgData[position]
                if (isFirstVideo && position == 0) {
                    val temp = DisplayUtils.dp2px(holder.itemView.context, 80f)
                    holder.itemView.context.doAsync {
                        val tempBitmap = createVideoThumbnail(path.getImageUrl(), temp, temp)
                        uiThread {
                            if (null != tempBitmap) {
                                holder.itemView.smallImage.setImageBitmap(tempBitmap)
                            } else {
                                holder.itemView.smallImage.setImageResource(R.mipmap.ic_launcher)
                            }
                        }
                    }

                } else {
                    Picasso.with(holder.itemView.context).load(path.getImageUrl()).into(holder.itemView.smallImage)
                }
            }

            override fun getItemCount(): Int = imgData.size
        }


        private fun createVideoThumbnail(url: String, width: Int, height: Int): Bitmap? {
            var bitmap: Bitmap? = null
            val retriever = MediaMetadataRetriever()
            val kind = MediaStore.Video.Thumbnails.MINI_KIND
            try {
                retriever.setDataSource(url, HashMap<String, String>())
                bitmap = retriever.getFrameAtTime()
            } catch (ex: IllegalArgumentException) {
                // Assume this is a corrupt video file
            } catch (ex: RuntimeException) {
                // Assume this is a corrupt video file.
            } finally {
                try {
                    retriever.release()
                } catch (ex: RuntimeException) {
                    // Ignore failures while cleaning up.
                }
            }
            if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
            }
            return bitmap
        }

    }

    private fun cancelReservation(pos: Int) {
        val map = mapOf(Pair("id", mData[pos].id.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                mData.removeAt(pos)
                mAdapter.notifyDataSetChanged()
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

        }, false).postRequest(activity as Context, QXWDYY.getInterface(), map)
    }

}