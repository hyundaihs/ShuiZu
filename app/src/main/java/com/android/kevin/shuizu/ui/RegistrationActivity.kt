package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import org.jetbrains.anko.toast
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_registration.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/14/014.
 */
class RegistrationActivity : MyBaseActivity() {

    companion object {
        val PAGE_KEY = "isRegiste"
    }

    var isRegister = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        isRegister = intent.getBooleanExtra(PAGE_KEY, true)
        if (isRegister) {
            initRegistration()
        } else {
            initFindPswd()
        }
        getVerifyCode.setOnClickListener {
            if (phone.text.isEmpty()) {
                phone.setError("手机号码不能为空")
            } else {
                getVerifyCode()
            }
        }
        submit.setOnClickListener {
            if (checkData()) {
                submit()
            }
        }
        goLogin.setOnClickListener {
            finish()
        }
    }

    private fun initRegistration() {
        initActionBar(this, "注册")
        isAgreeLayout.visibility = View.VISIBLE
        goLogin.visibility = View.VISIBLE
    }

    private fun initFindPswd() {
        initActionBar(this, "找回密码")
        isAgreeLayout.visibility = View.GONE
        goLogin.visibility = View.GONE
    }

    private fun checkData(): Boolean {
        if (phone.text.isEmpty()) {
            phone.setError("手机号码不能为空")
            return false
        } else if (password.text.isEmpty()) {
            password.setError("密码不能为空")
            return false
        } else if (verifyPassword.text.isEmpty()) {
            verifyPassword.setError("验证密码不能为空")
            return false
        } else if (password.text.trim() != verifyPassword.text.trim()) {
            password.setError("两次输入的密码不一致")
            return false
        } else if (verifyCode.text.isEmpty()) {
            verifyCode.setError("验证码不能为空")
            return false
        } else if (isRegister and !isAgree.isChecked) {
            toast("请阅读并同意水族协议")
            return false
        } else {
            return true
        }
    }

    private fun getVerifyCode() {
        val map = mapOf(Pair("phone", phone.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, getInterface(SEND_VERF), map)
    }

    private fun submit() {
        val map = mapOf(Pair("account", phone.text.toString()),
                Pair("verf", verifyCode.text.toString()),
                Pair("password", password.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("注册成功")
                finish()
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, getInterface(REG), map)
    }
}