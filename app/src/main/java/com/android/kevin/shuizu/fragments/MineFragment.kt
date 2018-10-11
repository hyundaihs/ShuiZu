package com.android.kevin.shuizu.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.SZApplication
import com.android.kevin.shuizu.SZApplication.Companion.userInfo
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.ui.ChangeUserInfoActivity
import com.android.kevin.shuizu.ui.LoginActivity
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CustomDialog
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.utils.PreferenceUtil
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_mine.*
import org.jetbrains.anko.toast

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
        initViews()
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
    }

    private fun initViews() {
        changeUserInfo.setOnClickListener {
            startActivity(Intent(it.context, ChangeUserInfoActivity::class.java))
        }
        exit.setOnClickListener {
            activity?.CustomDialog("提示", "确定要退出登录吗？", positiveClicked = DialogInterface.OnClickListener { p0, p1 ->
                login_verf = ""
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }, negative = "取消")
        }
    }

    private fun getUserInfo() {
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val userInfoRes = Gson().fromJson(result, UserInfoRes::class.java)
                userInfo = userInfoRes.retRes
                Picasso.with(context).load(userInfo.file_url.getImageUrl()).into(mineImage)
                mineName.text = userInfo.title
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

        }, false).postRequest(activity as Context, USER_INFO.getInterface())
    }
}