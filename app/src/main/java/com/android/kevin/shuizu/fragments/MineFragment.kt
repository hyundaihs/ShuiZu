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
import com.paradoxie.autoscrolltextview.VerticalTextview
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_mine.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class MineFragment : BaseFragment(), View.OnClickListener {
    var login_verf: String by PreferenceUtil(SZApplication.instance, App_Keyword.LOGIN_VERF, "")
    var isFlag = false
    val warnLogList = ArrayList<WarnLog>()

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
        isFlag = true
        getMsgLog()
        msgTitle.startAutoScroll()
    }

    override fun onStop() {
        super.onStop()
        msgTitle.stopAutoScroll()
        isFlag = false
    }

    private fun initViews() {
        changeUserInfo.setOnClickListener(this)
        exit.setOnClickListener(this)
        fishLogBtn.setOnClickListener(this)
        fishKnowledgeBtn.setOnClickListener(this)
        warnLayout.setOnClickListener(this)
        msgTitle.setTextStillTime(3000)//设置停留时长间隔
        msgTitle.setAnimTime(300)//设置进入和退出的时间间隔
        msgTitle.setOnItemClickListener(VerticalTextview.OnItemClickListener { position ->
            val intent = Intent(activity, MsgLogListActivity::class.java)
            startActivity(intent)
        })
        msgTitle.setOnClickListener {
            val intent = Intent(activity, MsgLogListActivity::class.java)
            startActivity(intent)
        }
        mineHelp.setOnClickListener {
            startActivity(Intent(it.context,InstructionsActivity::class.java))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.warnLayout -> {
                val intent = Intent(activity, MsgLogListActivity::class.java)
                startActivity(intent)
            }
            R.id.changeUserInfo -> {
                startActivity(Intent(context, ChangeUserInfoActivity::class.java))
            }
            R.id.exit -> {
                activity?.CustomDialog("提示", "确定要退出登录吗？", positiveClicked = DialogInterface.OnClickListener { p0, p1 ->
                    login_verf = ""
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }, negative = "取消")
            }
            R.id.fishLogBtn -> {
                startActivity(Intent(context, FishLogListActivity::class.java))
            }
            R.id.fishKnowledgeBtn -> {
                startActivity(Intent(context, FishKnowledgeListActivity::class.java))
            }
        }
    }

    private fun getUserInfo() {
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val userInfoRes = Gson().fromJson(result, UserInfoRes::class.java)
                userInfo = userInfoRes.retRes
                if (mineImage != null) {
                    Picasso.with(context).load(userInfo.file_url.getImageUrl()).into(mineImage)
                }
                mineName?.text = userInfo.title
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

    private fun getMsgLog() {
        doAsync {
            while (isFlag) {
                uiThread {
                    getMsgLogRequest()
                }
                Thread.sleep(10000)
            }
        }
    }

    private fun getMsgLogRequest() {
        val map = if (warnLogList.size > 0) {
            mapOf(Pair("times", warnLogList[warnLogList.lastIndex].create_time.toString()))
        } else {
            mapOf(Pair("", ""))
        }
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val warnLogListRes = Gson().fromJson(result, WarnLogListRes::class.java)
                warnLogList.clear()
                warnLogList.addAll(warnLogListRes.retRes)
                val titleList = ArrayList<String>()
                if (warnLogList.size > 0) {
                    msgNum.visibility = View.VISIBLE
                    msgNum.text = warnLogList.size.toString()
                    warnLogList.indices.mapTo(titleList) { warnLogList[it].title }
                } else {
                    msgNum.visibility = View.INVISIBLE
                    titleList.add("暂无新的消息")
                }
                msgTitle?.setTextList(titleList)
            }

            override fun onError(context: Context, error: String) {
                context.toast(error)
            }

            override fun onLoginErr(context: Context) {
            }

        }, false).postRequest(activity as Context, NEW_NEWS.getInterface(), map)
    }
}