package com.zhangshuo.magicnotice.barrage;

import androidx.annotation.Keep;

import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;

@Keep
public class BarrageInfo {
    private int count = 0;
    private NotificationData notificationData;
    private BarrageBehaviorModel behaviorModel;

    public BarrageInfo(NotificationData notificationData, BarrageBehaviorModel behaviorModel) {
        this.notificationData = notificationData;
        this.behaviorModel = behaviorModel;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public BarrageBehaviorModel getBehaviorModel() {
        return behaviorModel;
    }

    public void setBehaviorModel(BarrageBehaviorModel behaviorModel) {
        this.behaviorModel = behaviorModel;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
