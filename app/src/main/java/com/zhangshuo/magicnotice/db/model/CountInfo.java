package com.zhangshuo.magicnotice.db.model;

import java.text.DecimalFormat;

public class CountInfo {
    private long notificationCount;
    private long appCount;

    public CountInfo() {
    }

    public CountInfo(long notificationCount, long appCount) {
        this.notificationCount = notificationCount;
        this.appCount = appCount;
    }

    public long getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(long notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getNotificationCountStr() {
        if (notificationCount < 1000) {
            return String.valueOf(notificationCount);
        } else {
            DecimalFormat df = new DecimalFormat("#,###");
            return df.format(notificationCount);
        }
    }

    public long getAppCount() {
        return appCount;
    }

    public void setAppCount(long appCount) {
        this.appCount = appCount;
    }
}
