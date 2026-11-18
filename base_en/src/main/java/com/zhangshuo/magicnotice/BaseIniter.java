package com.zhangshuo.magicnotice;

import android.app.Application;
import android.util.Log;

public class BaseIniter {

    public static void preInit(Application application) {
        Log.i("BaseIniter", "en preInit ");
    }

    public static void init(Application application) {
        Log.i("BaseIniter", "en init ");
    }

}
