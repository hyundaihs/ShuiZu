package com.android.kevin.shuizu.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.View
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_instructions.*
import kotlinx.android.synthetic.main.activity_msg_log.*
import kotlinx.android.synthetic.main.layout_instructions_item.view.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/25/025.
 */
class InstructionsActivity : MyBaseActivity() {

    private val mData = ArrayList<Instructions>()
    private val mAdapter = InstructionsAdapter(mData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)
        initActionBar(this, "使用说明")
        initViews()
        getInstructionsList()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(this)
        instructionList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        instructionList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        instructionList.itemAnimator = DefaultItemAnimator()
        instructionList.isNestedScrollingEnabled = false
        instructionList.adapter = mAdapter
        mAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                val intent = Intent(view.context, InstructionsDetailsActivity::class.java)
                intent.putExtra("id", mData[position].id)
                startActivity(intent)
            }
        }
    }

    private fun getInstructionsList() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val instructionsListRes = Gson().fromJson(result, InstructionsListRes::class.java)
                mData.clear()
                mData.addAll(instructionsListRes.retRes)
                mAdapter.notifyDataSetChanged()
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

        }, false).postRequest(this as Context, CZSM.getInterface(), map)
    }

    private class InstructionsAdapter(val data: ArrayList<Instructions>) : MyBaseAdapter(R.layout.layout_instructions_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val instructions = data[position]
            holder.itemView.instructionItem.text = instructions.title
        }

        override fun getItemCount(): Int = data.size
    }

}