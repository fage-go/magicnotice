package com.zhangshuo.magicnotice.ui.model;

import androidx.annotation.IntDef;

import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.BroadcastBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.CopyBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.FlashLightBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.FloatBallModel;
import com.zhangshuo.magicnotice.ui.model.behavior.FullScreenBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;
import com.zhangshuo.magicnotice.ui.model.behavior.NotificationBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.OpenBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.ShockBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.SoundBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.WidgetBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.BarrageCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.BroadcastCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.CopyCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.FlashCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.FloatBallCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.FullScreenCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.LightCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.NotificationCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.OpenCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.ShockCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.SoundCBFragment;
import com.zhangshuo.magicnotice.ui.view.configrule.behavior.WidgetCBFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 触发什么效果
 */
public class BehaviorModel {
    private int type = 0;

    public BehaviorModel(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(@BehaviorType int type) {
        this.type = type;
    }

    public boolean isEmpty() {
        return false;
    }

    public void exec(NotificationData notificationData) {

    }

    @IntDef({
            BehaviorType.BEHAVIOR_SHOCK,
            BehaviorType.BEHAVIOR_SOUND,
            BehaviorType.BEHAVIOR_BROADCAST,
            BehaviorType.BEHAVIOR_BARRAGE,
            BehaviorType.BEHAVIOR_OPEN,
            BehaviorType.BEHAVIOR_COPY,
            BehaviorType.BEHAVIOR_FLOAT_BALL,
            BehaviorType.BEHAVIOR_FLASH_LIGHT,
            BehaviorType.BEHAVIOR_FULL_SCREEN,
            BehaviorType.BEHAVIOR_NOTIFICATION,
            BehaviorType.BEHAVIOR_WIDGET,
            BehaviorType.BEHAVIOR_LIGHT
    })
    public @interface BehaviorType {
        int BEHAVIOR_SHOCK = 2001;//震动提醒
        int BEHAVIOR_SOUND = 2002;//声音提醒
        int BEHAVIOR_BROADCAST = 2003;//播报功能
        int BEHAVIOR_BARRAGE = 2004;//弹幕
        int BEHAVIOR_OPEN = 2005;//自动打开
        int BEHAVIOR_COPY = 2006;//复制验证码、复制链接
        int BEHAVIOR_FLOAT_BALL = 2007;//悬浮球
        int BEHAVIOR_FLASH_LIGHT = 2008;//闪光灯
        int BEHAVIOR_FULL_SCREEN = 2009;//全屏提醒
        int BEHAVIOR_NOTIFICATION = 2010;//通知栏收纳
        int BEHAVIOR_WIDGET = 2011;//小部件收纳
        int BEHAVIOR_LIGHT = 2012;//LED
    }

    public static List<Integer> getBehaviorTypeList() {
        return Arrays.asList(
                BehaviorType.BEHAVIOR_BARRAGE,
                BehaviorType.BEHAVIOR_FLOAT_BALL,
                BehaviorType.BEHAVIOR_BROADCAST,
                BehaviorType.BEHAVIOR_SOUND,
                BehaviorType.BEHAVIOR_SHOCK,
                BehaviorType.BEHAVIOR_FLASH_LIGHT,
//                BehaviorType.BEHAVIOR_FULL_SCREEN,
                BehaviorType.BEHAVIOR_NOTIFICATION,
//                BehaviorType.BEHAVIOR_WIDGET,
                BehaviorType.BEHAVIOR_COPY,
                BehaviorType.BEHAVIOR_OPEN,
                BehaviorType.BEHAVIOR_LIGHT
        );
    }

    public static String getBehaviorName(int type) {
        switch (type) {
            case BehaviorType.BEHAVIOR_SHOCK:
                return StringUtil.getString(R.string.config_rule_b_shock);
            case BehaviorType.BEHAVIOR_SOUND:
                return StringUtil.getString(R.string.config_rule_b_sound);
            case BehaviorType.BEHAVIOR_BROADCAST:
                return StringUtil.getString(R.string.config_rule_b_broad);
            case BehaviorType.BEHAVIOR_BARRAGE:
                return StringUtil.getString(R.string.config_rule_b_barrage);
            case BehaviorType.BEHAVIOR_OPEN:
                return StringUtil.getString(R.string.config_rule_b_open);
            case BehaviorType.BEHAVIOR_COPY:
                return StringUtil.getString(R.string.config_rule_b_copy);
            case BehaviorType.BEHAVIOR_FLOAT_BALL:
                return StringUtil.getString(R.string.config_rule_b_float_ball);
            case BehaviorType.BEHAVIOR_FLASH_LIGHT:
                return StringUtil.getString(R.string.config_rule_b_flash_light);
            case BehaviorType.BEHAVIOR_FULL_SCREEN:
                return StringUtil.getString(R.string.config_rule_b_full_screen);
            case BehaviorType.BEHAVIOR_NOTIFICATION:
                return StringUtil.getString(R.string.config_rule_b_notification);
            case BehaviorType.BEHAVIOR_WIDGET:
                return StringUtil.getString(R.string.config_rule_b_widget);
            case BehaviorType.BEHAVIOR_LIGHT:
                return StringUtil.getString(R.string.config_rule_b_light);
        }
        return StringUtil.getString(R.string.config_rule_b_none);
    }

    public static IConfigRule getBehaviorView(int type) {
        switch (type) {
            case BehaviorType.BEHAVIOR_SHOCK:
                return new ShockCBFragment();
            case BehaviorType.BEHAVIOR_SOUND:
                return new SoundCBFragment();
            case BehaviorType.BEHAVIOR_BROADCAST:
                return new BroadcastCBFragment();
            case BehaviorType.BEHAVIOR_BARRAGE:
                return new BarrageCBFragment();
            case BehaviorType.BEHAVIOR_OPEN:
                return new OpenCBFragment();
            case BehaviorType.BEHAVIOR_COPY:
                return new CopyCBFragment();
            case BehaviorType.BEHAVIOR_FLOAT_BALL:
                return new FloatBallCBFragment();
            case BehaviorType.BEHAVIOR_FLASH_LIGHT:
                return new FlashCBFragment();
            case BehaviorType.BEHAVIOR_FULL_SCREEN:
                return new FullScreenCBFragment();
            case BehaviorType.BEHAVIOR_NOTIFICATION:
                return new NotificationCBFragment();
            case BehaviorType.BEHAVIOR_WIDGET:
                return new WidgetCBFragment();
            case BehaviorType.BEHAVIOR_LIGHT:
                return new LightCBFragment();
        }
        return null;
    }

    public static List<BehaviorModel> convert(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            List<BehaviorModel> behaviorModelList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int type = jsonObject.getInt("type");
                BehaviorModel behaviorModel = null;
                if (type == BehaviorType.BEHAVIOR_SHOCK) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), ShockBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_SOUND) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), SoundBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_BROADCAST) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), BroadcastBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_BARRAGE) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), BarrageBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_OPEN) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), OpenBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_COPY) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), CopyBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_FLOAT_BALL) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), FloatBallModel.class);
                } else if (type == BehaviorType.BEHAVIOR_FLASH_LIGHT) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), FlashLightBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_FULL_SCREEN) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), FullScreenBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_NOTIFICATION) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), NotificationBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_WIDGET) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), WidgetBehaviorModel.class);
                } else if (type == BehaviorType.BEHAVIOR_LIGHT) {
                    behaviorModel = GsonUtils.convert(jsonObject.toString(), LightModel.class);
                }
                if (behaviorModel == null) continue;
                behaviorModelList.add(behaviorModel);
            }
            return behaviorModelList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
