package com.zhangshuo.magicnotice.notice.model;

import android.app.PendingIntent;

import androidx.core.app.NotificationCompat;

public class PendingInfo {
    private String packageName;
    private String notifyKey;
    private PendingIntent pendingIntent;
    private NotificationCompat.CarExtender.UnreadConversation unreadConversation;

    public PendingInfo(String packageName, String notifyKey, PendingIntent pendingIntent, NotificationCompat.CarExtender.UnreadConversation unreadConversation) {
        this.packageName = packageName;
        this.notifyKey = notifyKey;
        this.pendingIntent = pendingIntent;
        this.unreadConversation = unreadConversation;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getNotifyKey() {
        return notifyKey;
    }

    public void setNotifyKey(String notifyKey) {
        this.notifyKey = notifyKey;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public NotificationCompat.CarExtender.UnreadConversation getUnreadConversation() {
        return unreadConversation;
    }

    public void setUnreadConversation(NotificationCompat.CarExtender.UnreadConversation unreadConversation) {
        this.unreadConversation = unreadConversation;
    }
}
