package com.zhangshuo.magicnotice.util;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.blankj.utilcode.util.RomUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;

import java.lang.reflect.Method;

public class ActivityFreeformUtil {


    //    32 Android 13.0 (Tiramisu)
    //    31 Android 12.0 (S)
    //    30 Android 11.0 (R)
    //    29 Android 10.0 (Q)
    //    28 Android 9.0 (Pie)
    public static SupportInfo isFreeformSupport(Context context) {
        //小米在miui12 AndroidQ及以上支持
        SupportInfo supportInfo = new SupportInfo();
        boolean support = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FREEFORM_WINDOW_MANAGEMENT)
                || Settings.Global.getInt(context.getContentResolver(), "enable_freeform_support", 0) != 0
                || (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1 && Settings.Global.getInt(context.getContentResolver(), "force_resizable_activities", 0) != 0);
        supportInfo.androidSupport = support;
        supportInfo.specName = RomUtils.getRomInfo().getName();
        if ("unknown".equalsIgnoreCase(supportInfo.specName)) {
            supportInfo.specName = "";
        }
        if (RomUtils.isXiaomi() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            supportInfo.specSupport = true;
            supportInfo.specVersion = ">= Android 11";
            supportInfo.flag = 5;
        } else if (RomUtils.isOppo() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            supportInfo.specSupport = true;
            supportInfo.specVersion = ">= Android 10";
            supportInfo.flag = 100;
        } else if (RomUtils.isOneplus() && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            supportInfo.specSupport = true;
            supportInfo.specVersion = ">= Android 12";
            supportInfo.flag = 100;
        } else if (isHarmonyOS() || ((RomUtils.isHuawei() || isHonor() || isNova())
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
            supportInfo.specSupport = true;
            supportInfo.specVersion = isHarmonyOS() ? "HarmonyOS" : ">= Android 10";
            supportInfo.flag = 102;
        } else if (RomUtils.isVivo() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //vivo 不支持原因：根据解码系统，发现其通过调用发送信息至multiwindowlaunch，该具备权限检测
            supportInfo.specSupport = false;
            supportInfo.specVersion = StringUtil.getString(R.string.freeform_support_no);
            supportInfo.flag = 5;
        } else if (RomUtils.isSamsung() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //三星
            supportInfo.specSupport = true;
            supportInfo.specVersion = ">= Android 10";
            supportInfo.flag = 5;
        } else if (RomUtils.isMeizu() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //魅族
            supportInfo.specSupport = false;
            supportInfo.specVersion = StringUtil.getString(R.string.freeform_support_no);
            supportInfo.flag = 5;
        } else {
            supportInfo.specSupport = false;
            supportInfo.flag = 5;
            supportInfo.specVersion = StringUtil.getString(R.string.freeform_support_app);
        }
        return supportInfo;
    }

    public static class SupportInfo {
        private boolean specSupport;
        private boolean androidSupport;
        private String specName;
        private String specVersion;
        private int flag = 5;

        public boolean isSupport() {
            return specSupport || androidSupport;
        }


        public boolean isSpecSupport() {
            return specSupport;
        }

        public boolean isAndroidSupport() {
            return androidSupport;
        }

        public String getSpecVersion() {
            return specVersion;
        }

        public String getSpecName() {
            return specName;
        }

        public int getFlag() {
            return flag;
        }

        @Override
        public String toString() {
            return "SupportInfo{" +
                    "specSupport=" + specSupport +
                    ", androidSupport=" + androidSupport +
                    ", specName='" + specName + '\'' +
                    ", flag=" + flag +
                    '}';
        }
    }


    public static Bundle getActivityOptions(Context context) {
        try {
            ActivityOptions activityOptions = null;
            SupportInfo freeformSupport = isFreeformSupport(context);
            Log.i("DDDD", "getActivityOptions Have " + Build.VERSION.SDK_INT + " >>> " + freeformSupport);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activityOptions = ActivityOptions.makeBasic();
                Bundle bundle = activityOptions.toBundle();
                bundle.putInt("android.activity.windowingMode", freeformSupport.getFlag());
                if (RomUtils.isXiaomi()) {
                    bundle.putBoolean("extra_miui_freeform_mode", true);
                }
//                bundle.putParcelable("android:activity.launchBounds", new Rect(100, 100, 500, 700));
                return bundle;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("DDDD", "getActivityOptions Null");
        return null;
    }


    public static boolean isHarmonyOS() {
        try {
            Class clz = Class.forName("com.huawei.system.BuildEx");
            Method method = clz.getMethod("getOsBrand");
            return "harmony".equalsIgnoreCase(String.valueOf(method.invoke(clz)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isHonor() {
        String name = RomUtils.getRomInfo().getName();
        return "honor".equalsIgnoreCase(name);
    }

    public static boolean isNova() {
        String name = RomUtils.getRomInfo().getName();
        return "nova".equalsIgnoreCase(name);
    }


}
