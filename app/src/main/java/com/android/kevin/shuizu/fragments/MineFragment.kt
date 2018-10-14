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
import com.android.kevin.shuizu.ui.*
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
class MineFragment : BaseFragment(),View.OnClickListener {
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
        getYYZJ()
    }

    private fun initViews() {
        changeUserInfo.setOnClickListener(this)
        yuYueMore.setOnClickListener(this)
        exit.setOnClickListener(this)
        fishLogBtn.setOnClickListener(this)
        fishKnowledgeBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.changeUserInfo->{
                startActivity(Intent(context, ChangeUserInfoActivity::class.java))
            }
            R.id.yuYueMore->{
                startActivity(Intent(context, ReservationActivity::class.java))
            }
            R.id.exit->{
                activity?.CustomDialog("提示", "确定要退出登录吗？", positiveClicked = DialogInterface.OnClickListener { p0, p1 ->
                    login_verf = ""
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }, negative = "取消")
            }
            R.id.fishLogBtn->{
                startActivity(Intent(context, FishLogListActivity::class.java))
            }
            R.id.fishKnowledgeBtn->{
                startActivity(Intent(context, FishKnowledgeListActivity::class.java))
            }
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

    //获取养鱼专家列表
    private fun getYYZJ() {
        val map = mapOf(Pair("page","1"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val yYZJInfoListRes = Gson().fromJson(result, YYZJInfoListRes::class.java)
                val list = yYZJInfoListRes.retRes
                if (list.size > 0) {
                    Picasso.with(context).load(list[0].file_url.getImageUrl()).into(reservationImage1)
                    name1.text = list[0].title
                    title1.text = list[0].sub_title
                    reservation1.setOnClickListener {
                        //进入专家详情
                        val intent = Intent(it.context, ReservationDetailsActivity::class.java)
                        intent.putExtra("id",list[0].id)
                        startActivity(intent)
                    }
                }
                if (list.size > 1) {
                    Picasso.with(context).load(list[1].file_url.getImageUrl()).into(reservationImage2)
                    name2.text = list[1].title
                    title2.text = list[1].sub_title
                    reservation2.setOnClickListener {
                        //进入专家详情
                        val intent = Intent(it.context, ReservationDetailsActivity::class.java)
                        intent.putExtra("id",list[1].id)
                        startActivity(intent)
                    }
                }
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

        }, false).postRequest(activity as Context, YYZJ.getInterface(),map)
    }
}