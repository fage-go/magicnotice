package com.zhangshuo.magicnotice.storage;

import android.util.ArrayMap;

import com.zhangshuo.magicnotice.db.model.NotificationData;

public class NotificationBoxHelper {
    private ArrayMap<Integer, Notificationer> notificationerMap = new ArrayMap<>();

    public void displayNotification(NotificationData notificationData) {
        int ruleId = notificationData.getRuleId();
        Notificationer notificationer = notificationerMap.get(ruleId);
        if (notificationer == null) {
            notificationer = new Notificationer(notificationData.getRuleId(),
                    notificationData.getRuleName());
            notificationerMap.put(ruleId, notificationer);
        }
        notificationer.addItemInfo(new Notificationer.ItemInfo(
                "【" + notificationData.getAppName() + "】" + notificationData.getTitle(),
                notificationData.getContent()
        ));
    }

    public void cleanNotification(int ruleId) {
        notificationerMap.remove(ruleId);
    }

    public static NotificationBoxHelper get() {
        return Holder.instance;
    }

    private static final class Holder {
        private static final NotificationBoxHelper instance = new NotificationBoxHelper();
    }

}
