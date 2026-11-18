package com.zhangshuo.magicnotice.util;

import android.text.TextUtils;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.IDUtil;

public class AccountUtil {
    private String deviceUUID;

    public synchronized String getUserId() {
        if (TextUtils.isEmpty(deviceUUID)) {
            deviceUUID = IDUtil.getDeviceUUID(AppUtil.get().getApplication());
        }
        return deviceUUID;
    }

    public static AccountUtil get() {
        return Holder.instance;
    }

    private AccountUtil() {

    }

    public final static class Holder {
        private static final AccountUtil instance = new AccountUtil();
    }
}
