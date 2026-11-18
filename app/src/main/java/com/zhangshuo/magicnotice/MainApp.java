package com.zhangshuo.magicnotice;

import androidx.multidex.MultiDexApplication;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.util.ConfigInit;
import com.zhangshuo.magicnotice.util.FingerPrintUtil;

import java.util.Locale;

public class MainApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (ConfigInit.isBuildTypeZH() || Locale.CHINA.equals(Locale.getDefault())) {
            Locale.setDefault(Locale.CHINA);
        } else {
            Locale.setDefault(Locale.ENGLISH);
        }

        AppUtil.get().init(this);
        RouterUtil.get().init(this, RoutePaths.class);
        FingerPrintUtil.get().init();
        NotificationHelper.get().refreshKeepAliveNotification(this);
    }
}
