package com.android.kevin.shuizu.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.SZApplication
import com.android.kevin.shuizu.entities.App_Keyword
import com.android.kevin.shuizu.ui.LoginActivity
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.utils.CustomDialog
import com.android.shuizu.myutillibrary.utils.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class MineFragment : BaseFragment() {
    var login_verf: String by PreferenceUtil(SZApplication.instance, App_Keyword.LOGIN_VERF, "")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        exit.setOnClickListener {
            activity?.CustomDialog("提示", "确定要黜退登录吗？", positiveClicked = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    login_verf = ""
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }, negative = "取消")
        }
    }
}