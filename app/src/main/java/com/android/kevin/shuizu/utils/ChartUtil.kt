package com.android.kevin.shuizu.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.ChartDataType
import com.android.shuizu.myutillibrary.I
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.CalendarUtil.YY_MM_DD_HH_MM
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.charts.Chart


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/23/023.
 */
class ChartUtil(private val context: Context, private val lineChart: LineChart,
                val type: Int, edgeListener: OnEdgeListener? = null) {
    /**
     * Entry 坐标点对象  构造函数 第一个参数为x点坐标 第二个为y点
     */
//    lateinit var dataSet: LineDataSet
    private var canLoad: Boolean = false//K线图手指交互已停止，正在惯性滑动


    init {
        lineChart.setNoDataText("没有数据哦")//没有数据时显示的文字
        lineChart.setNoDataTextColor(Color.BLUE)//没有数据时显示文字的颜色
        lineChart.setDrawGridBackground(false)//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(false)//禁止绘制图表边框的线
        lineChart.setVisibleXRangeMaximum(10f)
        lineChart.animateXY(1000, 1000)
        val lineData = LineData()
        lineChart.data = lineData
        lineChart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                canLoad = false
            }

            override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                val leftX = lineChart.lowestVisibleX
                val rightX = lineChart.highestVisibleX
                if (leftX == lineChart.xChartMin) {//滑到最左端
                    canLoad = false
                    edgeListener?.edgeLoad(leftX, true)
                } else if (rightX == lineChart.xChartMax) {//滑到最右端
                    canLoad = false
                    edgeListener?.edgeLoad(rightX, false)
                } else {
                    canLoad = true
                }
            }

            override fun onChartLongPressed(me: MotionEvent) {

            }

            override fun onChartDoubleTapped(me: MotionEvent) {

            }

            override fun onChartSingleTapped(me: MotionEvent) {

            }

            override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {

            }

            override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {
                I("onChartScale")
            }

            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
                if (canLoad) {
                    val leftX = lineChart.lowestVisibleX
                    val rightX = lineChart.highestVisibleX
                    if (leftX == lineChart.xChartMin) {//滑到最左端
                        canLoad = false
                        edgeListener?.edgeLoad(leftX, true)
                    } else if (rightX == lineChart.xChartMax) {//滑到最右端
                        canLoad = false
                        edgeListener?.edgeLoad(rightX, false)
                    }
                }
            }
        }
        setStyle()
    }

    fun setTitle(title: String) {
        val description = Description()
        description.isEnabled = true
        description.text = title
        description.textColor = Color.RED
        description.textSize = 16f
        lineChart.description = description//设置图表描述信息
    }

    fun addData(values: ArrayList<Entry>) {
        val lineData = lineChart.data
        // 每一个LineDataSet代表一条线，每张统计图表可以同时存在若干个统计折线，这些折线像数组一样从0开始下标。
        // 本例只有一个，那么就是第0条折线
        for (i in 0 until lineData.dataSetCount) {
            lineData.removeDataSet(i)
        }
        val set: LineDataSet
        if (lineData.dataSetCount > 0) {
            set = lineData.dataSets[0] as LineDataSet
        } else {
            set = createLineDataSet(values)
            lineData.addDataSet(set)
        }
        for (i in values.indices) {
            set.addEntry(values[i])
        }
        lineData.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    fun setData(values: ArrayList<Entry>) {

        val lineData = lineChart.data
        // 每一个LineDataSet代表一条线，每张统计图表可以同时存在若干个统计折线，这些折线像数组一样从0开始下标。
        // 本例只有一个，那么就是第0条折线
        for (i in 0 until lineData.dataSetCount) {
            lineData.removeDataSet(i)
        }
        val set: LineDataSet
        if (lineData.dataSetCount > 0) {
            set = lineData.dataSets[0] as LineDataSet
        } else {
            set = createLineDataSet(values)
            lineData.addDataSet(set)
        }
        set.values = values
        lineData.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    private fun createLineDataSet(values: ArrayList<Entry>): LineDataSet {
        //设置数据1  参数1：数据源 参数2：图例名称
        val dataSet = LineDataSet(values, null)
        val color = when (type) {
            ChartDataType.WD -> context.resources.getColor(R.color.tip_red)
            ChartDataType.PH -> context.resources.getColor(R.color.tip_yellow)
            ChartDataType.TDS -> context.resources.getColor(R.color.tip_blue)
            else -> {
                context.resources.getColor(R.color.tip_red)
            }
        }
        dataSet.color = color
        dataSet.setCircleColor(color)
        dataSet.valueTextColor = color
        dataSet.lineWidth = 1f//设置线宽
        dataSet.circleRadius = 2f//设置焦点圆心的大小
        dataSet.enableDashedHighlightLine(10f, 0f, 0f)//点击后的高亮线的显示样式
        dataSet.highlightLineWidth = 2f//设置点击交点后显示高亮线宽
        dataSet.isHighlightEnabled = false//是否禁用点击高亮线
        dataSet.highLightColor = Color.RED//设置点击交点后显示交高亮线的颜色
        dataSet.valueTextSize = 9f//设置显示值的文字大小
        dataSet.setDrawFilled(false)//设置禁用范围背景填充
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            val drawable = ContextCompat.getDrawable(context, android.R.color.white)
            dataSet.fillDrawable = drawable//设置范围背景填充
        } else {
            dataSet.fillColor = Color.BLACK
        }
        return dataSet
    }


    private fun setStyle() {
        //设置X轴样式
        val xAxis = lineChart.xAxis
        //设置X轴上每个竖线是否显示
        xAxis.setDrawGridLines(true)

        //设置是否绘制X轴上的对应值(标签)
        xAxis.setDrawLabels(true)

//        //设置x轴间距
//        xAxis.granularity = 1f
//
//        xAxis.setLabelCount(values.size,true)

        xAxis.position = XAxis.XAxisPosition.BOTTOM//设置x轴的显示位置
//        xAxis.labelCount = 3
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
            val calendarUtil = CalendarUtil(value.toLong(), true)
            calendarUtil.format(YY_MM_DD_HH_MM)
        }
//        xAxis.labelRotationAngle = 60f

        val rightAxis = lineChart.axisRight
        //设置图表右边的y轴禁用
        rightAxis.isEnabled = false
        //获取左边的轴线
        val leftAxis = lineChart.axisLeft
        leftAxis.mAxisMinimum = 0f
        //是否绘制0所在的网格线
        leftAxis.setDrawZeroLine(false)
        if (type == ChartDataType.TDS) {
            leftAxis.valueFormatter = IAxisValueFormatter { value, axis ->
                value.toLong().toString()
            }
        }
        val legend = lineChart.legend//图例
        legend.isEnabled = false
    }

    interface OnEdgeListener {
        fun edgeLoad(x: Float, left: Boolean)
    }
}