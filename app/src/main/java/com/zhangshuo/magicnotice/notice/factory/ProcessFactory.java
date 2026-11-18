package com.zhangshuo.magicnotice.notice.factory;

import android.content.Context;
import android.service.notification.StatusBarNotification;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;

public class ProcessFactory {
    //解码
    private DecodeProcess decodeProcess;
    //如果需要处理
    private MagicProcess magicProcess;

    public boolean onProcess(Context context, StatusBarNotification statusBarNotification) {
        boolean isNeedClear = false;
        long startTime = System.currentTimeMillis();
        NotificationInfo notificationInfo = null;
        LogUtils.i("onProcess>" + " start " + System.currentTimeMillis());
        try {
            notificationInfo = getDecodeProcess().onProcess(context, statusBarNotification);
            if (notificationInfo != null) {
                isNeedClear = getMagicProcess().onProcess(context, notificationInfo);
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        LogUtils.i("onProcess>" + " needClear:" + isNeedClear + " end. time: " + (System.currentTimeMillis() - startTime) + "ms");
        return isNeedClear;
    }


    public void initProcess() {
        decodeProcess = new DecodeProcess();
        magicProcess = new MagicProcess();
    }

    public DecodeProcess getDecodeProcess() {
        if (decodeProcess == null) {
            decodeProcess = new DecodeProcess();
        }
        return decodeProcess;
    }

    public MagicProcess getMagicProcess() {
        if (magicProcess == null) {
            magicProcess = new MagicProcess();
        }
        return magicProcess;
    }

    public static ProcessFactory get() {
        return Holder.instance;
    }

    private ProcessFactory() {
    }

    private static final class Holder {
        private static final ProcessFactory instance = new ProcessFactory();
    }
}
