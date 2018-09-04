package com.android.kevin.shuizu.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.App_Keyword
import com.android.kevin.shuizu.entities.App_Keyword.Companion.KEYWORD_CIRCLE_SELECTOR_TITLE
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import kotlinx.android.synthetic.main.activity_circle_selector.*

/**
 * Created by kevin on 2018/9/1.
 */
class CircleSelectorActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_selector)
        val title = intent.getStringExtra(KEYWORD_CIRCLE_SELECTOR_TITLE)
        val max = intent.getIntExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_MAX, 100)
        val min = intent.getIntExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_MIN, 0)
        val curr = intent.getFloatExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_CURR, 0f)
        val floatNum = intent.getIntExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_FLOAT_NUM, 1)
        val isFloat = intent.getBooleanExtra(App_Keyword.KEYWORD_CIRCLE_SELECTOR_ISFLOAT, true)
        initActionBar(this, title, rightBtn = "确定", rightClick = View.OnClickListener {
            val value = values.text.toString().toFloat()
            intent = Intent()
            intent.putExtra("data", value)
            setResult(App_Keyword.KEYWORD_CIRCLE_SELECTOR_RESULT, intent)
            finish()
        })
        selector.maxProcess = max.toFloat()
        selector.minProcess = min.toFloat()
        selector.isFloat = isFloat
        selector.floatNum = floatNum
        selector.curProcess = curr
        selector.setOnSeekBarChangeListener { seekbar, curValue ->
            values.text = curValue.toString()
        }
        values.text = curr.toString()
    }
}