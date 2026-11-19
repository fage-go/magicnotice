package com.zhangshuo.magicnotice.notice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.view.Gravity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.blankj.utilcode.util.AppUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.notice.model.PendingInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.util.ActivityFreeformUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NotificationHelper {
    private static final int MAX_MEMORY_NUM = 1000;
    static final int KEEP_ALIVE_NOTIFICATION_ID = 20001;
    private static final String KEEP_ALIVE_CHANNEL = "CicadaKeepAlive_Channel";
    private final Map<String, PendingInfo> pendingMap = new LinkedHashMap<>();
    private MainNoticeListenerService noticeListenerService;
    private int schedulerNotifyId = 10000;
    private NotificationManager notificationManager;


    public boolean replyNotification(NotificationData notificationData, Context context, String message) {
        PendingInfo pendingInfo = findDirectLink(notificationData);
        if (pendingInfo != null) {
            try {
                NotificationCompat.CarExtender.UnreadConversation unreadConversation = pendingInfo.getUnreadConversation();
                if (unreadConversation != null) {
                    PendingIntent pendingReply = unreadConversation.getReplyPendingIntent();
                    RemoteInput remoteInput = unreadConversation.getRemoteInput();
                    String key = remoteInput.getResultKey();
                    if (pendingReply != null) {
                        Intent localIntent = new Intent();
                        Bundle resultBundle = new Bundle();
                        resultBundle.putString(key, message);
                        RemoteInput.addResultsToIntent(new RemoteInput[]{new RemoteInput.Builder(key).build()}, localIntent, resultBundle);
                        pendingReply.send(context, 0, localIntent);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void openApp(NotificationData notificationData, int clickType) {
        PendingInfo pendingInfo = findDirectLink(notificationData);
        if (pendingInfo != null && pendingInfo.getPendingIntent() != null) {
            try {
                Bundle bundle = null;
                if (clickType == BarrageBehaviorModel.TYPE_CLICK_REPLAY_FREE) {
                    bundle = ActivityFreeformUtil.getActivityOptions(AppUtil.get().getApplication());
                }
                pendingInfo.getPendingIntent().send(null, 0, null, null, null,
                        null, bundle);
            } catch (PendingIntent.CanceledException e) {
                AppUtils.launchApp(notificationData.getPackageName());
            }
        } else {
            AppUtils.launchApp(notificationData.getPackageName());
        }
    }


    public synchronized PendingInfo findDirectLink(NotificationData notificationData) {
        try {
            return pendingMap.get(notificationData.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public synchronized void addPendingIntent(String packageName, String notificationKey, PendingIntent pendingIntent,
                                               NotificationCompat.CarExtender.UnreadConversation unreadConversation) {
        if (pendingIntent == null) return;
        PendingInfo pendingInfo = new PendingInfo(packageName, notificationKey, pendingIntent, unreadConversation);
        // A notification key identifies one notification. Keep only its latest PendingIntent.
        pendingMap.remove(notificationKey);
        pendingMap.put(notificationKey, pendingInfo);
        trimPendingIntents();
        LogUtils.i("SavePendingIntent: " + packageName + ":" + notificationKey + " size:" + pendingMap.size());
    }

    /**
     * Keep a global limit across all applications and remove the oldest unique keys first.
     */
    private void trimPendingIntents() {
        while (pendingMap.size() > MAX_MEMORY_NUM) {
            Iterator<String> iterator = pendingMap.keySet().iterator();
            if (!iterator.hasNext()) {
                break;
            }
            iterator.next();
            iterator.remove();
        }
    }

    /**
     * 判断服务是否打开
     */
    public static boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        return packageNames.contains(context.getPackageName());
    }

    /**
     * 重置状态
     */
    public static void reEnableSetting(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, MainNoticeListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(context, MainNoticeListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * 打开通知服务设置
     */
    public static void openNotificationListenSettings(Context context) {
        try {
            Intent intent;
            intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openNotification(Context context) {
        String appName = StringUtil.getString(R.string.app_name);
        SpannableString spannableString = new SpannableString(
                String.format(StringUtil.getString(R.string.notification_power_tip), appName)
        );
        CommonDialog.newInstance()
                .setContentGravity(Gravity.LEFT)
                .setContent(spannableString)
                .setCommonAction(StringUtil.getString(R.string.notification_power_tip_auction))
                .setCommonActionListener(new CommonDialog.ActionListener() {
                    @Override
                    public void onClick(CommonDialog dialog) {
                        NotificationHelper.openNotificationListenSettings(context);
                    }
                })
                .setLeftAction(StringUtil.getString(R.string.notification_power_tip_cancel))
                .show(context);
    }


    public void notifyRemind(Context context, String title, String content, String mockPackage) {
        String channel = "CicadaScheduler_Channel";
        String channelName = StringUtil.getString(R.string.notify_channel_name);
        schedulerNotifyId++;
        NotificationManager notificationManager = getNotificationManager(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        int smallIconId = R.mipmap.ic_launcher;
//        if (!TextUtils.isEmpty(mockPackage)) {
//            smallIconId = AppUtils.getAppIconId(mockPackage);
//        }
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(content);
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, channel)
                .setContentIntent(pendingIntent)
                .setSmallIcon(smallIconId)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .build();
        notificationManager.notify(schedulerNotifyId, notification);
    }

    /** Reconcile only the listener already bound by Android, never start a service. */
    public void refreshListenerForeground(boolean activityResumed) {
        MainNoticeListenerService listener = noticeListenerService;
        if (listener != null) listener.refreshBoundForeground(activityResumed);
    }

    Notification createKeepAliveNotification(Context context) {
        NotificationManager notificationManager = getNotificationManager(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    KEEP_ALIVE_CHANNEL,
                    StringUtil.getString(R.string.keep_alive_channel_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            launchIntent = new Intent();
        }
        launchIntent.setPackage(context.getPackageName());
        int pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlag |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, pendingIntentFlag);

        Notification notification = new NotificationCompat.Builder(context, KEEP_ALIVE_CHANNEL)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(StringUtil.getString(R.string.keep_alive_notice_title))
                .setContentText(StringUtil.getString(R.string.keep_alive_notice_content))
                .setOngoing(true)
                .setAutoCancel(false)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        return notification;
    }

    public NotificationManager getNotificationManager(Context context) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }


    public void cleanWaitNotice() {
        if (noticeListenerService != null) {
            noticeListenerService.cleanNeedNoticeList();
        }
    }

    public void bindListenerService(MainNoticeListenerService service) {
        this.noticeListenerService = service;
    }

    public void unBindListenerService(MainNoticeListenerService service) {
        if (noticeListenerService == service) unBindListenerService();
    }

    public void unBindListenerService() {
        this.noticeListenerService = null;
    }


    public boolean isZenModePolicy() {
        if (noticeListenerService != null) {
            return noticeListenerService.isZenMode();
        }
        return false;
    }

    public static NotificationHelper get() {
        return Holder.instance;
    }

    private NotificationHelper() {

    }

    private static final class Holder {
        private static final NotificationHelper instance = new NotificationHelper();
    }
}
