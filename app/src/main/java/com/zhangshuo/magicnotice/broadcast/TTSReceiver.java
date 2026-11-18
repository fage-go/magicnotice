package com.zhangshuo.magicnotice.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhangshuo.magicnotice.tts.TTSHelper;
import com.zhangshuo.magicnotice.tts.TTSService;

public class TTSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //UI控制
        if (TTSService.BROADCAST_KEY_NEXT.equals(intent.getAction())
                || TTSService.BROADCAST_KEY_RESUME.equals(intent.getAction())) {
            TTSService.TTSBinder ttsBinder = TTSHelper.getInstance().getTTSBinder();
            if (ttsBinder != null) {
                ttsBinder.getService().updateNotificationShow(intent.getAction());
            }
        }
    }
}
