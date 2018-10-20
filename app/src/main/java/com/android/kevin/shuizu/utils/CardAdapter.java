package com.android.kevin.shuizu.utils;

import android.support.v7.widget.CardView;

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/10/20/020.
 */
public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}

