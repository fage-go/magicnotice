package com.zhangshuo.magicnotice.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.notice.NotificationHelper;

/**
 * Uses a foreground service for the persistent notification keep-alive switch.
 */
public class KeepAliveService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        startKeepAliveForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!LocalDataUtil.get().getData(LocalDataKeys.BKEY_KEEP_ALIVE_NOTIFICATION, false)) {
            stopSelf();
            return START_NOT_STICKY;
        }
        startKeepAliveForeground();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    private void startKeepAliveForeground() {
        Notification notification = NotificationHelper.get().createKeepAliveNotification(this);
        startForeground(NotificationHelper.KEEP_ALIVE_NOTIFICATION_ID, notification);
    }
}
