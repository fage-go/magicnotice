package com.zhangshuo.magicnotice.config;

import com.zhangshuo.common.router.Scheme;

public class RoutePaths {
    //启动页
    @Scheme({"magic://notice"})
    public static final String APP_ACTIVITY_SPLASH = "/app/activity/splash";
    //主页
    public static final String APP_ACTIVITY_MAIN = "/app/activity/main";
    //主页-首页
    public static final String APP_FRAGMENT_INDEX = "/app/fragment/index";
    //主页-定时
    public static final String APP_FRAGMENT_TIME = "/app/fragment/time";
    //主页-配置页
    public static final String APP_FRAGMENT_CONFIG = "/app/fragment/config";
    //主页-设置
    public static final String APP_FRAGMENT_SET = "/app/fragment/set";
    //主页-首页tab配置
    public static final String APP_ACTIVITY_TAB_CONFIG = "/app/activity/tabConfig";
    //新增配置页
    public static final String APP_ACTIVITY_CONFIG_RULE = "/app/activity/configRule";
    //配置测试
    public static final String APP_ACTIVITY_CONFIG_TEST = "/app/activity/configTest";
    //选择通知
    public static final String APP_ACTIVITY_SELECT_NOTICE = "/app/activity/selectNotice";
    //创建定时器
    public static final String APP_ACTIVITY_MAKE_SCHEDULER = "/app/activity/makeScheduler";
    //定时记录
    public static final String APP_ACTIVITY_SCHEDULER_RECORD = "/app/activity/schedulerRecord";
    //随机文案
    public static final String APP_ACTIVITY_RANDOM_TXT = "/app/activity/randomTxt";
    //随机文案库
    public static final String APP_ACTIVITY_RANDOM_TXT_GROUP = "/app/activity/randomTxtGroup";
    //导入文案
    public static final String APP_ACTIVITY_IMPORT_TXT = "/app/activity/importTxt";
    //设置页面
    public static final String APP_ACTIVITY_SET = "/app/activity/set";
    //关于页面
    public static final String APP_ACTIVITY_ABOUT = "/app/activity/about";
    //H5页面
    public static final String APP_ACTIVITY_H5 = "/app/activity/h5";
    //规则命中列表
    public static final String APP_ACTIVITY_RULE_HIT_LIST = "/app/activity/ruleHitList";
    //规则命中未读列表
    public static final String APP_ACTIVITY_NOT_READ_LIST = "/app/activity/notReadList";
    //搜索
    public static final String APP_ACTIVITY_SEARCH = "/app/activity/search";
    //弹幕全局配置
    public static final String APP_ACTIVITY_CONFIG_BARRAGE = "/app/activity/configBarrage";
    public static final String APP_ACTIVITY_CUTOUT_LIGHT= "/app/activity/cutoutLight";


    //隐私协议
    public static final String APP_PRIVACY_PATH = "file:///android_asset/privacy.html";
    //隐私协议-英文
    public static final String APP_PRIVACY_EN_PATH = "file:///android_asset/privacy_en.html";

    //用户协议
    public static final String APP_USER_PRIVACY_PATH = "file:///android_asset/user_privacy.html";
}
