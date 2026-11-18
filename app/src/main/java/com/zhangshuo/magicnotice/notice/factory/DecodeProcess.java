package com.zhangshuo.magicnotice.notice.factory;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.zhangshuo.magicnotice.notice.model.NotificationInfo;

/**
 * 获取通知相关的信息
 */
public class DecodeProcess implements IProcess<StatusBarNotification, NotificationInfo> {


    @Override
    public NotificationInfo onProcess(Context context, StatusBarNotification statusBarNotification) {
        try {
            String packageName = statusBarNotification.getPackageName();
            int noticeId = statusBarNotification.getId();
            String key = statusBarNotification.getKey();
            long postTime = statusBarNotification.getPostTime();
            Notification notification = statusBarNotification.getNotification();
            Bundle extras = notification.extras;
            int progressMax = extras.getInt(Notification.EXTRA_PROGRESS_MAX);
            if (progressMax > 0) {
                //是通知信息，则不处理
                return null;
            }
            CharSequence charTitle = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence charContent = extras.getCharSequence(Notification.EXTRA_TEXT);
            String title = "";
            String content = "";
            try {
                title = charTitle.toString();
            } catch (Exception e) {
            }

            try {
                content = charContent.toString();
            } catch (Exception e) {
            }
            if (!TextUtils.isEmpty(content)) {
                content = content.replace("\n", "\t\t");
            }
            PendingIntent pendingIntent = notification.contentIntent;
            NotificationInfo notificationInfo = new NotificationInfo();
            notificationInfo.setPackageName(packageName);
            notificationInfo.setId(String.valueOf(noticeId));
            notificationInfo.setKey(key);
            notificationInfo.setPostTime(postTime);
            notificationInfo.setTitle(title);
            notificationInfo.setContent(content);
            if (notification.getLargeIcon() != null) {
                notificationInfo.setIcon(notification.getLargeIcon());
            } else {
                Icon smallIcon = notification.getSmallIcon();
                notificationInfo.setIcon(smallIcon);
                notificationInfo.setIconColor(notification.color);
            }
            Bitmap pictureBitmap = extras.getParcelable(Notification.EXTRA_PICTURE);
            if (pictureBitmap != null) {
                notificationInfo.setPictureBitmap(pictureBitmap);
            }
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content) && pictureBitmap == null) {
                //如果所有内容都为
                return null;
            }
            notificationInfo.setPendingIntent(pendingIntent);
            try {
                //设置自动回复相关内容
                NotificationCompat.CarExtender mCarExtender = new NotificationCompat.CarExtender(notification);
                NotificationCompat.CarExtender.UnreadConversation unreadConversation = mCarExtender.getUnreadConversation();
                notificationInfo.setUnreadConversation(unreadConversation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return notificationInfo;
        } catch (Exception e) {
            return null;
        }
    }
}
