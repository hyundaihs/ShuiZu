package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.fragments.ReservationListFragment
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.DisplayUtils
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_instructions.*
import kotlinx.android.synthetic.main.activity_my_reservations.*
import kotlinx.android.synthetic.main.layout_callzj_video_item.view.*
import kotlinx.android.synthetic.main.layout_my_reserations_list_item.view.*
import kotlinx.android.synthetic.main.layout_small_images_item.view.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/25/025.
 */
class MyReservationsActivity : MyBaseActivity() {

    private var list = ArrayList<BaseFragment>()
    private val titles = arrayOf("未处理", "已完成", "已作废")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reservations)
        initViews()
    }

    private fun initViews(){
        list = java.util.ArrayList()
        val res1 = ReservationListFragment()
        val bundle1 = Bundle()
        bundle1.putInt("index",1)
        res1.arguments = bundle1
        list.add(res1)
        val res2 = ReservationListFragment()
        val bundle2 = Bundle()
        bundle2.putInt("index",2)
        res2.arguments = bundle2
        list.add(res2)
        val res3 = ReservationListFragment()
        val bundle3 = Bundle()
        bundle3.putInt("index",3)
        res3.arguments = bundle3
        list.add(res3)
        //ViewPager的适配器
        val adapter = FragmentsAdapter(supportFragmentManager, list, titles)
        reservationViewPager.adapter = adapter
        //绑定
        reservationTabLayout.setupWithViewPager(reservationViewPager)
    }

    private inner class FragmentsAdapter(fm: FragmentManager, private val list: List<BaseFragment>, private val titles: Array<String>)
        : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return list[position]
        }

        override fun getCount(): Int {
            return list.size
        }

        //重写这个方法，将设置每个Tab的标题
        override fun getPageTitle(position: Int): CharSequence {
            return if (position < titles.size) titles[position] else ""
        }
    }


}