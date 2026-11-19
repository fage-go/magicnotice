package com.zhangshuo.magicnotice.notice.factory;

import android.app.PendingIntent;
import android.os.SystemClock;

import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.LogUtils;
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
public class MagicProcess {
    private List<ConfigRuleModel> closeConfigRuleModelList = new LinkedList<>();
    private List<ConfigRuleModel> openConfigRuleModelList = new LinkedList<>();
    private List<TabInfo> tabInfoList = new ArrayList<>();
    private static final int MAX_PENDING_RULES = 32;
    private final LinkedList<Runnable> pendingRules = new LinkedList<>();
    private final RuleReloadState rules = new RuleReloadState();

    public MagicProcess() {
        refreshConfigRule();
        refreshTabData();
    }

    public void refreshConfigRule() {
        final int generation = rules.begin();
        ConfigRuleDataService.findConfigRuleList(new AsyncCallback<List<ConfigRuleModel>>() {
            @Override
            public void onSuccess(List<ConfigRuleModel> result) {
                if (!rules.finish(generation, true)) return;
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
                finishRuleLoad();
            }

            @Override
            public void onError(Throwable error) {
                if (!rules.finish(generation, false)) return;
                LogUtils.w("delivery phase=rules_load_failed error=" + error.getClass().getSimpleName());
                // Fail open: archive pending posts without using stale policy for side effects.
                finishRuleLoad();
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

    private void finishRuleLoad() {
        while (!pendingRules.isEmpty()) pendingRules.removeFirst().run();
    }

    /** Internal synthetic events have no original Android notification to dismiss. */
    public void onProcess(NotificationInfo info) {
        onProcess(info, SystemClock.elapsedRealtime(), dismissal -> {});
    }

    public void onProcess(NotificationInfo info, long received, ProcessFactory.Completion completion) {
        if (!rules.isLoading()) {
            processWithRules(info, received, completion);
        } else if (pendingRules.size() < MAX_PENDING_RULES) {
            pendingRules.add(() -> processWithRules(info, received, completion));
        } else {
            // Never filter an original based on an incomplete rules snapshot.
            persist(info, null, rules.currentGeneration(), received, completion);
        }
    }

    private void processWithRules(NotificationInfo info, long received, ProcessFactory.Completion completion) {
        int generation = rules.currentGeneration();
        ConfigRuleModel rule = null;
        try {
            if (rules.canAct(generation)) rule = checkFitStatus(info);
        } catch (RuntimeException e) {
            LogUtils.w("delivery phase=rule_match_failed error=" + e.getClass().getSimpleName());
        }
        persist(info, rule, generation, received, completion);
    }

    private void persist(NotificationInfo notificationInfo, ConfigRuleModel rule, int generation, long received,
                         ProcessFactory.Completion completion) {
        final String packageName = notificationInfo.getPackageName();
        final String key = notificationInfo.getKey();
        final PendingIntent pendingIntent = notificationInfo.getPendingIntent();
        NotificationDataService.insertNotificationInfo(notificationInfo, rule, new AsyncCallback<NotificationData>() {
            @Override
            public void onSuccess(NotificationData result) {
                if (result == null) {
                    completion.persisted(null);
                    return;
                }
                LogUtils.i("delivery phase=committed callback_ms=" + (SystemClock.elapsedRealtime() - received));
                if (pendingIntent != null) {
                    NotificationHelper.get().addPendingIntent(packageName, key, pendingIntent,
                            notificationInfo.getUnreadConversation());
                }
                boolean ruleCurrent = rule != null && rules.canAct(generation);
                completion.persisted(ruleCurrent && !rule.isRetain()
                        ? () -> rules.canAct(generation) && !rule.isRetain()
                        : null);
                EventBusUtil.sendEvent(new EventMessage<Integer>(EventCodes.INSERT_NEW_NOTIFICATION, result.getId()));
                if (ruleCurrent) BehaviorHelper.exec(result, rule.getBehaviorList());
            }

            @Override
            public void onError(Throwable error) {
                LogUtils.w("delivery phase=write_failed error=" + error.getClass().getSimpleName());
                completion.persisted(null);
            }
        });
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


}
