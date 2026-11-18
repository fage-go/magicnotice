package com.zhangshuo.magicnotice.storage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;

import java.util.ArrayList;
import java.util.List;

public class Notificationer {
    public static final String NOTIFICATION_CLICK_RECEIVER = "cicada_notice_click_action";
    private int ruleId;//通知的id
    private String ruleName;//收纳箱名称
    private List<ItemInfo> itemInfoList = new ArrayList<>();//需要展示的内容
    private String channel = "CicadaNotice_Channel";//通知渠道
    private String channelName = StringUtil.getString(R.string.notice_box_channel_name);//通知渠道名称
    private String group = "CicadaNotice_Channel";
    private NotificationManager notificationManager;


    public Notificationer(int ruleId, String ruleName) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.group = "CicadaNotice_Channel_" + ruleName;
    }

    public void addItemInfo(ItemInfo itemInfo) {
        itemInfoList.add(0, itemInfo);
        notifyInfo();
    }

    public void cleanItemInfo() {
        itemInfoList.clear();
    }

    private void notifyInfo() {
        if (itemInfoList.size() == 0) return;
        //初始化构建渠道
        initCreateChannel();
        //创建InBox通知
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (int i = 0; i < itemInfoList.size() && i < 5; i++) {
            ItemInfo itemInfo = itemInfoList.get(i);
            String lineInfo = itemInfo.getTitle() + ": " + itemInfo.getContent();
            inboxStyle.addLine(lineInfo);
        }
        if (itemInfoList.size() > 5) {
            inboxStyle.addLine(String.format(StringUtil.getString(R.string.notification_over_num), itemInfoList.size() - 5));
        }
        String contentText = itemInfoList.get(0).getTitle() + ": " + itemInfoList.get(0).getContent();
        Intent intent = new Intent(NOTIFICATION_CLICK_RECEIVER);
        intent.putExtra("ruleId", ruleId);
        intent.putExtra("unReadNum", itemInfoList.size());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AppUtil.get().getApplication(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(AppUtil.get().getApplication(), channel)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(ruleName + "【" + itemInfoList.size() + " News】")
                .setContentText(contentText)
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .build();
        getNotificationManager().notify(ruleId, notification);
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) AppUtil.get().getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private void initCreateChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            getNotificationManager().createNotificationChannel(notificationChannel);
        }
    }


    public static class ItemInfo {
        private String title;
        private String content;

        public ItemInfo(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
