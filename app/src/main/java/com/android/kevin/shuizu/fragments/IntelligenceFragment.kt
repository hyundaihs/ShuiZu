package com.android.kevin.shuizu.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.android.kevin.shuizu.R
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_intelligence.*
import java.lang.reflect.Array.setBoolean
import java.lang.reflect.AccessibleObject.setAccessible



/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class IntelligenceFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        setOverflowShowingAlways()
        return inflater.inflate(R.layout.fragment_intelligence, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val fragmentList = ArrayList<Fragment>()
        val listTitle = arrayListOf("操作日志", "报警日志", "便捷设置")
        fragmentList.add(ActionLogFragment())
        fragmentList.add(WarnLogFragment())
        fragmentList.add(QuickSetFragment())
        viewpager.adapter = MyPagerAdapter(childFragmentManager, fragmentList, listTitle)
        tabLayout.setupWithViewPager(viewpager)
        viewpager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {

            }
        })
    }

    inner class MyPagerAdapter(fm: FragmentManager, val fragmentList: ArrayList<Fragment>, val listTitle: ArrayList<String>)
        : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return listTitle.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return listTitle[position]
        }

    }

    private fun setOverflowShowingAlways() {
        try {
            val config = ViewConfiguration.get(parentFragment!!.activity)
            val menuKeyField = ViewConfiguration::class.java
                    .getDeclaredField("sHasPermanentMenuKey")
            menuKeyField.setAccessible(true)
            menuKeyField.setBoolean(config, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}