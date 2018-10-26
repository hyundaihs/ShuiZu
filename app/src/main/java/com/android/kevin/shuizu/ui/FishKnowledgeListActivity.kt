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
import com.android.kevin.shuizu.entities.FishKnowledge
import com.android.kevin.shuizu.entities.FishKnowledgeListRes
import com.android.kevin.shuizu.entities.YLZS
import com.android.kevin.shuizu.entities.getInterface
import com.android.shuizu.myutillibrary.MyBaseActivity
import com.android.shuizu.myutillibrary.adapter.LineDecoration
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.initActionBar
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.android.shuizu.myutillibrary.widget.SwipeRefreshAndLoadLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_fish_knowledge_list.*
import kotlinx.android.synthetic.main.layout_fish_knowledge_list_item.view.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/14/014.
 */
class FishKnowledgeListActivity : MyBaseActivity(), View.OnClickListener {

    val fishData = ArrayList<FishKnowledge>()
    private val fishAdapter = FishKnowledgeAdapter(fishData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fish_knowledge_list)
        initViews()
    }

    private fun initViews() {
        fishKnowledgeBack.setOnClickListener {
            finish()
        }
        val layoutManager = LinearLayoutManager(this)
        fishKnowledgeList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        fishKnowledgeList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        fishKnowledgeList.itemAnimator = DefaultItemAnimator()
        fishKnowledgeList.isNestedScrollingEnabled = false
        fishKnowledgeSwipe.setOnRefreshListener(object : SwipeRefreshAndLoadLayout.OnRefreshListener {
            override fun onRefresh() {
                getFishKnowledgeList(fishKnowledgeSwipe.currPage, true)
            }

            override fun onLoadMore(currPage: Int, totalPages: Int) {
                getFishKnowledgeList(currPage)
            }
        })
        fishKnowledgeList.adapter = fishAdapter
        fishAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                val intent = Intent(view.context, FishKnowledgeDetailsActivity::class.java)
                intent.putExtra("id", fishData[position].id)
                startActivity(intent)
            }
        }
        getFishKnowledgeList(fishKnowledgeSwipe.currPage, true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.searchBtn -> {
                getFishKnowledgeList(fishKnowledgeSwipe.currPage, true)
            }
        }
    }

    private class FishKnowledgeAdapter(val data: ArrayList<FishKnowledge>) : MyBaseAdapter(R.layout.layout_fish_knowledge_list_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val fishKnowledge = data[position]
            holder.itemView.fishItemTitle.text = fishKnowledge.title
            holder.itemView.fishItemSubtitle.text = fishKnowledge.sub_title
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getFishKnowledgeList(page: Int, isRefresh: Boolean = false) {
        val title = if (searchText.text.isEmpty()) "" else searchText.text.toString()
        val map = mapOf(Pair("page", page.toString()), Pair("title", title))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val fishKnowledgeListRes = Gson().fromJson(result, FishKnowledgeListRes::class.java)
                fishKnowledgeSwipe.totalPages = if (fishKnowledgeListRes.retCounts % 20 == 0)
                    fishKnowledgeListRes.retCounts / 20 else fishKnowledgeListRes.retCounts / 20 + 1
                if (isRefresh) {
                    fishData.clear()
                }
                fishData.addAll(fishKnowledgeListRes.retRes)
                fishAdapter.notifyDataSetChanged()
                fishKnowledgeSwipe.isRefreshing = false
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

        }, false).postRequest(this as Context, YLZS.getInterface(), map)
    }
}