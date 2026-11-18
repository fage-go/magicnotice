package com.zhangshuo.common.listener;

import android.view.View;

public abstract class OnClickCallback implements View.OnClickListener {
    private long lastTimestamp = 0;

    @Override
    public void onClick(View v) {
        long diffTime = (System.currentTimeMillis() - lastTimestamp);
        if (diffTime > 1000) {
            lastTimestamp = System.currentTimeMillis();
            click(v);
        }
    }

    public abstract void click(View v);
}
