package com.zhangshuo.magicnotice.config.helper;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenStatusHelper extends StatusHelper<Boolean> {
    private KeyguardManager keyguardManager;
    private ScreenBroadcastReceiver screenBroadcastReceiver;
    public static boolean SCREEN_STATUS = true;

    public void register(Context context) {
        screenBroadcastReceiver = new ScreenBroadcastReceiver();
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            context.registerReceiver(screenBroadcastReceiver, filter);
        } catch (Exception e) {

        }
    }

    public void unRegister(Context context) {
        if (screenBroadcastReceiver != null) {
            try {
                context.unregisterReceiver(screenBroadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            screenBroadcastReceiver = null;
        }
    }

    //true 为解锁，false为锁住
    @Override
    public Boolean refreshStatus(Context context) {
        if (keyguardManager == null) {
            keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        }
        return SCREEN_STATUS && !keyguardManager.isKeyguardLocked();
    }

}
