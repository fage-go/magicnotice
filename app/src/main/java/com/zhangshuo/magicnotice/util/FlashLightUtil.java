package com.zhangshuo.magicnotice.util;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.text.TextUtils;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;

import java.util.concurrent.Callable;

public class FlashLightUtil {
    private boolean isRunning = false;
    private CameraManager manager;

    public void start(String timePattern) {
        if (TextUtils.isEmpty(timePattern)) return;
        if (isRunning) return;
        isRunning = true;
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    String[] splitArray = timePattern.split(";");
                    for (int i = 0; i < splitArray.length / 2; i++) {
                        String onTime = splitArray[i * 2];
                        String offTime = splitArray[i * 2 + 1];
                        openFlashLight();
                        Thread.sleep(Long.parseLong(onTime));
                        closeFlashLight();
                        Thread.sleep(Long.parseLong(offTime));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeFlashLight();
                    isRunning = false;
                }
                return true;
            }
        }, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isRunning = false;
            }
        });
    }

    public void openFlashLight() {
        try {
            getCameraManager().setTorchMode("0", true);// "0"是主闪光灯
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeFlashLight() {
        try {
            getCameraManager().setTorchMode("0", false);// "0"是主闪光灯
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraManager getCameraManager() {
        if (manager == null) {
            manager = (CameraManager) AppUtil.get().getApplication().getSystemService(Context.CAMERA_SERVICE);
        }
        return manager;
    }


    public static FlashLightUtil get() {
        return Holder.instance;
    }

    private FlashLightUtil() {

    }

    private static final class Holder {
        private static final FlashLightUtil instance = new FlashLightUtil();
    }

}
