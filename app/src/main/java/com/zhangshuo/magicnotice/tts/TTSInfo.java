package com.zhangshuo.magicnotice.tts;

import android.text.TextUtils;

import androidx.annotation.Keep;

import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.behavior.BroadcastBehaviorModel;

@Keep
public class TTSInfo {
    private NotificationData notificationData;
    private BroadcastBehaviorModel behaviorModel;

    public TTSInfo() {
    }

    public TTSInfo(NotificationData notificationData, BroadcastBehaviorModel behaviorModel) {
        this.notificationData = notificationData;
        this.behaviorModel = behaviorModel;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    public BroadcastBehaviorModel getBehaviorModel() {
        return behaviorModel;
    }

    public void setBehaviorModel(BroadcastBehaviorModel behaviorModel) {
        this.behaviorModel = behaviorModel;
    }

    public String getSpeech() {
        int contentType = behaviorModel.getContentType();
        boolean hasApp = (contentType & BroadcastBehaviorModel.CONTENT_TYPE_APP) == BroadcastBehaviorModel.CONTENT_TYPE_APP;
        boolean hasTitle = (contentType & BroadcastBehaviorModel.CONTENT_TYPE_TITLE) == BroadcastBehaviorModel.CONTENT_TYPE_TITLE;
        boolean hasTxt = (contentType & BroadcastBehaviorModel.CONTENT_TYPE_TXT) == BroadcastBehaviorModel.CONTENT_TYPE_TXT;

        StringBuilder speechSB = new StringBuilder();

        if (!TextUtils.isEmpty(behaviorModel.getPrefix())) {
            speechSB.append(behaviorModel.getPrefix());
        }

        if (hasApp) {
            speechSB.append(notificationData.getAppName() + ",");
        }
        if (hasTitle) {
            speechSB.append(notificationData.getTitle() + ",");
        }
        if (hasTxt) {
            speechSB.append(notificationData.getContent());
        }
        if (!TextUtils.isEmpty(behaviorModel.getSuffix())) {
            speechSB.append(behaviorModel.getSuffix());
        }

        return speechSB.toString();
    }
}
