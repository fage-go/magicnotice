package com.zhangshuo.common;

import android.app.Application;
import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.LocalDataUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

public class AppUtil {
    private Application application;
    private boolean isPrivacy;

    public void init(Application application) {
        this.application = application;
        initAutoSize();
        LocalDataUtil.get().init(application);
        Utils.init(application);
        isPrivacy = LocalDataUtil.get().getData(LocalDataUtil.BKEY_PRIVATE, false);
        ToastManager.get().init(application);
    }

    public void afterAllowPrivacy() {
        isPrivacy = LocalDataUtil.get().getData(LocalDataUtil.BKEY_PRIVATE, false);
    }

    private void initAutoSize() {
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.PT);
    }

    public static boolean isChineseLanguage() {
        return "zh".equals(Locale.getDefault().getLanguage());
    }

    private String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }


    public Application getApplication() {
        return application;
    }


    public static AppUtil get() {
        return Holder.instance;
    }

    private AppUtil() {

    }

    private static final class Holder {
        public static final AppUtil instance = new AppUtil();
    }
}
