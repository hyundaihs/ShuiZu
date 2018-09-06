package com.android.kevin.shuizu.utils

import android.content.Context
import android.graphics.Color
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
    var lineData = LineData()

    init {
        lineChart.setNoDataText("没有数据哦")//没有数据时显示的文字
        lineChart.setNoDataTextColor(Color.BLUE)//没有数据时显示文字的颜色
        lineChart.setDrawGridBackground(false)//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(false)//禁止绘制图表边框的线
        lineChart.setVisibleXRangeMaximum(10f)
        lineChart.animateXY(1000, 1000)
        lineChart.setMaxVisibleValueCount(10)
        lineChart.data = lineData
        //lineChart.setBorderColor() //设置 chart 边框线的颜色。
        //lineChart.setBorderWidth() //设置 chart 边界线的宽度，单位 dp。
        //lineChart.setLogEnabled(true)//打印日志
        //lineChart.notifyDataSetChanged()//刷新数据
        //invalidate()//重绘

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

//    fun setData(resData: ArrayList<WaterHistoryData>) {
//        for (i in 0 until resData.size) {
//            values.add(Entry(resData[i].x.toFloat(), resData[i].y))
//        }
//    }

//    fun notifyDataSetChanged() {
//        D("noti = $values")
//        //获取数据1
//        dataSet = lineChart.data.getDataSetByIndex(0) as LineDataSet
//        //刷新数据
//        dataSet.values = values
//        //通知数据已经改变
//        lineChart.data.notifyDataChanged()
//        lineChart.notifyDataSetChanged()
////        //设置在曲线图中显示的最大数量
////        lineChart.setVisibleXRangeMaximum(10f)
////        //移到某个位置
////        lineChart.moveViewToX((lineChart.data.entryCount - 5).toFloat())
//    }


    fun setTitle(title:String){
        val description = Description()
        description.isEnabled = false
        description.text = title
        description.textColor = Color.RED
        description.textSize = 20f
        lineChart.description = description//设置图表描述信息
    }

    fun show(values: ArrayList<Entry>) {

        val data = lineChart.getData()
        // 每一个LineDataSet代表一条线，每张统计图表可以同时存在若干个统计折线，这些折线像数组一样从0开始下标。
        // 本例只有一个，那么就是第0条折线
        var set: LineDataSet? = data.getDataSetByIndex(0) as LineDataSet?

        // 如果该统计折线图还没有数据集，则创建一条出来，如果有则跳过此处代码。
        if (set == null) {
            set = createLineDataSet(values)
            data.addDataSet(set)
        }
        for (i in values.indices) {
            data.addEntry(values[i], 0)
        }

////        // 像ListView那样的通知数据更新
//        data.notifyDataChanged()

        // y坐标轴线最大值
        // mChart.setVisibleYRange(30, AxisDependency.LEFT)
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
//        // 将坐标移动到最新
//        // 此代码将刷新图表的绘图
//        lineChart.moveViewToX(data.getDataSetCount() - 50f)
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