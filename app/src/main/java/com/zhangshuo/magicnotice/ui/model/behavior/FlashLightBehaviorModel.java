package com.zhangshuo.magicnotice.ui.model.behavior;

import android.text.TextUtils;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.util.FlashLightUtil;

public class FlashLightBehaviorModel extends BehaviorModel {
    private String name;
    private String timePattern = "";
    private int count = 1;

    public FlashLightBehaviorModel() {
        super(BehaviorType.BEHAVIOR_FLASH_LIGHT);
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(timePattern);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void exec(NotificationData notificationData) {
        String tmpTimePattern = "";
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                tmpTimePattern = timePattern;
            } else {
                tmpTimePattern = tmpTimePattern + ";" + timePattern;
            }
        }
        FlashLightUtil.get().start(tmpTimePattern);
        LogUtils.i("FlashLightBehavior exec "+tmpTimePattern);
    }
}
