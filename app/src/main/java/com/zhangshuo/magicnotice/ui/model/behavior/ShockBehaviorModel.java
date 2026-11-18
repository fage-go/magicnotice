package com.zhangshuo.magicnotice.ui.model.behavior;

import android.text.TextUtils;

import com.blankj.utilcode.util.VibrateUtils;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;

public class ShockBehaviorModel extends BehaviorModel {
    private String name;
    private String timePattern = "";
    private int count = 1;

    public ShockBehaviorModel() {
        super(BehaviorType.BEHAVIOR_SHOCK);
    }

    public String getTimePattern() {
        return timePattern;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    @Override
    public void exec(NotificationData notificationData) {
        if (TextUtils.isEmpty(timePattern)) return;

        String tmpTimePattern = "";
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                tmpTimePattern = timePattern;
            } else {
                tmpTimePattern = tmpTimePattern + ";500;0;" + timePattern;
            }
        }

        String[] timePatternArray = tmpTimePattern.split(";");
        long[] patterns = new long[timePatternArray.length];
        for (int i = 0; i < timePatternArray.length; i++) {
            patterns[i] = Long.parseLong(timePatternArray[i]);
        }

        VibrateUtils.vibrate(patterns, -1);
        LogUtils.i("ShockBehavior exec ");
    }
}
