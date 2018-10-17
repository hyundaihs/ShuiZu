package com.android.shuizu.myutillibrary.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/21/021.
 */
public class GridDivider extends RecyclerView.ItemDecoration {
    private int mDividerHight = 2;
    public final int[] ATRRS = new int[]{android.R.attr.listDivider};
    private int spanCount = 2;

    public GridDivider(Context context, int spanCount) {
        final TypedArray ta = context.obtainStyledAttributes(ATRRS);
        this.spanCount = spanCount;
        ta.recycle();
    }

    /*
     int dividerHight  分割线的线宽
     int dividerColor  分割线的颜色
     */
    public GridDivider(Context context, int dividerHight, int spanCount) {
        this(context, spanCount);
        mDividerHight = dividerHight / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int left = mDividerHight;
        int top = mDividerHight;
        int right = mDividerHight;
        int bottom = mDividerHight;
//        if (parent.getChildLayoutPosition(view) % spanCount == 0) {//第一列
//            top = 0;
//        }
//        if (parent.getChildLayoutPosition(view) < spanCount) {//第一行
//            left = 0;
//        }
//        if((parent.getChildLayoutPosition(view)  + 1) % spanCount == 0){//最后一列
//            bottom = 0;
//        }
//        if(parent.getChildCount() - parent.getChildLayoutPosition(view) < spanCount){//最后一行
//            right = 0;
//        }
        outRect.set(left, top, right, bottom);
    }

}
