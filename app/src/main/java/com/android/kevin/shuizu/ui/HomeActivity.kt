package com.android.kevin.shuizu.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.fragments.*
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import kotlinx.android.synthetic.main.activity_home.*


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class HomeActivity : MyBaseActivity(), BottomNavigationBar.OnTabSelectedListener {
    override fun onTabReselected(position: Int) {
    }

    override fun onTabUnselected(position: Int) {
    }

    override fun onTabSelected(position: Int) {
        loadFragment(position)
    }

    private val fragments = Array<Fragment?>(5){null}
    private var last = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
    }

    private fun init() {
        navigation.setTabSelectedListener(this)
        navigation.setFirstSelectedPosition(2)
        val item =
                navigation.addItem(BottomNavigationItem(R.mipmap.nav1a, getString(R.string.device)).setInactiveIconResource(R.mipmap.nav1))
                        .addItem(BottomNavigationItem(R.mipmap.nav2a, getString(R.string.intelligence)).setInactiveIconResource(R.mipmap.nav2))
                        .addItem(BottomNavigationItem(R.mipmap.nav5a, getString(R.string.service)).setInactiveIconResource(R.mipmap.nav5))
                        .addItem(BottomNavigationItem(R.mipmap.nav3a, getString(R.string.store)).setInactiveIconResource(R.mipmap.nav3))
                        .addItem(BottomNavigationItem(R.mipmap.nav4a, getString(R.string.mine)).setInactiveIconResource(R.mipmap.nav4))
                        .initialise()//所有的设置需在调用该方法前完成
        loadFragment(2)
    }

    fun loadPage(index: Int) {
        navigation.selectTab(index)
    }

    private fun loadFragment(position: Int) {
        val ft = supportFragmentManager.beginTransaction()
        if (fragments[position] == null) {
            fragments[position] = when (position) {
                0 -> DeviceFragment()
                1 -> IntelligenceFragment()
                2 -> ServiceFragment()
                3 -> StoreFragment()
                4 -> MineFragment()
                else -> MineFragment()
            }
            ft.add(R.id.content, fragments[position])
        }
        if(last != -1){
            ft.hide(fragments[last])
        }
        ft.show(fragments[position])
        last = position
        ft.commit()
    }

}