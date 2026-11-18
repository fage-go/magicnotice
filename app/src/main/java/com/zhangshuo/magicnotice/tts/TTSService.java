package com.zhangshuo.magicnotice.tts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.broadcast.TTSReceiver;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.behavior.BroadcastBehaviorModel;

public class TTSService extends Service {
    public static final String BROADCAST_KEY_RESUME = "broadcast_resume";

    public static final String BROADCAST_KEY_NEXT = "broadcast_next";
    public static final String BROADCAST_KEY_START = "broadcast_start";

    private RemoteViews remoteViews;
    private Notification notification;
    private int NOTIFICATION_ID = 1;
    private NotificationManager manager;
    private TTSReceiver ttsReceiver;
    private boolean isNotify = false;//是否有通知正在展示
    private boolean isPlaying = false;//是否正在播报
    private boolean isForeground = false;
    private long speechGeneration = 0;
    private TTSInfo lastTTSInfo = null;

    public class TTSBinder extends Binder {
        public TTSService getService() {
            return TTSService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TTSBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化RemoteViews配置
        initRemoteViews();
        //初始化通知
        initNotification();
        //注册动态广播
        registReceiver();
        //更新当前的view
        updateNotificationShow(BROADCAST_KEY_START);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void initRemoteViews() {
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.layout_notification);
        //通知栏控制器下一首按钮广播操作
        Intent intentNext = new Intent(BROADCAST_KEY_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, intentNext, 0);
        //为next控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.ll_content, nextPendingIntent);
    }

    /**
     * 初始化通知
     */
    private void initNotification() {
        String channelId = "CicadaNotice";
        String channelName = StringUtil.getString(R.string.notice_tts_channel_name);
        if (manager == null) {
            manager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
        }
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, channelName, importance);
        }

