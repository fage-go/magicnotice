package com.zhangshuo.magicnotice.ui.model.behavior;

import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.tts.TTSHelper;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;

public class BroadcastBehaviorModel extends BehaviorModel {
    public static final int CONTENT_TYPE_APP = 0x0001;
    public static final int CONTENT_TYPE_TITLE = 0x0002;
    public static final int CONTENT_TYPE_TXT = 0x0004;
    private boolean notice;
    private String languageName = "";
    private float speed = 1f;//如果值为 1，则速率不会变化。 如果值为 0.5，则速率会减慢一半。 如果值为 3，则速率为三倍
    private float voice = 100;// 0.0 到 100.0
    private int volume = 100;
    private int contentType = CONTENT_TYPE_APP | CONTENT_TYPE_TITLE | CONTENT_TYPE_TXT;
    private String prefix;
    private String suffix;
    private boolean autoPlay = true;


    public BroadcastBehaviorModel() {
        super(BehaviorType.BEHAVIOR_BROADCAST);
    }

    public static BroadcastBehaviorModel getDefault() {
        BroadcastBehaviorModel broadcastBehaviorModel = new BroadcastBehaviorModel();
        broadcastBehaviorModel.notice = true;
        broadcastBehaviorModel.speed = 1f;
        broadcastBehaviorModel.voice = 100;
        broadcastBehaviorModel.volume = 100;
        broadcastBehaviorModel.contentType = CONTENT_TYPE_TITLE | CONTENT_TYPE_TXT;
        broadcastBehaviorModel.prefix = null;
        broadcastBehaviorModel.suffix = null;
        broadcastBehaviorModel.autoPlay = true;
        return broadcastBehaviorModel;
    }

    public boolean isNotice() {
        return notice;
    }

    public void setNotice(boolean notice) {
        this.notice = notice;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVoice() {
        int tmpVoice = Math.round(voice);
        tmpVoice = Math.max(0, tmpVoice);
        tmpVoice = Math.min(tmpVoice, 100);
        return tmpVoice;
    }

    public void setVoice(float voice) {
        this.voice = voice;
    }

    public boolean hasContentType(int contentType) {
        return (this.contentType & contentType) == contentType;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public void exec(NotificationData notificationData) {
        super.exec(notificationData);
        TTSHelper.getInstance().addTTSInfo(notificationData, this);
    }
}
