package com.android.kevin.shuizu.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.fragments.DeviceFragment
import com.android.kevin.shuizu.fragments.IntelligenceFragment
import com.android.kevin.shuizu.fragments.MineFragment
import com.android.kevin.shuizu.fragments.StoreFragment
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
        loadFragment(fragments[position])
    }

    companion object {
        private val fragments = ArrayList<Fragment>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
    }

    private fun init() {
        navigation.setTabSelectedListener(this)
        navigation.addItem(BottomNavigationItem(R.mipmap.nav1a, getString(R.string.device)).setInactiveIconResource(R.mipmap.nav1))
                .addItem(BottomNavigationItem(R.mipmap.nav2a, getString(R.string.intelligence)).setInactiveIconResource(R.mipmap.nav2))
                .addItem(BottomNavigationItem(R.mipmap.nav3a, getString(R.string.store)).setInactiveIconResource(R.mipmap.nav3))
                .addItem(BottomNavigationItem(R.mipmap.nav4a, getString(R.string.mine)).setInactiveIconResource(R.mipmap.nav4))
                .initialise()//所有的设置需在调用该方法前完成

        fragments.add(DeviceFragment())
        fragments.add(IntelligenceFragment())
        fragments.add(StoreFragment())
        fragments.add(MineFragment())
        loadFragment(fragments[0])
    }

    private fun loadFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.content, fragment)
        ft.commit()
    }

}