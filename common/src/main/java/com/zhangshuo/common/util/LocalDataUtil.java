package com.zhangshuo.common.util;

import android.content.Context;

import com.tencent.mmkv.MMKV;


public class LocalDataUtil {
    public static final String BKEY_PRIVATE = "key_private";

    public void saveData(String key, String value) {
        MMKV.defaultMMKV().putString(key, value);
    }

    public void saveData(String key, boolean value) {
        MMKV.defaultMMKV().putBoolean(key, value);
    }

    public void saveData(String key, long value) {
        MMKV.defaultMMKV().putLong(key, value);
    }

    public void saveData(String key, float value) {
        MMKV.defaultMMKV().putFloat(key, value);
    }

    public void saveData(String key, int value) {
        MMKV.defaultMMKV().putInt(key, value);
    }

    public String getData(String key, String defaultValue) {
        return MMKV.defaultMMKV().getString(key, defaultValue);
    }

    public boolean getData(String key, boolean defaultValue) {
        return MMKV.defaultMMKV().getBoolean(key, defaultValue);
    }

    public long getData(String key, long defaultValue) {
        return MMKV.defaultMMKV().getLong(key, defaultValue);
    }

    public float getData(String key, float defaultValue) {
        return MMKV.defaultMMKV().getFloat(key, defaultValue);
    }

    public int getData(String key, int defaultValue) {
        return MMKV.defaultMMKV().getInt(key, defaultValue);
    }


    public void init(Context context) {
        MMKV.initialize(context);
    }

    public static LocalDataUtil get() {
        return Holder.instance;
    }

    private LocalDataUtil() {

    }

    public static final class Holder {
        private static final LocalDataUtil instance = new LocalDataUtil();
    }
}
