package com.zhangshuo.magicnotice.config.helper;

import android.content.Context;

public class ScreenOrientationHelper extends StatusHelper<Integer> {

    public void register(Context context) {
    }

    public void unRegister(Context context) {

    }

    @Override
    public Integer refreshStatus(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

}
