package com.zhangshuo.magicnotice.ball;

import android.text.TextUtils;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.floatwindow.impl.ViewStateListenerImpl;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.db.model.GlobalConfigData;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.GlobalConfigDataService;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.ui.model.behavior.FloatBallModel;

import java.util.HashMap;
import java.util.Map;

public class FloatBallHelper {
    private Map<String, FloatBallWindow> floatBallMap = new HashMap<>();

    public void addFloatBall(NotificationData notificationData, FloatBallModel floatBallModel) {
        String ruleId = "RuleView_" + notificationData.getRuleId();
        FloatBallWindow floatBallWindow = floatBallMap.get(ruleId);
        if (floatBallWindow == null) {
            floatBallWindow = makeNewWindow(ruleId, floatBallModel);
            floatBallMap.put(ruleId, floatBallWindow);
        }
        FloatBallView floatBallView = (FloatBallView) floatBallWindow.getView();
        floatBallWindow.setUnReadNum(floatBallWindow.getUnReadNum() + 1);
        floatBallView.setDataInfo(floatBallWindow.getUnReadNum(),
                notificationData.getPackageName(), notificationData.getIconPath());
        GlobalConfigDataService.getGlobalConfig(ruleId, new AsyncCallback<GlobalConfigData>() {
            @Override
            public void onSuccess(GlobalConfigData result) {
                String code = result.getCode();
                String value = result.getValue();
                String[] tmpData = value.split(",");
                if (tmpData.length != 2) return;
                FloatBallWindow floatBallWindow = floatBallMap.get(code);
                if (floatBallWindow != null) {
                    floatBallWindow.updateX(StringUtil.stringToInt(tmpData[0]));
                    floatBallWindow.updateY(StringUtil.stringToInt(tmpData[1]));
                }
            }
        });
        floatBallWindow.show();
    }

    public void addFloatBall(NotificationData notificationData, BarrageBehaviorModel behaviorModel) {
        FloatBallModel floatBallModel = FloatBallModel.getDefault();
        floatBallModel.setBgColorHex(behaviorModel.getBgColorHex());
        floatBallModel.setBgAlpha(behaviorModel.getBgAlpha());
        floatBallModel.setTxtColorHex(behaviorModel.getTxtColorHex());
        floatBallModel.setFontAlpha(behaviorModel.getFontAlpha());
        addFloatBall(notificationData, floatBallModel);
    }

    public void addFloatBall(NotificationData notificationData) {
        addFloatBall(notificationData, FloatBallModel.getDefault());
    }

    public FloatBallWindow getFloatWindow(String ruleId) {
        return floatBallMap.get(ruleId);
    }

    public void dismissFloatBall(String ruleId) {
        if (TextUtils.isEmpty(ruleId)) return;
        FloatBallWindow floatBallWindow = floatBallMap.get(ruleId);
        if (floatBallWindow != null) {
            floatBallWindow.dismiss();
            floatBallWindow.setUnReadNum(0);
            floatBallMap.remove(ruleId);
        }
    }

    public void hideFloatBall(String ruleId) {
        if (TextUtils.isEmpty(ruleId)) return;
        FloatBallWindow floatBallWindow = floatBallMap.get(ruleId);
        if (floatBallWindow != null) {
            floatBallWindow.dismiss();
        }
    }

    public void showFloatBall(String ruleId, int reduceNum) {
        if (TextUtils.isEmpty(ruleId)) return;
        FloatBallWindow floatBallWindow = floatBallMap.get(ruleId);
        if (floatBallWindow != null) {
            int unReadNum = floatBallWindow.getUnReadNum() + reduceNum;
            if (unReadNum <= 0) {
                floatBallWindow.setUnReadNum(0);
                floatBallMap.remove(ruleId);
            } else {
                floatBallWindow.setUnReadNum(unReadNum);
                floatBallWindow.show();
            }
        }
    }


    private FloatBallWindow makeNewWindow(String ruleId, FloatBallModel model) {
        FloatBallView floatBallView = new FloatBallView(AppUtil.get().getApplication());
        floatBallView.setRuleId(ruleId);
        floatBallView.setFloatBallModel(model);
        return new FloatBallWindow.Builder()
                .setView(floatBallView)
                .setX(ScreenUtils.getScreenWidth() / 2)
                .setY(ScreenUtils.getScreenHeight() / 2)
                .setViewStateListener(new ViewStateListenerImpl() {
                    @Override
                    public void onPositionUpdate(int x, int y) {
                        super.onPositionUpdate(x, y);
                        GlobalConfigDataService.saveOrUpdate(ruleId, x + "," + y, null);
                    }
                })
                .build();
    }


    public static FloatBallHelper get() {
        return Holder.instance;
    }

    private FloatBallHelper() {

    }

    private static final class Holder {
        private static final FloatBallHelper instance = new FloatBallHelper();
    }
}
