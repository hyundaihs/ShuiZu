package com.android.kevin.shuizu.ui

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.getImageUrl
import com.android.kevin.shuizu.utils.SdCardUtil
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.layout_gallery_image.view.*
import kotlinx.android.synthetic.main.layout_video_play.view.*
import java.io.File

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/30/030.
 */
class GalleryActivity : MyBaseActivity() {

    val pagerView = ArrayList<View>()
    var hasVideo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        val list = intent.getStringArrayListExtra("urls")
        hasVideo = intent.getBooleanExtra("hasVideo", false)
        if (null != list) {
            initViews(list)
        }
    }

    private fun initViews(urls: ArrayList<String>) {
        for (i in 0 until urls.size) {
            if (hasVideo && i == 0) {
                val view = layoutInflater.inflate(R.layout.layout_video_play, null)
                val path = SdCardUtil.IMAGE + urls[i].substring(urls[i].indexOfLast { it == '/' } + 1)
                if (File(path).exists()) {
                    initVideo(view, path)
                } else {
                    MySimpleRequest().downLoadFile(urls[i].getImageUrl(), path, object : MySimpleRequest.ReqProgressCallBack {
                        override fun onFailed(e: String) {
                            view.videoLoading.visibility = View.GONE
                            view.videoCurr.text = "视频加载失败"
                        }

                        override fun onSuccess(file: File) {
                            initVideo(view, file.path)
                        }

                        override fun onProgress(total: Long, current: Long) {
                            view.videoCurr.text = "${current}%"
                        }
                    })
                }
                pagerView.add(view)
            } else {
                val view = layoutInflater.inflate(R.layout.layout_gallery_image, null)
                Picasso.with(this).load(urls[i].getImageUrl()).into(view.galleryImg)
                pagerView.add(view.galleryImg)
            }
        }
        gallery.adapter = MyPagerAdapter()
    }

    private fun initVideo(view: View, path: String) {
        view.videoView.setVideoPath(path)
        view.videoStart.setOnClickListener {
            if (view.videoView.isPlaying) {
                view.videoView.pause()
                view.videoStart.visibility = View.VISIBLE
            } else {
                view.videoView.start()
                view.videoStart.visibility = View.GONE
            }
        }
        view.videoView.setOnPreparedListener {
            view.videoView.start()
            view.videoView.pause()
            view.videoLoading.visibility = View.GONE
            view.videoCurr.visibility = View.GONE
            view.videoStart.visibility = View.VISIBLE
        }
        view.videoView.setOnCompletionListener {
            view.videoView.start()
            view.videoView.pause()
            view.videoStart.visibility = View.VISIBLE
        }
        view.videoView.start()
    }

    private inner class MyPagerAdapter : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            (container as ViewPager).addView(pagerView[position])
            return pagerView[position]
        }

        override fun getCount(): Int {
            return pagerView.size
        }

    }
}