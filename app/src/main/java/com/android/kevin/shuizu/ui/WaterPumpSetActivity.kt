package com.android.kevin.shuizu.ui

import android.os.Bundle
import android.view.View
import com.android.kevin.shuizu.R
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.initActionBar
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.android.kevin.shuizu.MainActivity
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView



/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/30/030.
 */
class WaterPumpSetActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_pump)
        initActionBar(this, "智能水泵", rightBtn = "提交", rightClick = View.OnClickListener {

        })

    }

    private fun showPicker(){
        val pvOptions = OptionsPickerBuilder(this, OnOptionsSelectListener { options1, option2, options3, v ->
            //返回的分别是三个级别的选中位置
//            val tx = (options1Items.get(options1).getPickerViewText()
//                    + options2Items.get(options1).get(option2)
//                    + options3Items.get(options1).get(option2).get(options3).getPickerViewText())
//            tvOptions.setText(tx)
        })
                //.build()
        //pvOptions.setPicker(options1Items, options2Items, options3Items)
        //pvOptions.show()
    }
}