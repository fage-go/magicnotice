package com.zhangshuo.common.util;

import android.text.TextUtils;
import android.util.Log;

import com.zhangshuo.common.BuildConfig;


public class LogUtils {

    private static final String LOG_TAG = "MagicNoticeLog";

    public static void v(String msg) {
        log("v", msg);
    }

    public static void i(String msg) {
        log("i", msg);
    }

    public static void d(String msg) {
        log("d", msg);
    }

    public static void w(String msg) {
        log("w", msg);
    }

    public static void e(String msg) {
        log("e", msg);
    }

    public static void e(Exception e) {
        e.printStackTrace();
    }

    private static void log(String t, String msg) {
        if (BuildConfig.DEBUG) {
            if (TextUtils.isEmpty(msg)) {
                msg = "logcat msg is empty";
            }
            int max_str_length = 2001 - t.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                printLog(t, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            printLog(t, msg);
        }
    }

    private static void printLog(String t, String msg) {
        switch (t) {
            case "v":
                Log.v(LOG_TAG, msg);
                break;
            case "i":
                Log.i(LOG_TAG, msg);
                break;
            case "d":
                Log.d(LOG_TAG, msg);
                break;
            case "w":
                Log.w(LOG_TAG, msg);
                break;
            case "e":
                Log.e(LOG_TAG, msg);
                break;
        }
    }
}
