package com.zhangshuo.magicnotice.config.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.notice.factory.MagicProcess;
import com.zhangshuo.magicnotice.notice.factory.ProcessFactory;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;

public class PhoneStatusHelper extends StatusHelper<String> {

    private PhoneStateListener phoneStateListener;
    private String phonePackageName = "";


    public PhoneStatusHelper() {

    }

    public void register(Context context) {
        if (phoneStateListener == null) {
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    super.onCallStateChanged(state, phoneNumber);
                    LogUtils.i("onCallStateChanged>>>> " + state + " >> " + phoneNumber);
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING:
                            //来电
                            if (!TextUtils.isEmpty(phoneNumber))
                                judgeConditions(phoneNumber);
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            //挂断
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            //停止
                            break;
                    }
                }
            };
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            Intent infoIntent = new Intent(Intent.ACTION_DIAL);
            ResolveInfo res = context.getPackageManager().resolveActivity(infoIntent, 0);
            if (res != null) {
                phonePackageName = res.activityInfo.packageName;
            }
        }
    }

    private void judgeConditions(String phoneNum) {
        MagicProcess magicProcess = ProcessFactory.get().getMagicProcess();
        if (magicProcess == null) return;
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setKey("magic_notice_phone_call");
        notificationInfo.setTitle(StringUtil.getString(R.string.judge_phone_status));
        notificationInfo.setPostTime(System.currentTimeMillis());
        notificationInfo.setPackageName(phonePackageName);
        notificationInfo.setContent(phoneNum);
        magicProcess.onProcess(AppUtil.get().getApplication(), notificationInfo);
    }

    public void unRegister(Context context) {
        if (phoneStateListener != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public String refreshStatus(Context context) {
        if (TextUtils.isEmpty(phonePackageName)) {
            Intent infoIntent = new Intent(Intent.ACTION_DIAL);
            ResolveInfo res = context.getPackageManager().resolveActivity(infoIntent, 0);
            if (res != null) {
                phonePackageName = res.activityInfo.packageName;
            }
        }
        return phonePackageName;
    }
}
