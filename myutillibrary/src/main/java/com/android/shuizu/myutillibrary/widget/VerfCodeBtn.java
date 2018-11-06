package com.android.shuizu.myutillibrary.widget;

import android.content.Context;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/5/6/006.
 */
public class VerfCodeBtn extends android.support.v7.widget.AppCompatTextView {

    private boolean isRun = false;
    private int i = 60;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            i--;
            if (i <= 0) {
                isRun = false;
                setText("获取验证码");
                i = 60;
            } else {
                setText("重新获取(" + i + "秒)");
            }
        }
    };

    public VerfCodeBtn(Context context) {
        super(context);
    }

    public VerfCodeBtn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener l) {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRun) {
                    if (l != null){
                        l.onClick(v);
                    }
                }
            }
        });
    }

    public void startCount(){
        new Thread(new MyRunnable()).start();
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
