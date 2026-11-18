package com.zhangshuo.magicnotice.notice;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.AppStatusConfig;
import com.zhangshuo.magicnotice.notice.factory.ProcessFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知监听器
 */
public class MainNoticeListenerService extends NotificationListenerService {
    private List<String> needCleanNoticeList = new ArrayList<>();
    private String zenModePolicyKey = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 收到通知时
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        LogUtils.i("onNotificationPosted " + sbn.getKey());
        //如果是自己应用
        if (sbn.getPackageName().equals(getPackageName())) {
            String channelId = "";
            int notificationId = sbn.getId();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channelId = sbn.getNotification().getChannelId();
            }
            LogUtils.i("Ignore MagicNotice " + sbn.getGroupKey() + " >> " + sbn.getKey() + ">C>" + channelId + ">>" + notificationId);
            boolean isRemindNotification = false;
            if ((!TextUtils.isEmpty(channelId) && "CicadaScheduler_Channel".equals(channelId))
                    || (TextUtils.isEmpty(channelId) && notificationId > 10000 && notificationId < 20000)) {
                isRemindNotification = true;
            }
            //如果不是提醒类型的通知，则返回，不拦截
            if (!isRemindNotification)
                return;
        }
        //判断是不是常驻通知
        if (sbn.isClearable() && !sbn.isOngoing()) {
            //开始处理数据
            boolean needFilter = ProcessFactory.get().onProcess(this, sbn);
            if (needFilter) {
                if (AppStatusConfig.get().isScreenUnPresent()) {
                    cancelNotification(sbn.getKey());
                } else {
                    needCleanNoticeList.add(sbn.getKey());
                }
            }
            LogUtils.i("onNotificationPosted needFilter:" + needFilter);
        } else {
            //如果不可以被清理，但是可以强制清除的，除去onGoing的
            String notificationPKGName = sbn.getPackageName();
            if (maybeZenModePolicy(sbn)) {
                //可能是免打扰
                zenModePolicyKey = sbn.getKey();
                LogUtils.i("onNotificationRemoved zenModePolicy On");
            }
            LogUtils.i("onNotificationPosted ongoingNotification" + sbn.getKey() + " pkgName:" + notificationPKGName);
        }
    }

    /**
     * 移除通知时
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if (sbn != null) {
            String key = sbn.getKey();
            if (!TextUtils.isEmpty(zenModePolicyKey) && zenModePolicyKey.equals(key)) {
                zenModePolicyKey = "";
                LogUtils.i("onNotificationRemoved zenModePolicy Off");
                return;
            }
            LogUtils.i("onNotificationRemoved " + key);
//            BarrageHelper.get().removeBarrageInfo(key);
        }
    }

    public void cleanNeedNoticeList() {
        for (int i = 0; i < needCleanNoticeList.size(); i++) {
            cancelNotification(needCleanNoticeList.get(i));
        }
        needCleanNoticeList.clear();
    }

    public boolean isZenMode() {
        return !TextUtils.isEmpty(zenModePolicyKey);
    }

    /**
     * 当通知连接时
     */
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        AppStatusConfig.get().register(this);
        ProcessFactory.get().initProcess();
        NotificationHelper.get().bindListenerService(this);
        LogUtils.i("onListenerConnected " + Thread.currentThread());

        StatusBarNotification[] activeNotifications = getActiveNotifications();
        if (activeNotifications != null) {
            for (StatusBarNotification activeNotification : activeNotifications) {
                if (maybeZenModePolicy(activeNotification)) {
                    zenModePolicyKey = activeNotification.getKey();
                    break;
                }
            }
        }
        LogUtils.i("onCreate zenModePolicy " + (TextUtils.isEmpty(zenModePolicyKey) ? "Off" : "On"));
    }

    /**
     * 当通知关闭时
     */
    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        LogUtils.i("onListenerDisconnected " + Thread.currentThread());
        NotificationHelper.get().unBindListenerService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                // A disconnected listener receives no more callbacks. Ask the system to bind it again.
            NotificationListenerService.requestRebind(
                    new ComponentName(this, MainNoticeListenerService.class));
        } catch (Exception e) {
                LogUtils.e("requestRebind notification listener failed: " + e.getMessage());
                LogUtils.e(e);
        }
        }
    }

    //异常关闭后尝试重启
    private void requestEnable() {
        try {
            PackageManager pm = getPackageManager();
            pm.setComponentEnabledSetting(new ComponentName(this, MainNoticeListenerService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(new ComponentName(this, MainNoticeListenerService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean maybeZenModePolicy(StatusBarNotification sbn) {
        if (sbn.getKey().contains("zenmode_notification_tag")) return true;
        try {
            Bundle extras = sbn.getNotification().extras;
            CharSequence charTitle = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence charContent = extras.getCharSequence(Notification.EXTRA_TEXT);
            String zenModePolicy = StringUtil.getString(R.string.notice_zen_mode_policy);
            if (charTitle != null && charTitle.toString().contains(zenModePolicy)) {
                return true;
            }
            if (charContent != null && charContent.toString().contains(zenModePolicy)) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
