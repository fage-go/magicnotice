package com.zhangshuo.magicnotice.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.db.model.TxtData;
import com.zhangshuo.magicnotice.db.service.RemindDataService;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.ui.model.RemindInfo;

public class SchedulerBroadcast extends BroadcastReceiver {
    public static final String ACTION_SCHEDULER = "cicada_scheduler_action";

    @Override
    public void onReceive(Context context, Intent intent) {
        int remindId = intent.getIntExtra("remindId", 0);
        RemindDataService.findRemindById(remindId, new AsyncCallback<RemindInfo>() {
            @Override
            public void onSuccess(RemindInfo remindInfo) {
                if (remindInfo != null) {
                    int remindType = remindInfo.getRemindType();
                    String title = remindInfo.getTitle();
                    String content = remindInfo.getContent();
                    if (remindType == RemindInfo.TYPE_REMIND) {
                        TxtData randomTxtData = remindInfo.getRandomTxtData();
                        title = randomTxtData.getTitle();
                        content = randomTxtData.getContent();
                    }
                    NotificationHelper.get().notifyRemind(context, title, content,remindInfo.getMockPkg());
                    remindInfo.setNotifyCount(remindInfo.getNotifyCount() + 1);
                    remindInfo.setNotifyNumber(remindInfo.getNotifyNumber() + 1);
                    RemindDataService.insertOrUpdate(remindInfo, new AsyncCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            SchedulerUtil.get().configAlarm(remindInfo);
                        }
                    });
                }
            }
        });
    }
}
