package com.zhangshuo.magicnotice.ui.model.conditions;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.config.AppStatusConfig;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;

public class DirectionConditionsModel extends ConditionsModel {

    public DirectionConditionsModel() {
        super(ConditionsType.CONDITIONS_DIRECTION);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isFilter(NotificationInfo notificationInfo) {
        boolean appStatus = AppStatusConfig.get().isPortrait();
        boolean result = appStatus == isFilterType();
        LogUtils.i("DirectionConditions> filterType: " + isFilterType()
                + " AppStatus: " + appStatus
                + " isPortrait: " + isFilterType()
                + " result: " + result);
        return result;
    }
}
