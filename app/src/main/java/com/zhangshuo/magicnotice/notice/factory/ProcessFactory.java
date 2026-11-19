package com.zhangshuo.magicnotice.notice.factory;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.os.SystemClock;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;

public class ProcessFactory {
    //解码
    private DecodeProcess decodeProcess;
    //如果需要处理
    private MagicProcess magicProcess;

    public interface DismissalPolicy {
        boolean allowed();
    }

    public interface Completion {
        void persisted(DismissalPolicy dismissOriginal);
    }

    public void onProcess(Context context, StatusBarNotification notification, long received, Completion completion) {
        try {
            NotificationInfo decoded = getDecodeProcess().onProcess(context, notification);
            if (decoded != null) {
                LogUtils.i("delivery phase=decoded callback_ms=" + (SystemClock.elapsedRealtime() - received));
                getMagicProcess().onProcess(decoded, received, completion);
                return;
            }
            LogUtils.i("delivery phase=decode_skipped");
        } catch (RuntimeException e) {
            LogUtils.w("delivery phase=process_failed error=" + e.getClass().getSimpleName());
        }
        completion.persisted(null);
    }

    public void initProcess() {
        getDecodeProcess();
        if (magicProcess == null) getMagicProcess(); // constructor starts the first load
        else magicProcess.refreshConfigRule();
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
