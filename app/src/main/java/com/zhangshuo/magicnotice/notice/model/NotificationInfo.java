package com.zhangshuo.magicnotice.notice.model;


import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;

import androidx.core.app.NotificationCompat;

public class NotificationInfo {
    private String id;
    private String key;
    private long postTime;
    private String title;
    private String packageName;
    private String content;
    private PendingIntent pendingIntent;
    private NotificationCompat.CarExtender.UnreadConversation unreadConversation;

    private Icon icon;
    private int iconColor;
    private Bitmap pictureBitmap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Bitmap getPictureBitmap() {
        return pictureBitmap;
    }

    public void setPictureBitmap(Bitmap pictureBitmap) {
        this.pictureBitmap = pictureBitmap;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public NotificationCompat.CarExtender.UnreadConversation getUnreadConversation() {
        return unreadConversation;
    }

    public void setUnreadConversation(NotificationCompat.CarExtender.UnreadConversation unreadConversation) {
        this.unreadConversation = unreadConversation;
    }
}
