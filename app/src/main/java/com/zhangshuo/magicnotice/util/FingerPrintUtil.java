package com.zhangshuo.magicnotice.util;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.ui.SplashActivity;
import com.zhangshuo.magicnotice.ui.dialog.FingerPrintDialog;

public class FingerPrintUtil {
    private boolean fingerPrintFlag = false;
    private long lastFigureTime = -1;
    private boolean hasToDesktop = true;
    private boolean isChecked = false;

    public void onAppResume(Activity activity) {
        if (!hasToDesktop) return;
        if (isChecked) return;
        isChecked = true;
        boolean data = LocalDataUtil.get().getData(LocalDataKeys.BKEY_FINGURE_FLAG, false);
        if (data) {
            if (lastFigureTime == 0) return;
            if ((System.currentTimeMillis() - lastFigureTime) > 10 * 1000) {
                FingerPrintDialog fingerPrintDialog = new FingerPrintDialog();
                fingerPrintDialog.setInfo(new FingerPrintDialog.OnFingerAuthCallback() {
                    @Override
                    public void onSuccess() {
                        lastFigureTime = 0;
                        fingerPrintFlag = true;
                        isChecked = false;
                    }

                    @Override
                    public void onFailed() {
                        fingerPrintFlag = false;
                        lastFigureTime = -1;
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        activity.startActivity(intent);
                        isChecked = false;
                    }
                });
                fingerPrintDialog.show(activity);
            }
        }
    }

    public void init() {
        AppUtils.registerAppStatusChangedListener(new Utils.OnAppStatusChangedListener() {
            @Override
            public void onForeground(Activity activity) {
            }

            @Override
            public void onBackground(Activity activity) {
                if (lastFigureTime == 0) {
                    lastFigureTime = System.currentTimeMillis();
                }
                hasToDesktop = true;
            }
        });
        ActivityUtils.addActivityLifecycleCallbacks(new Utils.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                super.onActivityStarted(activity);
                if (activity instanceof SplashActivity) {
                    return;
                }
                onAppResume(activity);
            }

        });
    }

    public boolean isSafe() {
        return fingerPrintFlag;
    }

    public static FingerPrintUtil get() {
        return Holder.instance;
    }

    private FingerPrintUtil() {

    }

    private static final class Holder {
        private static FingerPrintUtil instance = new FingerPrintUtil();
    }
}
