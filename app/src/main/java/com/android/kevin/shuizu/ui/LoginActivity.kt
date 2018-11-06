package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.SZApplication
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.utils.MyProgressDialog
import com.android.shuizu.myutillibrary.utils.PreferenceUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import cn.jpush.android.api.TagAliasCallback
import android.R.attr.label
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.e.a.b.showToast
import com.android.shuizu.myutillibrary.*
import com.android.shuizu.myutillibrary.utils.CustomDialog
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/14/014.
 */
class LoginActivity : MyBaseActivity() {

    var login_verf: String by PreferenceUtil(SZApplication.instance, App_Keyword.LOGIN_VERF, "")
    var login_account: String by PreferenceUtil(SZApplication.instance, App_Keyword.LOGIN_ACCOUNT, "")
    var isSetAlias: Int by PreferenceUtil(SZApplication.instance, App_Keyword.IS_SET_ALIAS, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestPermission({
            getSystemInfo()
        }, onDenied = {
            finish()
        })
        registration.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra(RegistrationActivity.PAGE_KEY, true)
            startActivity(intent)
        }

        forgetPassword.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra(RegistrationActivity.PAGE_KEY, false)
            startActivity(intent)
        }
        loginBtn.setOnClickListener {
            if (checkData()) {
                login()
            }
        }
    }

    private fun autoLogin() {
        if (!login_verf.isEmpty()) {
            val map = mapOf(Pair("login_verf", login_verf))
            MySimpleRequest(object : MySimpleRequest.RequestCallBack {
                override fun onSuccess(context: Context, result: String) {
                    val loginInfoRes = Gson().fromJson(result, LoginInfoRes::class.java)
                    val loginInfo = loginInfoRes.retRes
                    login_verf = loginInfo.login_verf
                    setAlias(login_account)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }

                override fun onError(context: Context, error: String) {
                }

                override fun onLoginErr(context: Context) {
                    LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                }

            }).postRequest(this, VERF_LOGIN.getInterface(Gson().toJson(map)), map)
        }
    }

    private fun checkData(): Boolean {
        return when {
            account.text.trim().isEmpty() -> {
                account.error = "手机号码不能为空"
                false
            }
            password.text.trim().isEmpty() -> {
                password.error = "密码不能为空"
                false
            }
            else -> true
        }
    }

    private fun setAlias(str: String) {
//        if (isSetAlias == 0) {
        JPushInterface.setAlias(this, 0, str)
    }

    private fun login() {
        val map = mapOf(Pair("account", account.text.toString()),
                Pair("password", password.text.toString()),
                Pair("jpush_id", JPushInterface.getRegistrationID(this)))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val loginInfoRes = Gson().fromJson(result, LoginInfoRes::class.java)
                val loginInfo = loginInfoRes.retRes
                login_verf = loginInfo.login_verf
                setAlias(account.text.toString())
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }

            override fun onError(context: Context, error: String) {
                context.toast(error)
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }

        }).postRequest(this, LOGIN.getInterface(Gson().toJson(map)), map)
    }

    private fun getSystemInfo() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val systemInfoRes = Gson().fromJson(result, SystemInfoRes::class.java)
                SZApplication.systemInfo = systemInfoRes.retRes
                autoLogin()
            }

            override fun onError(context: Context, error: String) {
                context.toast(error)
                CustomDialog("错误",error,positiveClicked = object :DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        finish()
                    }
                })
            }

            override fun onLoginErr(context: Context) {
                context.LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }

        }).postRequest(this, SYS_INFO.getInterface(Gson().toJson(map)), map)
    }
}