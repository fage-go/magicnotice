package com.zhangshuo.magicnotice.ui.model.conditions;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.ui.model.PhoneInfo;

import java.util.ArrayList;
import java.util.List;

public class PhoneConditionsModel extends ConditionsModel {
    private List<PhoneInfo> dataList = new ArrayList<>();

    public PhoneConditionsModel() {
        super(ConditionsType.CONDITIONS_PHONE_CALL);
    }

    public List<PhoneInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<PhoneInfo> dataList) {
        this.dataList = dataList;
    }

    @Override
    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    @Override
    public boolean isFilter(NotificationInfo notificationInfo) {
        if (!"magic_notice_phone_call".equals(notificationInfo.getKey())) {
            return false;
        }
        boolean result = false;
        int index = -1;
        String phoneNum = notificationInfo.getContent();
        try {
            for (int i = 0; i < dataList.size(); i++) {
                PhoneInfo phoneInfo = dataList.get(i);
                if (phoneNum.equals(phoneInfo.getPhone())) {
                    index = i;
                    break;
                }
            }
        } catch (Exception e) {

        }

        result = isFilterType() == index > -1;
        LogUtils.i("PhoneConditionsModel> filterType: " + isFilterType()
                + " index: " + index
                + " result: " + result);
        return result;
    }

    @Override
    public String getFilterTip(int maxLength) {
        String filterTip = "";
        StringBuilder stringBuilder = new StringBuilder();
        if (dataList.size() > 0) {
            for (int i = 0; i < dataList.size() && i < maxLength; i++) {
                PhoneInfo phoneInfo = dataList.get(i);
                stringBuilder.append(phoneInfo.getName() + ":" + phoneInfo.getPhone() + "、");
            }
            filterTip = stringBuilder.substring(0, stringBuilder.length() - 1);
            if (dataList.size() >= maxLength) {
                filterTip = filterTip + String.format(StringUtil.getString(R.string.app_conditions_more_phone), dataList.size());
            }
        }
        return filterTip;
    }
}
