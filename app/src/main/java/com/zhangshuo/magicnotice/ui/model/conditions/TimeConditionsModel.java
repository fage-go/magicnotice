package com.zhangshuo.magicnotice.ui.model.conditions;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.util.TimeUtil;

public class TimeConditionsModel extends ConditionsModel {
    private long startTime;
    private long endTime;

    public TimeConditionsModel() {
        super(ConditionsType.CONDITIONS_TIME);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean isEmpty() {
        return startTime <= 0 || endTime <= 0;
    }

    @Override
    public boolean isFilter(NotificationInfo notificationInfo) {
        long postTime = notificationInfo.getPostTime();
        int hmTime = TimeUtil.getHMTime(postTime);
        boolean isContain = startTime <= hmTime && hmTime <= endTime;
        boolean result = isFilterType() == isContain;
        LogUtils.i("TimeConditions> filterType: " + isFilterType()
                + " startTime-endTime: " + startTime + " - " + endTime
                + " postTime: " + postTime
                + " result: " + result);
        return result;
    }

    @Override
    public String getFilterTip(int maxLength) {
        String startTimeStr = TimeUtil.formatHMTime(getStartTime());
        String endTimeStr = TimeUtil.formatHMTime(getEndTime());
        return startTimeStr + StringUtil.getString(R.string.time_conditions_to_tip) + endTimeStr;
    }
}
