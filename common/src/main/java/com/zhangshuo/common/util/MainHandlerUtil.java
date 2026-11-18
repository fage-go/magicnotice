package com.zhangshuo.common.util;


import android.os.Handler;
import android.os.Looper;

public class MainHandlerUtil {


    private Handler handler;

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    public void post(Runnable runnable) {
        getHandler().post(runnable);
    }


    public void post(Runnable runnable, long delay) {
        getHandler().postDelayed(runnable, delay);
    }


    private static MainHandlerUtil instance;

    private static class Holder {
        static final MainHandlerUtil holder = new MainHandlerUtil();
    }

    public static MainHandlerUtil get() {
        if (instance == null) {
            instance = MainHandlerUtil.Holder.holder;
        }
        return instance;
    }


    private MainHandlerUtil() {

    }
}
