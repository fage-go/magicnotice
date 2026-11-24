package com.zhangshuo.magicnotice.notice;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.AppStatusConfig;
import com.zhangshuo.magicnotice.notice.factory.ProcessFactory;

import java.util.List;


/**
 * 通知监听器
 */
public class MainNoticeListenerService extends NotificationListenerService {
    private final OriginalDismissals originals = new OriginalDismissals();
    private final Handler main = new Handler(Looper.getMainLooper());
    private static final int DISMISS_BATCH_SIZE = 4;
    private final Runnable drainCommitted = this::dismissCommittedBatch;
    private boolean dismissalScheduled;
    private boolean connected;
    private boolean destroyed;
    private ListenerStatusDot statusDot;
    private volatile String zenModePolicyKey = "";

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
        if (sbn == null) return;
        final long received = SystemClock.elapsedRealtime();
        final long sourceAge = System.currentTimeMillis() - sbn.getPostTime();
        onMain(() -> handlePosted(sbn, received, sourceAge));
    }

    private void handlePosted(StatusBarNotification sbn, long received, long sourceAge) {
        final OriginalDismissals.Delivery delivery = originals.begin(sbn.getKey(), sbn.getPostTime());
        //如果是自己应用
        if (sbn.getPackageName().equals(getPackageName())) {
            String channelId = "";
            int notificationId = sbn.getId();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channelId = sbn.getNotification().getChannelId();
            }
            boolean isRemindNotification = false;
            if ((!TextUtils.isEmpty(channelId) && "CicadaScheduler_Channel".equals(channelId))
                    || (TextUtils.isEmpty(channelId) && notificationId > 10000 && notificationId < 20000)) {
                isRemindNotification = true;
            }
            //如果不是提醒类型的通知，则返回，不拦截
            if (!isRemindNotification) {
                originals.finish(delivery);
                return;
            }
        }
        //判断是不是常驻通知
        if (sbn.isClearable() && !sbn.isOngoing()) {
            LogUtils.i("delivery phase=received source_age_ms=" + sourceAge);
            ProcessFactory.get().onProcess(this, sbn, received, dismissal -> {
                if (dismissal != null && originals.commit(delivery, dismissal::allowed)) {
                    scheduleDismissals(0);
                } else {
                    originals.finish(delivery);
                }
            });
        } else {
            originals.finish(delivery);
            if (maybeZenModePolicy(sbn)) zenModePolicyKey = sbn.getKey();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if (sbn == null) return;
        onMain(() -> {
            originals.removed(sbn.getKey(), sbn.getPostTime());
            if (sbn.getKey().equals(zenModePolicyKey)) zenModePolicyKey = "";
        });
    }

    public void cleanNeedNoticeList() {
        onMain(() -> scheduleDismissals(0));
    }

    private void scheduleDismissals(long delay) {
        if (!dismissalScheduled && connected && AppStatusConfig.get().isScreenUnPresent()) {
            dismissalScheduled = true;
            main.postDelayed(drainCommitted, delay);
        }
    }

    private void dismissCommittedBatch() {
        dismissalScheduled = false;
        if (!connected || !AppStatusConfig.get().isScreenUnPresent()) return;
        List<OriginalDismissals.Delivery> batch = originals.committedBatch(DISMISS_BATCH_SIZE);
        if (batch.isEmpty()) return;
        try {
            String[] keys = new String[batch.size()];
            for (int i = 0; i < keys.length; i++) keys[i] = batch.get(i).key;
            StatusBarNotification[] active = getActiveNotifications(keys);
            if (active != null) {
                for (OriginalDismissals.Delivery delivery : batch) {
                    if (!originals.canDismiss(delivery)) continue;
                    for (StatusBarNotification candidate : active) {
                        // Android only cancels by key, so the lookup and cancellation are not atomic.
                        if (delivery.key.equals(candidate.getKey())
                                && delivery.postTime == candidate.getPostTime()
                                && candidate.isClearable() && !candidate.isOngoing()) {
                            cancelNotification(delivery.key);
                            break;
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            LogUtils.w("delivery phase=original_cancel_failed error=" + e.getClass().getSimpleName());
        } finally {
            for (OriginalDismissals.Delivery delivery : batch) originals.finish(delivery);
        }
        // A long locked-screen backlog must not monopolize the application's main thread.
        if (batch.size() == DISMISS_BATCH_SIZE) scheduleDismissals(16);
    }

    private void clearDismissals() {
        main.removeCallbacks(drainCommitted);
        dismissalScheduled = false;
        originals.reset();
    }

    private void onMain(Runnable action) {
        if (Looper.myLooper() == Looper.getMainLooper()) action.run();
        else main.post(action);
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
        onMain(this::handleListenerConnected);
    }

    private void handleListenerConnected() {
        if (destroyed) return;
        connected = true;
        clearDismissals();
        zenModePolicyKey = "";
        AppStatusConfig.get().register(this);
        ProcessFactory.get().initProcess();
        NotificationHelper.get().bindListenerService(this);
        refreshListenerStatusDot();
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
        onMain(this::handleListenerDisconnected);
    }

    private void handleListenerDisconnected() {
        connected = false;
        refreshListenerStatusDot();
        clearDismissals();
        zenModePolicyKey = "";
        NotificationHelper.get().unBindListenerService(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                NotificationListenerService.requestRebind(
                        new ComponentName(this, MainNoticeListenerService.class));
            } catch (RuntimeException e) {
                LogUtils.w("delivery phase=rebind_failed error=" + e.getClass().getSimpleName());
            }
        }
    }

    void refreshListenerStatusDot() {
        onMain(() -> {
            if (statusDot == null && connected && !destroyed) {
                try {
                    statusDot = new ListenerStatusDot(this, main, this::refreshListenerStatusDot);
                } catch (RuntimeException error) {
                    LogUtils.w("listener_status_dot setup=" + error.getClass().getSimpleName());
                    return;
                }
            }
            if (statusDot != null) statusDot.refresh(connected && !destroyed);
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshListenerStatusDot();
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        connected = false;
        refreshListenerStatusDot();
        clearDismissals();
        NotificationHelper.get().unBindListenerService(this);
        super.onDestroy();
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
