package com.zhangshuo.magicnotice.ui.model.behavior;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;

public class OpenBehaviorModel extends BehaviorModel {
    private boolean autoOpen = true;

    public OpenBehaviorModel() {
        super(BehaviorType.BEHAVIOR_OPEN);
    }


    public boolean isAutoOpen() {
        return autoOpen;
    }

    public void setAutoOpen(boolean autoOpen) {
        this.autoOpen = autoOpen;
    }

    @Override
    public void exec(NotificationData notificationData) {
        try {
            if (autoOpen) {
                NotificationHelper.get().openApp(notificationData, BarrageBehaviorModel.TYPE_CLICK_MSG);
                LogUtils.i("OpenBehavior exec ");
            }
        } catch (Exception e) {

        }

    }
}
