package com.zhangshuo.magicnotice.ui.model;

import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.conditions.AppConditionsModel;
import com.zhangshuo.magicnotice.ui.model.conditions.DirectionConditionsModel;
import com.zhangshuo.magicnotice.ui.model.conditions.HeadsetConditionsModel;
import com.zhangshuo.magicnotice.ui.model.conditions.PhoneConditionsModel;
import com.zhangshuo.magicnotice.ui.model.conditions.ScreenOutConditionsModel;
import com.zhangshuo.magicnotice.ui.model.conditions.TimeConditionsModel;
import com.zhangshuo.magicnotice.ui.model.conditions.TxtConditionsModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.magicnotice.ui.view.configrule.conditions.AppCRFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.conditions.DirectionCRFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.conditions.HeadsetCRFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.conditions.PhoneCRFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.conditions.ScreenOutCRFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.conditions.TimeCRFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.conditions.TxtCRFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 条件类型
 * 文字、APP、横竖屏、时间区域
 */
public class ConditionsModel {
    private int type = 0;//-1为新增
    //满足or排查
    private boolean filterType = true;

    public ConditionsModel(int type) {
        this.type = type;
    }

    public boolean isFilterType() {
        return filterType;
    }

    public void setFilterType(boolean filterType) {
        this.filterType = filterType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isFilter(NotificationInfo notificationInfo) {
        return false;
    }

    public TestLogItemInfo getFilterLog(NotificationInfo notificationInfo) {
        TestLogItemInfo testLogItemInfo = new TestLogItemInfo();
        testLogItemInfo.setName(getFilterTip(getType(), isFilterType()));
        testLogItemInfo.setResult(isFilter(notificationInfo));
        testLogItemInfo.setMsg(getFilterTip(Integer.MAX_VALUE));
        return testLogItemInfo;
    }

    public String getFilterTip(int maxLength) {
        return "";
    }

    public @interface ConditionsType {
        int CONDITIONS_TXT = 1000;//文字
        int CONDITIONS_APP = 1001;//app
        int CONDITIONS_DIRECTION = 1002;//方向
        int CONDITIONS_TIME = 1003;//时间区域
        int CONDITIONS_SCREEN_OUT = 1004;//熄屏状态
        int CONDITIONS_HEAD_SET = 1005;//耳机/蓝牙连接
        int CONDITIONS_PHONE_CALL = 1006;//电话提醒
    }

    public static List<Integer> getConditionsTypeList() {
        return Arrays.asList(
                ConditionsType.CONDITIONS_TXT,
                ConditionsType.CONDITIONS_APP,
                ConditionsType.CONDITIONS_TIME,
                ConditionsType.CONDITIONS_HEAD_SET,
                ConditionsType.CONDITIONS_DIRECTION,
                ConditionsType.CONDITIONS_SCREEN_OUT,
                ConditionsType.CONDITIONS_PHONE_CALL
        );
    }

    public static String getConditionsName(int type) {
        switch (type) {
            case ConditionsModel.ConditionsType.CONDITIONS_TXT:
                return StringUtil.getString(R.string.config_rule_c_txt);
            case ConditionsModel.ConditionsType.CONDITIONS_APP:
                return StringUtil.getString(R.string.config_rule_c_app);
            case ConditionsModel.ConditionsType.CONDITIONS_TIME:
                return StringUtil.getString(R.string.config_rule_c_time);
            case ConditionsModel.ConditionsType.CONDITIONS_DIRECTION:
                return StringUtil.getString(R.string.config_rule_c_direction);
            case ConditionsModel.ConditionsType.CONDITIONS_SCREEN_OUT:
                return StringUtil.getString(R.string.config_rule_c_screenout);
            case ConditionsModel.ConditionsType.CONDITIONS_HEAD_SET:
                return StringUtil.getString(R.string.config_rule_c_headset);
            case ConditionsModel.ConditionsType.CONDITIONS_PHONE_CALL:
                return StringUtil.getString(R.string.config_rule_c_phone);
        }
        return StringUtil.getString(R.string.config_rule_c_none);
    }

    public static String getFilterTip(int type, boolean filterType) {
        switch (type) {
            case ConditionsType.CONDITIONS_TXT:
                return filterType ? StringUtil.getString(R.string.c_filter_tip_txt_y) : StringUtil.getString(R.string.c_filter_tip_txt_n);
            case ConditionsType.CONDITIONS_APP:
                return filterType ? StringUtil.getString(R.string.c_filter_tip_app_y) : StringUtil.getString(R.string.c_filter_tip_app_n);
            case ConditionsType.CONDITIONS_DIRECTION:
                return filterType ? StringUtil.getString(R.string.c_filter_tip_direction_y) : StringUtil.getString(R.string.c_filter_tip_direction_n);
            case ConditionsType.CONDITIONS_TIME:
                return filterType ? StringUtil.getString(R.string.c_filter_tip_time_y) : StringUtil.getString(R.string.c_filter_tip_time_n);
            case ConditionsType.CONDITIONS_SCREEN_OUT:
                return filterType ? StringUtil.getString(R.string.c_filter_tip_screen_y) : StringUtil.getString(R.string.c_filter_tip_screen_n);
            case ConditionsType.CONDITIONS_HEAD_SET:
                return filterType ? StringUtil.getString(R.string.c_filter_tip_headset_y) : StringUtil.getString(R.string.c_filter_tip_headset_n);
            case ConditionsType.CONDITIONS_PHONE_CALL:
                return filterType ? StringUtil.getString(R.string.c_filter_tip_phone_y) : StringUtil.getString(R.string.c_filter_tip_phone_n);
        }
        return filterType ? StringUtil.getString(R.string.c_filter_tip_y) : StringUtil.getString(R.string.c_filter_tip_n);
    }

    public static IConfigRule getConditionsView(int type) {
        switch (type) {
            case ConditionsType.CONDITIONS_TXT:
                return new TxtCRFragment();
            case ConditionsType.CONDITIONS_APP:
                return new AppCRFragment();
            case ConditionsType.CONDITIONS_DIRECTION:
                return new DirectionCRFragment();
            case ConditionsType.CONDITIONS_TIME:
                return new TimeCRFragment();
            case ConditionsType.CONDITIONS_SCREEN_OUT:
                return new ScreenOutCRFragment();
            case ConditionsType.CONDITIONS_HEAD_SET:
                return new HeadsetCRFragment();
            case ConditionsType.CONDITIONS_PHONE_CALL:
                return new PhoneCRFragment();
        }
        return null;
    }


    public static List<ConditionsModel> convert(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            List<ConditionsModel> conditionsModelList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int type = jsonObject.getInt("type");
                ConditionsModel conditionsModel = null;
                if (type == ConditionsType.CONDITIONS_TXT) {
                    conditionsModel = GsonUtils.convert(jsonObject.toString(), TxtConditionsModel.class);
                } else if (type == ConditionsType.CONDITIONS_APP) {
                    conditionsModel = GsonUtils.convert(jsonObject.toString(), AppConditionsModel.class);
                } else if (type == ConditionsType.CONDITIONS_DIRECTION) {
                    conditionsModel = GsonUtils.convert(jsonObject.toString(), DirectionConditionsModel.class);
                } else if (type == ConditionsType.CONDITIONS_TIME) {
                    conditionsModel = GsonUtils.convert(jsonObject.toString(), TimeConditionsModel.class);
                } else if (type == ConditionsType.CONDITIONS_SCREEN_OUT) {
                    conditionsModel = GsonUtils.convert(jsonObject.toString(), ScreenOutConditionsModel.class);
                } else if (type == ConditionsType.CONDITIONS_HEAD_SET) {
                    conditionsModel = GsonUtils.convert(jsonObject.toString(), HeadsetConditionsModel.class);
                } else if (type == ConditionsType.CONDITIONS_PHONE_CALL) {
                    conditionsModel = GsonUtils.convert(jsonObject.toString(), PhoneConditionsModel.class);
                }
                if (conditionsModel == null) continue;
                conditionsModelList.add(conditionsModel);
            }
            return conditionsModelList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
