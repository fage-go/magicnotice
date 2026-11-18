package com.zhangshuo.magicnotice.config;

import android.content.Context;
import android.content.res.Configuration;

import com.zhangshuo.magicnotice.config.helper.HeadsetHelper;
import com.zhangshuo.magicnotice.config.helper.PhoneStatusHelper;
import com.zhangshuo.magicnotice.config.helper.ScreenOrientationHelper;
import com.zhangshuo.magicnotice.config.helper.ScreenStatusHelper;
import com.zhangshuo.magicnotice.config.helper.StatusHelper;

import java.util.HashMap;
import java.util.Map;

//**APP状态信息
public class AppStatusConfig {
    private Context mContext;
    private Map<String, StatusHelper> statusHelperMap = new HashMap<>();
    //最后一次更新时间
    private long lastUpdateTime = 0;//避免频繁更新

    public void register(Context context) {
        this.mContext = context;
        //解绑原先的
        for (Map.Entry<String, StatusHelper> entry : statusHelperMap.entrySet()) {
            StatusHelper statusHelper = entry.getValue();
            try {
                statusHelper.unRegister(mContext);
            } catch (Exception e) {

            }
        }
        //重新构建
        statusHelperMap.clear();
        statusHelperMap.put(ScreenStatusHelper.class.getSimpleName(), new ScreenStatusHelper());
        statusHelperMap.put(HeadsetHelper.class.getSimpleName(), new HeadsetHelper());
        statusHelperMap.put(PhoneStatusHelper.class.getSimpleName(), new PhoneStatusHelper());
        statusHelperMap.put(ScreenOrientationHelper.class.getSimpleName(), new ScreenOrientationHelper());
        //重新绑定
        for (Map.Entry<String, StatusHelper> entry : statusHelperMap.entrySet()) {
            StatusHelper statusHelper = entry.getValue();
            try {
                statusHelper.register(mContext);
            } catch (Exception e) {

            }
        }
        //重新刷新 屏幕状态、耳机状态、屏幕方向数据
        for (Map.Entry<String, StatusHelper> entry : statusHelperMap.entrySet()) {
            StatusHelper statusHelper = entry.getValue();
            try {
                statusHelper.onRefresh(mContext);
            } catch (Exception e) {

            }
        }
    }


    public boolean isPortrait() {
        ScreenOrientationHelper statusHelper = (ScreenOrientationHelper) statusHelperMap.get(ScreenOrientationHelper.class.getSimpleName());
        if (statusHelper == null) return true;
        //横竖屏刷新一下
        statusHelper.onRefresh(mContext);
        return statusHelper.getData() != Configuration.ORIENTATION_LANDSCAPE;
    }


    public boolean isScreenUnPresent() {
        if (mContext == null) return true;
        ScreenStatusHelper statusHelper = (ScreenStatusHelper) statusHelperMap.get(ScreenStatusHelper.class.getSimpleName());
        if (statusHelper == null) return true;
        //屏幕锁屏状态需要刷新一下
        statusHelper.onRefresh(mContext);
        return statusHelper.getData();
    }

    public void refreshHeadsetName() {
        if (mContext == null) return;
        HeadsetHelper statusHelper = (HeadsetHelper) statusHelperMap.get(HeadsetHelper.class.getSimpleName());
        statusHelper.onRefresh(mContext);
    }

    public String getHeadsetName() {
        HeadsetHelper statusHelper = (HeadsetHelper) statusHelperMap.get(HeadsetHelper.class.getSimpleName());
        if (statusHelper == null) return "";
        return statusHelper.getData();
    }

    public static AppStatusConfig get() {
        return Holder.instance;
    }

    private AppStatusConfig() {

    }

    public static final class Holder {
        private static final AppStatusConfig instance = new AppStatusConfig();
    }
}
