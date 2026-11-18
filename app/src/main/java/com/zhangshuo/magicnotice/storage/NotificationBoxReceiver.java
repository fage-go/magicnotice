package com.zhangshuo.magicnotice.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhangshuo.magicnotice.ui.activity.NotReadListActivity;

public class NotificationBoxReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int ruleId = intent.getIntExtra("ruleId", 0);
        int unReadNum = intent.getIntExtra("unReadNum", 0);
        NotReadListActivity.openNotReadWindow(context, ruleId, unReadNum, false);
        NotificationBoxHelper.get().cleanNotification(ruleId);
    }
}
