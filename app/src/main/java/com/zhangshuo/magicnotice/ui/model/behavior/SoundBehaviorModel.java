package com.zhangshuo.magicnotice.ui.model.behavior;

import android.text.TextUtils;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.util.RingtoneHelper;

public class SoundBehaviorModel extends BehaviorModel {
    private String ringtoneTitle;
    private String ringtoneUri;
    private int count=1;

    public SoundBehaviorModel() {
        super(BehaviorType.BEHAVIOR_SOUND);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRingtoneTitle() {
        return ringtoneTitle;
    }

    public void setRingtoneTitle(String ringtoneTitle) {
        this.ringtoneTitle = ringtoneTitle;
    }

    public String getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(ringtoneUri);
    }

    @Override
    public void exec(NotificationData notificationData) {
        RingtoneHelper.playRingtone(ringtoneUri);
        LogUtils.i("SoundBehavior exec ");
    }
}
