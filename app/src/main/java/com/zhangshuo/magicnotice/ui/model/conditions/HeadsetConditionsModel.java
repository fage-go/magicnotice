package com.zhangshuo.magicnotice.ui.model.conditions;

import android.text.TextUtils;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.AppStatusConfig;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.ui.model.HeadsetSelectedInfo;

import java.util.ArrayList;
import java.util.List;

public class HeadsetConditionsModel extends ConditionsModel {
    private List<HeadsetSelectedInfo> dataList = new ArrayList<>();

    public HeadsetConditionsModel() {
        super(ConditionsType.CONDITIONS_HEAD_SET);
    }

    public List<HeadsetSelectedInfo> getDataList() {
        return dataList;
    }

    @Override
    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    @Override
    public boolean isFilter(NotificationInfo notificationInfo) {
        boolean result = false;
        String headsetName = AppStatusConfig.get().getHeadsetName();
        try {
            if (!TextUtils.isEmpty(headsetName)) {
                for (HeadsetSelectedInfo info : dataList) {
                    if (info.getValue().equals(headsetName)) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {

        }
        LogUtils.i("HeadsetConditions> filterType: " + isFilterType()
                + " headsetName: " + headsetName
                + " index: " + isFilterType()
                + " result: " + result);
        return isFilterType() == result;
    }

    @Override
    public String getFilterTip(int maxLength) {
        String filterTip = "";
        StringBuilder stringBuilder = new StringBuilder();
        if (dataList.size() > 0) {
            for (int i = 0; i < dataList.size() && i < maxLength; i++) {
                HeadsetSelectedInfo info = dataList.get(i);
                stringBuilder.append(info.getName()).append("、");
            }
            filterTip = stringBuilder.substring(0, stringBuilder.length() - 1);
            if (dataList.size() >= maxLength) {
                filterTip = filterTip + String.format(StringUtil.getString(R.string.app_conditions_more_device), dataList.size());
            }
        }
        return filterTip;
    }
}
