package com.zhangshuo.magicnotice.ui.model.behavior;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.barrage.BarrageHelper;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;

public class BarrageBehaviorModel extends BehaviorModel {
    //透出内容形势
    public static final int CONTENT_TYPE_LOGO = 0x0001;
    public static final int CONTENT_TYPE_TITLE = 0x0002;
    public static final int CONTENT_TYPE_TXT = 0x0004;

    //展示效果
    public static final int ENTER_EFFECT_TYPE_MOVE = 1;//移动
    public static final int ENTER_EFFECT_TYPE_SLIDE = 2;//展开
    public static final int ENTER_EFFECT_TYPE_ALPHA = 3;//闪现
    public static final int ENTER_EFFECT_TYPE_JUMP = 4;//跳动

    public static final int EXIT_EFFECT_TYPE_MOVE = 1;//移出
    public static final int EXIT_EFFECT_TYPE_SLIDE = 2;//收起
    public static final int EXIT_EFFECT_TYPE_ALPHA = 3;//淡出
    public static final int EXIT_EFFECT_TYPE_SCALE = 4;//缩放

    public static final int TYPE_CLICK_CANCEL = 0;//隐藏
    public static final int TYPE_CLICK_MSG = 1;//打开消息
    public static final int TYPE_CLICK_REPLAY_FREE = 2;//小窗回复消息
    public static final int TYPE_CLICK_COLLECT = 3;//收藏消息
    public static final int TYPE_CLICK_MSG_BALL = 4;//消息球

    private int contentType = CONTENT_TYPE_LOGO | CONTENT_TYPE_TXT;//为默认
    //字体大小
    private int txtSizeSP;
    //字体颜色值
    private String txtColorHex;
    //字体透明度
    private float fontAlpha;
    //背景圆角
    private int bgRadius;
    //背景颜色值
    private String bgColorHex;
    //背景透明度
    private float bgAlpha;
    //进入效果
    private int enterEffectType = ENTER_EFFECT_TYPE_MOVE;
    //停留时长
    private int stayEffectTime = 1500;
    //退出效果
    private int exitEffectType = EXIT_EFFECT_TYPE_ALPHA;
    //点击效果
    private int clickType = TYPE_CLICK_CANCEL;
    //双击效果
    private int doubleClickType = TYPE_CLICK_MSG;

    public BarrageBehaviorModel() {
        super(BehaviorType.BEHAVIOR_BARRAGE);
    }

    public static BarrageBehaviorModel getDefault() {
        BarrageBehaviorModel barrageBehaviorModel = new BarrageBehaviorModel();
        barrageBehaviorModel.setContentType(CONTENT_TYPE_LOGO | CONTENT_TYPE_TXT);
        barrageBehaviorModel.setTxtSizeSP(16);
        barrageBehaviorModel.setFontAlpha(1);
        barrageBehaviorModel.setBgAlpha(1);
        barrageBehaviorModel.setTxtColorHex("#2E333B");
        barrageBehaviorModel.setBgColorHex("#FFFFFF");
        barrageBehaviorModel.setBgRadius(8);
        barrageBehaviorModel.setEnterEffectType(ENTER_EFFECT_TYPE_MOVE);
        barrageBehaviorModel.setStayEffectTime(1500);
        barrageBehaviorModel.setExitEffectType(EXIT_EFFECT_TYPE_ALPHA);
        barrageBehaviorModel.setClickType(TYPE_CLICK_CANCEL);
        barrageBehaviorModel.setDoubleClickType(TYPE_CLICK_MSG);
        return barrageBehaviorModel;
    }

    public int getTxtSizeSP() {
        if (txtSizeSP == 12) {
            txtSizeSP = 10;
        } else if (txtSizeSP == 20) {
            txtSizeSP = 22;
        }
        return txtSizeSP;
    }

    public void setTxtSizeSP(int txtSizeSP) {
        this.txtSizeSP = txtSizeSP;
    }

    public String getTxtColorHex() {
        return txtColorHex;
    }

    public void setTxtColorHex(String txtColorHex) {
        this.txtColorHex = txtColorHex;
    }

    public int getBgRadius() {
        return bgRadius;
    }

    public void setBgRadius(int bgRadius) {
        this.bgRadius = bgRadius;
    }

    public String getBgColorHex() {
        return bgColorHex;
    }

    public void setBgColorHex(String bgColorHex) {
        this.bgColorHex = bgColorHex;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public float getFontAlpha() {
        return fontAlpha;
    }

    public void setFontAlpha(float fontAlpha) {
        this.fontAlpha = fontAlpha;
    }

    public float getBgAlpha() {
        return bgAlpha;
    }

    public void setBgAlpha(float bgAlpha) {
        this.bgAlpha = bgAlpha;
    }


    public int getEnterEffectType() {
        return enterEffectType;
    }

    public void setEnterEffectType(int enterEffectType) {
        this.enterEffectType = enterEffectType;
    }


    public int getStayEffectTime() {
        return stayEffectTime;
    }

    public void setStayEffectTime(int stayEffectTime) {
        this.stayEffectTime = stayEffectTime;
    }

    public int getExitEffectType() {
        return exitEffectType;
    }

    public void setExitEffectType(int exitEffectType) {
        this.exitEffectType = exitEffectType;
    }


    public int getClickType() {
        return clickType;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }

    public int getDoubleClickType() {
        return doubleClickType;
    }

    public void setDoubleClickType(int doubleClickType) {
        this.doubleClickType = doubleClickType;
    }


    public static String getClickTip(int clickType) {
        String textContent = "";
        if (clickType == BarrageBehaviorModel.TYPE_CLICK_CANCEL) {
            textContent += StringUtil.getString(R.string.barrage_behavior_hide);
        } else if (clickType == BarrageBehaviorModel.TYPE_CLICK_MSG) {
            textContent += StringUtil.getString(R.string.barrage_behavior_open);
        } else if (clickType == BarrageBehaviorModel.TYPE_CLICK_REPLAY_FREE) {
            textContent += StringUtil.getString(R.string.barrage_behavior_float);
        } else if (clickType == BarrageBehaviorModel.TYPE_CLICK_COLLECT) {
            textContent += StringUtil.getString(R.string.barrage_behavior_collect);
        } else if (clickType == BarrageBehaviorModel.TYPE_CLICK_MSG_BALL) {
            textContent += StringUtil.getString(R.string.barrage_behavior_msg_ball);
        }
        return textContent;
    }

    @Override
    public boolean isEmpty() {
        boolean haveTitle = (contentType & CONTENT_TYPE_TITLE) == CONTENT_TYPE_TITLE;
        boolean haveTxt = (contentType & CONTENT_TYPE_TXT) == CONTENT_TYPE_TXT;
        return !(haveTitle || haveTxt);
    }

    @Override
    public void exec(NotificationData notificationData) {
        BarrageHelper.get().addBarrageInfo(notificationData, this);
        LogUtils.i("BarrageBehavior exec");
    }
}
