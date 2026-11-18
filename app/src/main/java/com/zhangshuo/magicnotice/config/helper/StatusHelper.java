package com.zhangshuo.magicnotice.config.helper;

import android.content.Context;

import com.zhangshuo.common.util.LogUtils;

public abstract class StatusHelper<D> {
    private D data;

    public abstract void register(Context context);

    public abstract void unRegister(Context context);

    public abstract D refreshStatus(Context context);

    public void onRefresh(Context context) {
        long currentTime = System.currentTimeMillis();
        data = refreshStatus(context);
        LogUtils.i(getClass().getSimpleName() + " onRefresh >> " + data + " lost time:" + (System.currentTimeMillis() - currentTime));
    }

    public D getData() {
        LogUtils.i("AppStatus >> " + getClass().getSimpleName() + " data >> " + data);
        return data;
    }

}
