package com.zhangshuo.magicnotice.ui.model.conditions;

import android.app.KeyguardManager;
import android.content.Context;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;

public class ScreenOutConditionsModel extends ConditionsModel {

    public ScreenOutConditionsModel() {
        super(ConditionsType.CONDITIONS_SCREEN_OUT);
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isFilter(NotificationInfo notificationInfo) {
        KeyguardManager mKeyguardManager = (KeyguardManager) AppUtil.get().getApplication().getSystemService(Context.KEYGUARD_SERVICE);
        boolean isKeyguardLocked = mKeyguardManager.inKeyguardRestrictedInputMode();
        boolean result = isFilterType() == isKeyguardLocked;
        LogUtils.i("ScreenOutConditions> filterType: " + isFilterType()
                + " AppStatus: " + isKeyguardLocked
                + " isScreenOut: " + isFilterType()
                + " result: " + result);
        return result;
    }
}
