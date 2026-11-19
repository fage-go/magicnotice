package com.zhangshuo.magicnotice.barrage;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

import java.util.List;

public class BehaviorHelper {

    public static final int TEST_NOTIFICATION_ID = -8888;
    public static void exec(NotificationData notificationData, List<BehaviorModel> behaviorList) {
        if (behaviorList == null) return;
        for (BehaviorModel behavior : behaviorList) {
            if (behavior == null) continue;
            try {
                behavior.exec(notificationData);
            } catch (RuntimeException e) {
                LogUtils.w("delivery phase=action_failed error=" + e.getClass().getSimpleName());
            }
        }
    }

    public static void exec(ConfigRuleModel configRuleModel) {
        if (configRuleModel == null || configRuleModel.getBehaviorList() == null) return;
        NotificationData testNotificationData = getTestNotificationData();
        exec(testNotificationData, configRuleModel.getBehaviorList());
    }

    public static NotificationData getTestNotificationData() {
        NotificationData notificationData = new NotificationData();
        notificationData.setId(TEST_NOTIFICATION_ID);
        notificationData.setNoticeId("9999");
        notificationData.setKey("CicadaNotice|9999");
        notificationData.setAppName(StringUtil.getString(R.string.app_name));
        notificationData.setPackageName(AppUtil.get().getApplication().getPackageName());
        notificationData.setRuleId(1);
        notificationData.setRuleName("TestRuleName");
        notificationData.setPostTime(System.currentTimeMillis());
        notificationData.setTitle(StringUtil.getString(R.string.app_name));
        notificationData.setContent(StringUtil.getString(R.string.barrage_tip_content_preview));
        return notificationData;
    }

    public static NotificationData getPreviewNotificationData(String title,String content){
        NotificationData notificationData = new NotificationData();
        notificationData.setId(TEST_NOTIFICATION_ID);
        notificationData.setNoticeId("8888");
        notificationData.setKey("CicadaNotice|8888");
        notificationData.setAppName(StringUtil.getString(R.string.app_name));
        notificationData.setPackageName(AppUtil.get().getApplication().getPackageName());
        notificationData.setRuleId(0);
        notificationData.setRuleName(StringUtil.getString(R.string.toast_barrage_preview_rule));
        notificationData.setPostTime(System.currentTimeMillis());
        notificationData.setTitle(title);
        notificationData.setContent(content);
        return notificationData;
    }
}
