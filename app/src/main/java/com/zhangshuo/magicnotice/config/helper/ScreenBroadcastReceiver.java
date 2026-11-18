package com.zhangshuo.magicnotice.config.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.barrage.BarrageHelper;
import com.zhangshuo.magicnotice.notice.NotificationHelper;

public class ScreenBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.i("ScreenBroadcastReceiver onReceive>>" + action);
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            ScreenStatusHelper.SCREEN_STATUS = true;
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            ScreenStatusHelper.SCREEN_STATUS = false;
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
            ScreenStatusHelper.SCREEN_STATUS = true;
            BarrageHelper.get().checkBarrageStatus();
            NotificationHelper.get().cleanWaitNotice();
        }
    }
}