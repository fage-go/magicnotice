package com.zhangshuo.magicnotice;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.ancient.app_test.BuildConfig;

import com.umeng.commonsdk.UMConfigure;

import java.nio.charset.StandardCharsets;

public class BaseIniter {

    private static final String APP_ID = new String(
            Base64.decode(BuildConfig.OPTIONAL_UMENG_APP_ID_B64, Base64.DEFAULT), StandardCharsets.UTF_8);
    private static String channelName;

    public static void preInit(Application application) {
        if (APP_ID.isEmpty()) return;
        channelName = getMetaDataValue(application, "BUGLY_APP_CHANNEL");
        Log.i("BaseIniter", "zh preInit channelName: " + channelName);
        UMConfigure.preInit(application, APP_ID, channelName);
    }

    public static void init(Application application) {
        if (APP_ID.isEmpty()) return;
        UMConfigure.init(application, APP_ID, channelName, UMConfigure.DEVICE_TYPE_PHONE, "");
        Log.i("BaseIniter", "zh init");
    }


    public static String getMetaDataValue(Application application, String metaDataName) {
        PackageManager pm = application.getPackageManager();
        ApplicationInfo appinfo;
        String metaDataValue = "";
        try {
            appinfo = pm.getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appinfo.metaData;
            metaDataValue = metaData.getString(metaDataName);
            return metaDataValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataValue;
    }
}