        Intent intentPlay = new Intent(BROADCAST_KEY_NEXT);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, 0);
        remoteViews.setOnClickPendingIntent(R.id.ll_next, playPendingIntent);

        Intent intentResume = new Intent(BROADCAST_KEY_RESUME);
        PendingIntent resumePendingIntent = PendingIntent.getBroadcast(this, 0, intentResume, 0);
        remoteViews.setOnClickPendingIntent(R.id.ll_content, resumePendingIntent);

        //初始化通知
        notification = new NotificationCompat.Builder(this, channelId)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        manager.createNotificationChannel(channel);
    }


    public void updateNotificationShow(String auction) {
        LogUtils.i("updateNotificationShow>" + auction);
        if (BROADCAST_KEY_RESUME.equals(auction)) {
            if (isPlaying) {
                //如果正在播放,则暂停
                invalidateSpeech();
                TTSHelper.getInstance().stopSpeech();
                remoteViews.setTextViewText(R.id.tv_title_tip, StringUtil.getString(R.string.tts_notice_click_resume));
                manager.notify(NOTIFICATION_ID, notification);
                isPlaying = false;
            } else {
                boolean speechStarted = false;
                if (lastTTSInfo != null) {
                    speechStarted = startSpeech(lastTTSInfo);
                }
                if (!speechStarted) {
                    isPlaying = false;
                    return;
                }
                remoteViews.setTextViewText(R.id.tv_title_tip, StringUtil.getString(R.string.tts_notice_click_stop));
                manager.notify(NOTIFICATION_ID, notification);
                isPlaying = true;
            }
        } else if (BROADCAST_KEY_NEXT.equals(auction) || BROADCAST_KEY_START.equals(auction)) {
            invalidateSpeech();
            TTSHelper.getInstance().stopSpeech();
            TTSInfo nextTTSInfo = TTSHelper.getInstance().getTopTTS();
            if (nextTTSInfo == null || remoteViews == null) {
                try {
                    manager.cancel(NOTIFICATION_ID);
                    if (isForeground) {
                        stopForeground(true);
                        isForeground = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isNotify = false;
                isPlaying = false;
            } else {
                if (!TTSHelper.getInstance().canTextToSpeech()) {
                    TTSHelper.getInstance().initTextToSpeech(null);
                    isNotify = false;
                    isPlaying = false;
                    return;
                }
                //如果有数据
                isNotify = true;

                boolean canAutoPlay = true;
                if (!isPlaying && BROADCAST_KEY_START.equals(auction)) {
                    //如果没有通知在播放的情况下，判断是否可以播放
                    BroadcastBehaviorModel behaviorModel = nextTTSInfo.getBehaviorModel();
                    canAutoPlay = behaviorModel.isAutoPlay();
                }

                int ttsSize = TTSHelper.getInstance().getTTSSize();
                String nextTip = "";
                if (canAutoPlay) {
                    //如果可以播放，则显示下一个
                    int nextTTSSize = ttsSize - 1;
                    if (nextTTSSize > 0) {
                        nextTip = StringUtil.getString(R.string.tts_next_tip) + "\n(" + nextTTSSize + ")";
                    } else {
                        nextTip = StringUtil.getString(R.string.tts_next_tip_done);
                    }
                } else {
                    //如果不可以播放，则显示暂停
                    nextTip = StringUtil.getString(R.string.tts_play_tip) + "\n(" + ttsSize + ")";
                }
                remoteViews.setTextViewText(R.id.tv_next_tip, nextTip);
                NotificationData notificationData = nextTTSInfo.getNotificationData();
                String content = "【" + notificationData.getAppName() + "】"
                        + notificationData.getTitle() + ": " + notificationData.getContent();
                remoteViews.setTextViewText(R.id.tv_content, content);
                startForegroundIfNeeded();
                manager.notify(NOTIFICATION_ID, notification);

                if (!canAutoPlay) {
                    lastTTSInfo = nextTTSInfo;
                    isPlaying = false;
                    return;
                }

                lastTTSInfo = nextTTSInfo;
                boolean speechStarted = startSpeech(lastTTSInfo);
                if (!speechStarted) {
                    isPlaying = false;
                    lastTTSInfo = null;
                    TTSHelper.getInstance().remoteTopTTS();
                    updateNotificationShow(BROADCAST_KEY_NEXT);
                    return;
                }
                TTSHelper.getInstance().remoteTopTTS();
                isPlaying = true;
            }
        }
    }

    public void updateNextTip() {
        if (notification == null || !isNotify()) return;
        int ttsSize = TTSHelper.getInstance().getTTSSize();
        String nextTip = "";

        if (ttsSize > 0) {
            nextTip = StringUtil.getString(R.string.tts_next_tip) + "\n(" + ttsSize + ")";
        } else {
            nextTip = StringUtil.getString(R.string.tts_next_tip_done);
        }

        remoteViews.setTextViewText(R.id.tv_next_tip, nextTip);
        manager.notify(NOTIFICATION_ID, notification);
    }


    public boolean isNotify() {
        return isNotify;
    }

    private void registReceiver() {
        ttsReceiver = new TTSReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_KEY_RESUME);
        intentFilter.addAction(BROADCAST_KEY_NEXT);
        registerReceiver(ttsReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (isForeground) {
            stopForeground(true);
            isForeground = false;
        }
        super.onDestroy();
        if (ttsReceiver != null) {
            //解除动态注册的广播
            unregisterReceiver(ttsReceiver);
        }
    }

    private void startForegroundIfNeeded() {
        if (isForeground) return;
        try {
            startForeground(NOTIFICATION_ID, notification);
            isForeground = true;
        } catch (Exception e) {
            LogUtils.e("TTS foreground start failed: " + e.getMessage());
        }
    }

    private void invalidateSpeech() {
        speechGeneration++;
    }

    private boolean startSpeech(final TTSInfo ttsInfo) {
        final long generation = ++speechGeneration;
        return TTSHelper.getInstance().startSpeech(ttsInfo.getSpeech(), ttsInfo.getBehaviorModel(), new TTSListener() {
            @Override
            public void onDone(String id) {
                finishSpeech(generation, "done");
            }

            @Override
            public void onError(String id) {
                finishSpeech(generation, "error");
            }
        });
    }

    private void finishSpeech(long generation, String event) {
        if (generation != speechGeneration) {
            LogUtils.i("TTS ignore stale callback: " + event);
            return;
        }
        LogUtils.i("TTS speech finished: " + event);
        isPlaying = false;
        updateNotificationShow(BROADCAST_KEY_NEXT);
    }
}
