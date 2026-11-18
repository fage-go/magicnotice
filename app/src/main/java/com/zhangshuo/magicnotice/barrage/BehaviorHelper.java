package com.zhangshuo.magicnotice.barrage;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

import java.util.List;

public class BehaviorHelper {

    public static final int TEST_NOTIFICATION_ID = -8888;
    public static void exec(NotificationData notificationData, List<BehaviorModel> behaviorList) {
        if (behaviorList != null && behaviorList.size() > 0) {
            startExec(notificationData, behaviorList, 0);
        }
    }

    public static void exec(ConfigRuleModel configRuleModel) {
        if (configRuleModel == null || configRuleModel.getBehaviorList() == null) return;
        NotificationData testNotificationData = getTestNotificationData();
        exec(testNotificationData, configRuleModel.getBehaviorList());
    }

    private static void startExec(NotificationData notificationData, List<BehaviorModel> behaviorModelList, int index) {
        if (index >= behaviorModelList.size()) return;
        BehaviorModel behaviorModel = behaviorModelList.get(index);
        behaviorModel.exec(notificationData);
        index++;
        startExec(notificationData, behaviorModelList, index);
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
