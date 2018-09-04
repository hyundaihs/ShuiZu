package com.android.kevin.shuizu.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.android.kevin.shuizu.R
import com.android.kevin.shuizu.entities.ChartDataType
import com.android.kevin.shuizu.entities.WaterHistoryData
import com.android.shuizu.myutillibrary.D
import com.android.shuizu.myutillibrary.E
import com.android.shuizu.myutillibrary.utils.CalendarUtil
import com.android.shuizu.myutillibrary.utils.CalendarUtil.YY_MM_DD_HH_MM
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.components.Legend
import android.view.MotionEvent
import com.android.shuizu.myutillibrary.I
import com.android.shuizu.myutillibrary.toast
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/23/023.
 */
class ChartUtil(private val context: Context, private val lineChart: LineChart, resData: ArrayList<WaterHistoryData>, val type: Int) {
    /**
     * Entry 坐标点对象  构造函数 第一个参数为x点坐标 第二个为y点
     */
    var values: ArrayList<Entry> = ArrayList()
    lateinit var set: LineDataSet
    var mIsCanLoad = false

    init {
        val description = Description()
        description.isEnabled = false
        description.text = ""
        description.textColor = Color.RED
        description.textSize = 20f
        lineChart.description = description//设置图表描述信息
        lineChart.setNoDataText("没有数据哦")//没有数据时显示的文字
        lineChart.setNoDataTextColor(Color.BLUE)//没有数据时显示文字的颜色
        lineChart.setDrawGridBackground(false)//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(false)//禁止绘制图表边框的线
        //lineChart.setBorderColor() //设置 chart 边框线的颜色。
        //lineChart.setBorderWidth() //设置 chart 边界线的宽度，单位 dp。
        //lineChart.setLogEnabled(true)//打印日志
        //lineChart.notifyDataSetChanged()//刷新数据
        //invalidate()//重绘
        for (i in 0 until resData.size) {
            values.add(Entry(resData[i].x.toFloat(), resData[i].y))
        }
        lineChart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                mIsCanLoad = false
            }

            override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {

                val rightXIndex = lineChart.highestVisibleX  //获取可视区域中，显示在x轴最右边的index
                val size = values.size
//                context.toast("    size = $size")
//                if (lastPerformedGesture == ChartTouchListener.ChartGesture.DRAG) {
//                    mIsCanLoad = true
//                    if (rightXIndex == size - 1 || rightXIndex == size) {
//                        mIsCanLoad = false
//                        //加载更多数据的操作
//
//                    }
//                }
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
                if (mIsCanLoad) {
                    val rightXIndex = lineChart.highestVisibleX     //获取可视区域中，显示在x轴最右边的index
//                    val size = mBarChart.getBarData().getXVals().size()
//                    if (rightXIndex == size - 1 || rightXIndex == size) {
//                        mIsCanLoad = false
//                        //加载更多数据的操作
//
//                    }
                }
            }
        }
    }

    fun show() {
        //判断图表中原来是否有数据
        if (lineChart.data != null &&
                lineChart.data.dataSetCount > 0) {
            //获取数据1
            set = lineChart.data.getDataSetByIndex(0) as LineDataSet
            set.values = values
            //刷新数据
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
        } else {
            //设置数据1  参数1：数据源 参数2：图例名称
            set = LineDataSet(values, null)
            val color = when (type) {
                ChartDataType.WD -> context.resources.getColor(R.color.tip_red)
                ChartDataType.PH -> context.resources.getColor(R.color.tip_yellow)
                ChartDataType.TDS -> context.resources.getColor(R.color.tip_blue)
                else -> {
                    context.resources.getColor(R.color.tip_red)
                }
            }
            set.color = color
            set.setCircleColor(color)
            set.valueTextColor = color
            set.lineWidth = 1f//设置线宽
            set.circleRadius = 2f//设置焦点圆心的大小
            set.enableDashedHighlightLine(10f, 0f, 0f)//点击后的高亮线的显示样式
            set.highlightLineWidth = 2f//设置点击交点后显示高亮线宽
            set.isHighlightEnabled = false//是否禁用点击高亮线
            set.highLightColor = Color.RED//设置点击交点后显示交高亮线的颜色
            set.valueTextSize = 9f//设置显示值的文字大小
            set.setDrawFilled(false)//设置禁用范围背景填充

//            //格式化显示数据
//            val mFormat = DecimalFormat("###,###,##0")
//            set.setValueFormatter { value, entry, dataSetIndex, viewPortHandler -> mFormat.format(value) }

            setStyle()
//            val rightAxis = lineChart.axisRight
//            rightAxis.valueFormatter = IAxisValueFormatter { value, axis ->
//                val calendarUtil = CalendarUtil(value.toLong(), true)
//                calendarUtil.format(YY_MM_DD_HH_MM)
//            }
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(context, android.R.color.white)
                set.fillDrawable = drawable//设置范围背景填充
            } else {
                set.fillColor = Color.BLACK
            }

            //保存LineDataSet集合
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set) // icon_add the datasets
            //创建LineData对象 属于LineChart折线图的数据集合
            val data = LineData(dataSets)
            // 添加到图表中
            lineChart.data = data
            //绘制图表
            lineChart.invalidate()
        }
    }

    private fun setStyle() {
        //设置X轴样式
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM//设置x轴的显示位置
        xAxis.labelCount = 3
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
        leftAxis.setStartAtZero(true)
        if (type == ChartDataType.TDS) {
            leftAxis.valueFormatter = IAxisValueFormatter { value, axis ->
                value.toLong().toString()
            }
        }
        val legend = lineChart.legend//图例
        legend.isEnabled = false
    }
}