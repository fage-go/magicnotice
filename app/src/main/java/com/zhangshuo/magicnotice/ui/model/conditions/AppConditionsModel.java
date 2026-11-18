package com.zhangshuo.magicnotice.ui.model.conditions;

import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;

import java.util.ArrayList;
import java.util.List;

public class AppConditionsModel extends ConditionsModel {
    private List<String> packageNameList = new ArrayList<>();

    public AppConditionsModel() {
        super(ConditionsType.CONDITIONS_APP);
    }

    public List<String> getPackageNameList() {
        return packageNameList;
    }

    public void setPackageNameList(List<String> packageNameList) {
        this.packageNameList = packageNameList;
    }

    @Override
    public boolean isEmpty() {
        return packageNameList.isEmpty();
    }

    @Override
    public boolean isFilter(NotificationInfo notificationInfo) {
        boolean result = false;
        int index = -1;
        try {
            index = packageNameList.indexOf(notificationInfo.getPackageName());
        } catch (Exception e) {

        }
        result = isFilterType() ? index > -1 : index == -1;
        LogUtils.i("AppConditions> filterType: " + isFilterType()
                + " filter: " + (index > -1)
                + " >>: " + notificationInfo.getPackageName()
                + " result: " + result);
        return result;
    }

    @Override
    public String getFilterTip(int maxLength) {
        String filterTip = "";
        StringBuilder filterLogBuilder = new StringBuilder();
        if (packageNameList.size() > 0) {
            for (int i = 0; i < packageNameList.size() && i < maxLength; i++) {
                String appName = AppUtils.getAppName(packageNameList.get(i));
                if (TextUtils.isEmpty(appName)) continue;
                filterLogBuilder.append(appName).append("、");
            }
            filterTip = filterLogBuilder.substring(0, filterLogBuilder.length() - 1);
            if (packageNameList.size() >= maxLength) {
                filterTip = filterTip + String.format(StringUtil.getString(R.string.app_conditions_more_app), packageNameList.size());
            }
        }
        return filterTip;
    }
}
