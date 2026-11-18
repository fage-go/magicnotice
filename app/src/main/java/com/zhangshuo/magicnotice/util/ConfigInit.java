package com.zhangshuo.magicnotice.util;

import android.provider.Telephony;
import android.text.TextUtils;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.BuildConfig;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.db.service.RemindDataService;
import com.zhangshuo.magicnotice.db.service.TabDataService;
import com.zhangshuo.magicnotice.notice.factory.ProcessFactory;
import com.zhangshuo.magicnotice.scheduler.SchedulerUtil;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.KeywordInfo;
import com.zhangshuo.magicnotice.ui.model.RemindInfo;
import com.zhangshuo.magicnotice.ui.model.RemindTimeInfo;
import com.zhangshuo.magicnotice.ui.model.TabInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.BroadcastBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.CopyBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.ShockBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.conditions.AppConditionsModel;
import com.zhangshuo.magicnotice.ui.model.conditions.DirectionConditionsModel;
import com.zhangshuo.magicnotice.ui.model.conditions.TxtConditionsModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ConfigInit {
    private static boolean haveInit = false;
    private static int smsModeId = 0;
    private static int expressModeId = 0;

    private static String buildType = BuildConfig.FLAVOR;

    public static boolean isBuildTypeZH() {
        if (buildType == null) return true;
        return buildType.equalsIgnoreCase("zh");
    }

    public static void checkAndFirstSchedulerConfig(AsyncCallback<Boolean> callback) {
        if (haveInit) return;
        haveInit = true;
        ConfigRuleModel[] configRuleModels = null;
        AsyncCallback<Boolean> asyncCallback = null;
        if (ConfigInit.isBuildTypeZH() || Locale.CHINA.equals(Locale.getDefault())) {
            //如果是中文
            // 定时通知
            ConfigRuleModel scheduleMode = buildScheduleMode();
            // 快递取件码配置
            ConfigRuleModel expressModel = buildExpressModel();
            // 自动复制验证码
            ConfigRuleModel smsModel = buildAutoCopySmsCode();
            // 横屏游戏时，微信、QQ弹幕提醒
            ConfigRuleModel gameModeModel = buildGameWithInWXAndQQ(true);
            // 非微信、QQ通知时弹幕提醒
            ConfigRuleModel normalModel = buildNormalBarrage();
            configRuleModels = new ConfigRuleModel[]{normalModel, gameModeModel, smsModel, expressModel, scheduleMode};
            asyncCallback = new AsyncCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    smsModeId = smsModel.getId();
                    expressModeId = expressModel.getId();
                    checkAndBuildTab();
                    buildWelcomeScheduleTip();
                    if (callback != null) {
                        callback.onSuccess(result);
                    }
                }
            };
        } else {
            //如果是英文
            // 定时通知
            ConfigRuleModel scheduleMode = buildScheduleMode();
            //普通情况下 弹幕提醒
            ConfigRuleModel normalModel = buildNormalBarrage();
            //语音播报
            ConfigRuleModel barrageModel = buildBroadcast();
            configRuleModels = new ConfigRuleModel[]{scheduleMode, normalModel, barrageModel};
            asyncCallback = new AsyncCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    checkAndBuildTab();
                    buildWelcomeScheduleTip();
                    if (callback != null) {
                        callback.onSuccess(result);
                    }
                }
            };
        }
        ConfigRuleDataService.insertOrUpdate(configRuleModels, asyncCallback);
        LocalDataUtil.get().saveData(LocalDataKeys.BKEY_CONFIG_SCHEDULER, true);
    }


    private static ConfigRuleModel buildExpressModel() {
        ConfigRuleModel expressRuleMode = new ConfigRuleModel();
        expressRuleMode.setRuleName(StringUtil.getString(R.string.config_init_express_model));
        expressRuleMode.setRetain(true);
        expressRuleMode.setTagColor("#0081FF");
        AppConditionsModel smsAppConditionsModel = new AppConditionsModel();
        String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(AppUtil.get().getApplication());
        smsAppConditionsModel.setPackageNameList(Arrays.asList(defaultSmsPackage));
        TxtConditionsModel txtConditionsModel = new TxtConditionsModel();
        txtConditionsModel.setTxtList(Arrays.asList(StringUtil.getString(R.string.config_init_express_way)));
        expressRuleMode.setConditionList(Arrays.asList(smsAppConditionsModel, txtConditionsModel));
        return expressRuleMode;
    }

    private static ConfigRuleModel buildAutoCopySmsCode() {
        ConfigRuleModel smsConfigRuleModel = new ConfigRuleModel();
        smsConfigRuleModel.setRuleName(StringUtil.getString(R.string.config_init_copy_sms));
        smsConfigRuleModel.setRetain(true);
        smsConfigRuleModel.setTagColor("#0081FF");
        String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(AppUtil.get().getApplication());
        AppConditionsModel conditionsModel = new AppConditionsModel();
        conditionsModel.setPackageNameList(Arrays.asList(defaultSmsPackage));
        TxtConditionsModel txtConditionsModel = new TxtConditionsModel();
        txtConditionsModel.setTxtList(Arrays.asList(StringUtil.getStringArray(R.array.first_guide_copy_txt)));
        smsConfigRuleModel.setConditions(Arrays.asList(conditionsModel, txtConditionsModel));
        CopyBehaviorModel copyBehaviorModel = new CopyBehaviorModel();
        copyBehaviorModel.setName(StringUtil.getString(R.string.first_guide_copy_num));
        copyBehaviorModel.setRegular("([0-9]+)");
        smsConfigRuleModel.setBehaviorModel(Arrays.asList(copyBehaviorModel));
        return smsConfigRuleModel;
    }

    private static ConfigRuleModel buildGameWithInWXAndQQ(boolean appFilter) {
        ConfigRuleModel gameConfigRuleModel = new ConfigRuleModel();
        gameConfigRuleModel.setRuleName(StringUtil.getString(R.string.config_init_game_with_in));
        gameConfigRuleModel.setRetain(false);
        gameConfigRuleModel.setTagColor("#8DC63F");

        DirectionConditionsModel directionConditionsModel = new DirectionConditionsModel();
        directionConditionsModel.setFilterType(false);

        if (appFilter) {
            AppConditionsModel appConditionsModel = new AppConditionsModel();
            appConditionsModel.setPackageNameList(Arrays.asList("com.tencent.mm", "com.tencent.mobileqq"));
            gameConfigRuleModel.setConditions(Arrays.asList(directionConditionsModel, appConditionsModel));
        }

        BarrageBehaviorModel barrageBehaviorModel = BarrageBehaviorModel.getDefault();
        barrageBehaviorModel.setBgColorHex("#2E333B");
        barrageBehaviorModel.setTxtColorHex("#39B54A");
        barrageBehaviorModel.setClickType(BarrageBehaviorModel.TYPE_CLICK_CANCEL);
        ActivityFreeformUtil.SupportInfo freeformSupport = ActivityFreeformUtil.isFreeformSupport(AppUtil.get().getApplication());
        if (freeformSupport.isSupport()) {
            barrageBehaviorModel.setDoubleClickType(BarrageBehaviorModel.TYPE_CLICK_REPLAY_FREE);
        } else {
            barrageBehaviorModel.setDoubleClickType(BarrageBehaviorModel.TYPE_CLICK_MSG);
        }
        gameConfigRuleModel.setBehaviorModel(Arrays.asList(barrageBehaviorModel));

        return gameConfigRuleModel;
    }

    private static ConfigRuleModel buildBroadcast() {
        ConfigRuleModel broadcastConfigRuleModel = new ConfigRuleModel();
        broadcastConfigRuleModel.setOpen(false);
        broadcastConfigRuleModel.setRuleName(StringUtil.getString(R.string.config_init_broadcast));
        broadcastConfigRuleModel.setRetain(true);
        broadcastConfigRuleModel.setConditions(new ArrayList<>());
        broadcastConfigRuleModel.setBehaviorModel(Arrays.asList(BroadcastBehaviorModel.getDefault()));
        return broadcastConfigRuleModel;
    }

    private static ConfigRuleModel buildNormalBarrage() {
        ConfigRuleModel configRuleModel = new ConfigRuleModel();
        configRuleModel.setRuleName(StringUtil.getString(R.string.first_guide_barrage_config));
        configRuleModel.setRetain(false);
        configRuleModel.setConditions(new ArrayList<>());
        configRuleModel.setBehaviorModel(Arrays.asList(BarrageBehaviorModel.getDefault()));
        return configRuleModel;
    }

    private static ConfigRuleModel buildScheduleMode() {
        ConfigRuleModel configRuleModel = new ConfigRuleModel();
        configRuleModel.setRuleName(StringUtil.getString(R.string.config_init_schedule_mode));
        configRuleModel.setRetain(true);
        configRuleModel.setTagColor("#2E333B");
        AppConditionsModel conditionsModel = new AppConditionsModel();
        conditionsModel.setFilterType(true);
        conditionsModel.setPackageNameList(Arrays.asList(AppUtil.get().getApplication().getPackageName()));
        configRuleModel.setConditions(Arrays.asList(conditionsModel));
        ShockBehaviorModel shockBehaviorModel = new ShockBehaviorModel();
        shockBehaviorModel.setName(StringUtil.getString(R.string.shock_behavior_2));
        shockBehaviorModel.setTimePattern("1000;200");
        configRuleModel.setBehaviorModel(Arrays.asList(shockBehaviorModel));
        return configRuleModel;
    }


    public static void checkAndCreateSchedulerConfig(AsyncCallback<Boolean> callback) {
        boolean haveConfig = LocalDataUtil.get().getData(LocalDataKeys.BKEY_CONFIG_SCHEDULER, false);
        if (haveConfig) return;
        ConfigRuleModel configRuleModel = buildExpressModel();
        ConfigRuleDataService.insertOrUpdate(new ConfigRuleModel[]{configRuleModel}, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                //排序
                String data = LocalDataUtil.get().getData(LocalDataKeys.SKEY_SORT_IDS, "");
                if (TextUtils.isEmpty(data)) {
                    data = configRuleModel.getId() + ";";
                } else {
                    data = data.replace(configRuleModel.getId() + ";", "");
                    data = configRuleModel.getId() + ";" + data;
                }
                LocalDataUtil.get().saveData(LocalDataKeys.SKEY_SORT_IDS, data);
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.UPDATE_RULE));
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }
        });
        LocalDataUtil.get().saveData(LocalDataKeys.BKEY_CONFIG_SCHEDULER, true);
    }


    public static void checkAndBuildTab() {
        List<TabInfo> tabInfoList = new ArrayList<>();
        TabInfo todayTabInfo = new TabInfo();
        todayTabInfo.setTabName(StringUtil.getString(R.string.config_init_express_tab_today));
        todayTabInfo.setRelationDateOffset(-1);
        todayTabInfo.setOpenFlag(1);
        tabInfoList.add(todayTabInfo);
        if (expressModeId > 0) {
            TabInfo expressTabInfo = new TabInfo();
            expressTabInfo.setTabName(StringUtil.getString(R.string.config_init_express_tab));
            expressTabInfo.setRelationIds(expressModeId + ",");
            expressTabInfo.setKeywordInfoList(Arrays.asList(
                    new KeywordInfo(StringUtil.getString(R.string.config_init_express_get_code), "#0081FF")
            ));
            expressTabInfo.setOpenFlag(1);
            tabInfoList.add(expressTabInfo);
        }
        if (smsModeId > 0) {
            TabInfo smsTabInfo = new TabInfo();
            smsTabInfo.setTabName(StringUtil.getString(R.string.config_init_express_vefiy_code));
            smsTabInfo.setRelationIds(smsTabInfo + ",");
            List<KeywordInfo> keywordInfoList = new ArrayList<>();
            for (String data : StringUtil.getStringArray(R.array.first_guide_copy_txt)) {
                keywordInfoList.add(new KeywordInfo(data, "#0081FF"));
            }
            smsTabInfo.setShowKeyWord(GsonUtils.toString(keywordInfoList));
            smsTabInfo.setOpenFlag(1);
            tabInfoList.add(smsTabInfo);
        }
        TabDataService.insertList(tabInfoList, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ProcessFactory.get().getMagicProcess().refreshTabData();
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_TAB_CONFIG));
            }
        });
    }

    private static void buildWelcomeScheduleTip() {
        RemindInfo remindInfo = new RemindInfo();
        remindInfo.setTitle(StringUtil.getString(R.string.config_init_schedule_tip));
        remindInfo.setContent(StringUtil.getString(R.string.config_init_schedule_tip_more));
        remindInfo.setRemindType(RemindInfo.TYPE_EXACT);
        RemindTimeInfo remindTimeInfo = new RemindTimeInfo();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 3);
        remindTimeInfo.setStartTime(TimeUtil.formatHHMMTime(calendar.getTimeInMillis()));
        remindTimeInfo.setRepeatType(RemindTimeInfo.TYPE_REPEAT_NONE);
        remindTimeInfo.setHandlerCount(1);
        remindTimeInfo.setRepeatData(GsonUtils.toString(Arrays.asList(TimeUtil.formatYMDDate(calendar.getTimeInMillis()))));
        remindInfo.setRemindTimeInfo(remindTimeInfo);
        remindInfo.setTimeJson(GsonUtils.toString(remindTimeInfo));
        remindInfo.setNotifyNumber(0);
        RemindDataService.insertOrUpdate(remindInfo, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                SchedulerUtil.get().configAlarm(remindInfo);
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_SCHEDULER));
            }
        });
    }
}
