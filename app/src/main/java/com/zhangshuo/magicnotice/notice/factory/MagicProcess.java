package com.zhangshuo.magicnotice.notice.factory;

import android.app.PendingIntent;
import android.content.Context;

import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.magicnotice.barrage.BehaviorHelper;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.db.service.TabDataService;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.TabInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 处理数据
 */
public class MagicProcess implements IProcess<NotificationInfo, Boolean> {
    private List<ConfigRuleModel> closeConfigRuleModelList = new LinkedList<>();
    private List<ConfigRuleModel> openConfigRuleModelList = new LinkedList<>();
    private List<TabInfo> tabInfoList = new ArrayList<>();

    public MagicProcess() {
        refreshConfigRule();
        refreshTabData();
    }

    public void refreshConfigRule() {
        ConfigRuleDataService.findConfigRuleList(new AsyncCallback<List<ConfigRuleModel>>() {
            @Override
            public void onSuccess(List<ConfigRuleModel> result) {
                closeConfigRuleModelList.clear();
                openConfigRuleModelList.clear();
                for (int i = 0; i < result.size(); i++) {
                    ConfigRuleModel configRuleModel = result.get(i);
                    if (configRuleModel.isOpen()) {
                        openConfigRuleModelList.add(configRuleModel);
                    } else {
                        closeConfigRuleModelList.add(configRuleModel);
                    }
                }
            }
        });
    }

    public void refreshTabData() {
        TabDataService.findActTab(new AsyncCallback<List<TabInfo>>() {
            @Override
            public void onSuccess(List<TabInfo> result) {
                tabInfoList.clear();
                tabInfoList.addAll(result);
            }
        });
    }

    public List<TabInfo> getTabInfoList() {
        return tabInfoList;
    }

    public ConfigRuleModel getConfigRuleModelById(int ruleId) {
        for (ConfigRuleModel configRuleModel : openConfigRuleModelList) {
            if (ruleId == configRuleModel.getId()) {
                return configRuleModel;
            }
        }
        for (ConfigRuleModel configRuleModel : closeConfigRuleModelList) {
            if (ruleId == configRuleModel.getId()) {
                return configRuleModel;
            }
        }
        return null;
    }

    @Override
    public Boolean onProcess(Context context, NotificationInfo notificationInfo) {
        //判断有没有命中
        String packageName = notificationInfo.getPackageName();
        ConfigRuleModel configRuleModel = checkFitStatus(notificationInfo);
        String key = notificationInfo.getKey();
        PendingIntent pendingIntent = notificationInfo.getPendingIntent();
        NotificationDataService.insertNotificationInfo(notificationInfo, configRuleModel, new AsyncCallback<NotificationData>() {
            @Override
            public void onSuccess(NotificationData result) {
                //保存pending
                if (pendingIntent != null)
                    NotificationHelper.get().addPendingIntent(
                            packageName,
                            key,
                            pendingIntent,
                            notificationInfo.getUnreadConversation());
                //本地保存成功，则进行命中规则处理
                if (configRuleModel != null) {
                    List<BehaviorModel> behaviorList = configRuleModel.getBehaviorList();
                    BehaviorHelper.exec(result, behaviorList);
                }
                if (result != null) {
                    EventBusUtil.sendEvent(new EventMessage<Integer>(EventCodes.INSERT_NEW_NOTIFICATION, 1));
                }
            }
        });
        return checkNeedRetainNotify(configRuleModel);
    }

    private ConfigRuleModel checkFitStatus(NotificationInfo notificationInfo) {
        ConfigRuleModel resultModel = null;
        if (openConfigRuleModelList.size() > 0) {
            for (ConfigRuleModel configRuleModel : openConfigRuleModelList) {
                //判断免打扰
                if (configRuleModel.isPolicy() && NotificationHelper.get().isZenModePolicy()) {
                    //如果当前的规则需要免打扰，并且当前时免打扰状态下，则忽略判断
                    continue;
                }
                List<ConditionsModel> conditionList = configRuleModel.getConditionList();
                if (conditionList != null && conditionList.size() > 0) {
                    boolean haveHitAll = true;
                    for (int i = 0; i < conditionList.size(); i++) {
                        ConditionsModel conditionsModel = conditionList.get(i);
//                        long filterTime = System.currentTimeMillis();
                        boolean filter = conditionsModel.isFilter(notificationInfo);
//                        LogUtils.i(conditionsModel.getClass().getSimpleName() + ">>>"
//                                + (System.currentTimeMillis() - filterTime));
                        if (!filter) {
                            haveHitAll = false;
                            break;
                        }
                    }
                    if (haveHitAll) {
                        resultModel = configRuleModel;
                        break;
                    }
                } else {
                    //没有条件的情况，则全部拦截
                    resultModel = configRuleModel;
                    break;
                }
            }
        }
        return resultModel;
    }


    //检查是否需要保留原始通知
    private boolean checkNeedRetainNotify(ConfigRuleModel configRuleModel) {
        return configRuleModel != null && !configRuleModel.isRetain();
    }
}
