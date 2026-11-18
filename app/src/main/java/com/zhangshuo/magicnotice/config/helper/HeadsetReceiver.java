package com.zhangshuo.magicnotice.config.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.config.AppStatusConfig;

public class HeadsetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(">>>>onReceive" + intent.getAction());
        AppStatusConfig.get().refreshHeadsetName();
    }
}
