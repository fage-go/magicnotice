package com.zhangshuo.magicnotice.ui.model.behavior;

import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.storage.NotificationBoxHelper;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;

public class NotificationBehaviorModel extends BehaviorModel {
    public NotificationBehaviorModel() {
        super(BehaviorType.BEHAVIOR_NOTIFICATION);
    }

    public static NotificationBehaviorModel getDefault() {
        return new NotificationBehaviorModel();
    }

    @Override
    public void exec(NotificationData notificationData) {
        NotificationBoxHelper.get().displayNotification(notificationData);
    }
}
