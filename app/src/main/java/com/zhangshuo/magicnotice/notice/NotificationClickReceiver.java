package com.zhangshuo.magicnotice.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationClickReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "push_notification";//intent action

    @Override
    public void onReceive(Context context, Intent intent) {
        if (INTENT_ACTION.equals(intent.getAction())) {
//            ToastUtils.showShort(context.getString(R.string.notification_click));
        }
    }

}
