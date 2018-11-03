package com.android.kevin.shuizu.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.*
import com.android.kevin.shuizu.ui.HomeActivity
import com.android.kevin.shuizu.ui.LogListActivity
import com.android.kevin.shuizu.ui.LoginActivity
import com.android.shuizu.myutillibrary.adapter.MyBaseAdapter
import com.android.shuizu.myutillibrary.fragment.BaseFragment
import com.android.shuizu.myutillibrary.request.MySimpleRequest
import com.android.shuizu.myutillibrary.utils.DisplayUtils
import com.android.shuizu.myutillibrary.utils.LoginErrDialog
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_intelligence.*
import kotlinx.android.synthetic.main.intelligence_list_item.view.*
import org.jetbrains.anko.toast


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/15/015.
 */
class IntelligenceFragment : BaseFragment(), View.OnClickListener {

    val mData = ArrayList<Statistics>()
    private val mAdapter = MyAdapter(mData)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intelligence, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        getInfo()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(activity)
        intelligenceRecycler.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        //intelligenceRecycler.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        intelligenceRecycler.itemAnimator = DefaultItemAnimator()
        intelligenceRecycler.adapter = mAdapter
        intelligenceRecycler.isNestedScrollingEnabled = false
        layout_left.setOnClickListener(this)
        layout_right.setOnClickListener(this)
        layout_top.setOnClickListener(this)
    }

    private fun initPieChart(data: ArrayList<Statistics>) {
//        pieChart.setDescription("BarChart Test")
//        pieChart.setHoleColorTransparent(true)
//        pieChart.setHoleRadius(60f)  //半径
//        pieChart.setHoleRadius(0)  //实心圆
//
//        pieChart.setDrawCenterText(true)  //饼状图中间可以添加文字
//        pieChart.setDrawHoleEnabled(true)
//        pieChart.setRotationAngle(90) // 初始旋转角度
        val d = Description()
        d.text = ""
        pieChart.transparentCircleRadius = 0f // 半透明圈
        pieChart.description = d
        pieChart.holeRadius = 0f
        pieChart.isRotationEnabled = false // 可以手动旋转
        pieChart.setUsePercentValues(false)  //显示成百分比
//        pieChart.setCenterText("PieChart")  //饼状图中间的文字

        //设置数据
        val pieData = getPieData(data)
        pieChart.data = pieData

        val mLegend = pieChart.legend  //设置比例图
        mLegend.formSize = 0f
        mLegend.orientation = Legend.LegendOrientation.VERTICAL
        mLegend.form = Legend.LegendForm.SQUARE  //设置比例图的形状，默认是方形 SQUARE
        mLegend.xEntrySpace = 7f
        mLegend.yEntrySpace = 5f

        pieChart.animateXY(1000, 1000)  //设置动画
        pieChart.invalidate()
    }

    private fun getPieData(src: ArrayList<Statistics>): PieData {
        val xValues = ArrayList<String>()  //xVals用来表示每个饼块上的内容

        /**
         * 将一个饼形图分成六部分， 各个部分的数值比例为12:12:18:20:28:10
         * 所以 12代表的百分比就是12%
         * 在具体的实现过程中，这里是获取网络请求的list的数据
         */
        val yValues = ArrayList<PieEntry>()  //yVals用来表示封装每个饼块的实际数据
        for (i in 0 until src.size) {
            xValues.add("")  //饼块上显示成PieChart1, PieChart2, PieChart3, PieChart4，PieChart5，PieChart6
            yValues.add(PieEntry((src[i].nums + 1).toFloat()))
        }
        //y轴的集合
        val pieDataSet = PieDataSet(yValues, "")
        pieDataSet.sliceSpace = 0f //设置个饼状图之间的距离
        pieDataSet.valueTextColor = Color.TRANSPARENT

        // 饼图颜色
        val colors = ArrayList<Int>()
        colors.add(resources.getColor(R.color.device_type1))
        colors.add(resources.getColor(R.color.device_type2))
        colors.add(resources.getColor(R.color.device_type3))
        colors.add(resources.getColor(R.color.device_type4))
        colors.add(resources.getColor(R.color.device_type5))
        pieDataSet.colors = colors

        pieDataSet.selectionShift = DisplayUtils.px2dp(activity, 10f).toFloat() // 选中态多出的长度

        return PieData(pieDataSet)
    }

    private fun refresh(bigStatistics: BigStatistics) {
        addGroup.text = bigStatistics.ygsl.toString()
        addDevice.text = bigStatistics.sbsl.toString()
        errDevice.text = bigStatistics.ycsb.toString()
        commHistory.text = bigStatistics.czsl.toString()
        warnHistory.text = bigStatistics.bjsl.toString()
        mData.clear()
        mData.addAll(bigStatistics.sbfb)
        mAdapter.notifyDataSetChanged()
        initPieChart(bigStatistics.sbfb)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_top -> {
                val h = activity as HomeActivity
                h.loadPage(0)
            }
            R.id.layout_left -> {
                val intent = Intent(activity, LogListActivity::class.java)
                intent.putExtra("type", 2)
                startActivity(intent)
            }
            R.id.layout_right -> {
                val intent = Intent(activity, LogListActivity::class.java)
                intent.putExtra("type", 1)
                startActivity(intent)
            }
        }
    }

    private class MyAdapter(val myData: ArrayList<Statistics>) : MyBaseAdapter(R.layout.intelligence_list_item) {
        override fun getItemCount(): Int {
            return myData.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val statistics = myData[position]
            when (statistics.card_type) {
                DeviceType.TR -> {
                    holder.itemView.intelDeviceTip.setImageResource(R.drawable.small_rect_type1)
                }
                DeviceType.WF -> {
                    holder.itemView.intelDeviceTip.setImageResource(R.drawable.small_rect_type2)
                }
                DeviceType.WP -> {
                    holder.itemView.intelDeviceTip.setImageResource(R.drawable.small_rect_type3)
                }
                DeviceType.PF -> {
                    holder.itemView.intelDeviceTip.setImageResource(R.drawable.small_rect_type4)
                }
                DeviceType.WL -> {
                    holder.itemView.intelDeviceTip.setImageResource(R.drawable.small_rect_type5)
                }
            }
            holder.itemView.intelDeviceTitle.text = statistics.title
            holder.itemView.intelDeviceNum.text = statistics.nums.toString()
        }

    }

    private fun getInfo() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val bigStatisticsRes = Gson().fromJson(result, BigStatisticsRes::class.java)
                refresh(bigStatisticsRes.retRes)
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

        }, false).postRequest(activity as Context, ZNTJ.getInterface(Gson().toJson(map)), map)
    }

}