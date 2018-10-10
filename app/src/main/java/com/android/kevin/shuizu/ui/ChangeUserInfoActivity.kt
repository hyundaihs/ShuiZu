package com.android.kevin.shuizu.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.SZApplication
import com.android.kevin.shuizu.entities.UserInfo
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import kotlinx.android.synthetic.main.activity_change_user_info.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/9/009.
 */
class ChangeUserInfoActivity : MyBaseActivity() {

    lateinit var userTemp :UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_info)
        initActionBar(this, "修改信息")
        //userTemp = SZApplication.userInfo.copy(file_url = "")
        initViews()
    }

    private fun initViews() {
        photoMore.setOnClickListener {
            startActivity(Intent(it.context, MyTakePhotoActivity::class.java))
        }
        name.setText(SZApplication.userInfo.title)
        warnMsg.isChecked = SZApplication.userInfo.ts_status == 1
        warnMsg.setOnCheckedChangeListener { buttonView, isChecked ->
            D("设置推送")
        }
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userTemp.title = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }
}