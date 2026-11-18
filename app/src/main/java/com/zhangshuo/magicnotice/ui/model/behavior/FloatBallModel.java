package com.zhangshuo.magicnotice.ui.model.behavior;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.ball.FloatBallHelper;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;

public class FloatBallModel extends BehaviorModel {
    public static final int TYPE_BG_NORMAL = 1;//纯色
    public static final int TYPE_BG_EDGE = 2;//有边缘
    public static final int TYPE_BG_LOGO = 3;//显示logo

    public static final int TYPE_SHOW_NORMAL = 1;//不显示详情
    public static final int TYPE_SHOW_DETAIL = 2;//显示详情

    public static final int TYPE_CLICK_NONE = 0;//无操作
    public static final int TYPE_CLICK_MSG = 1;//打开消息
    public static final int TYPE_CLICK_DETAIL_SMALL = 3;//打开详情(小窗)
    public static final int TYPE_CLICK_DETAIL = 4;//打开详情

    //字体大小
    private int txtSizeSP;
    //字体颜色值
    private String txtColorHex;
    //字体透明度
    private float fontAlpha;
    //背景效果
    private int bgType;
    //背景颜色值
    private String bgColorHex;
    //背景透明度
    private float bgAlpha;
    //消息展示类型
    private int showType;
    //显示时间
    private int showTime;
    //点击效果
    private int clickType = TYPE_CLICK_DETAIL_SMALL;


    public FloatBallModel() {
        super(BehaviorType.BEHAVIOR_FLOAT_BALL);
    }

    public static FloatBallModel getDefault() {
        FloatBallModel floatBallModel = new FloatBallModel();
        floatBallModel.setTxtSizeSP(16);
        floatBallModel.setTxtColorHex("#2E333B");
        floatBallModel.setFontAlpha(1);
        floatBallModel.setBgColorHex("#FFFFFF");
        floatBallModel.setBgAlpha(1);
        floatBallModel.setBgType(TYPE_BG_EDGE);
        floatBallModel.setShowType(TYPE_SHOW_NORMAL);
        floatBallModel.setShowTime(0);
        floatBallModel.setClickType(TYPE_CLICK_DETAIL);
        return floatBallModel;
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

    public float getFontAlpha() {
        return fontAlpha;
    }

    public void setFontAlpha(float fontAlpha) {
        this.fontAlpha = fontAlpha;
    }

    public String getBgColorHex() {
        return bgColorHex;
    }

    public void setBgColorHex(String bgColorHex) {
        this.bgColorHex = bgColorHex;
    }

    public float getBgAlpha() {
        return bgAlpha;
    }

    public void setBgAlpha(float bgAlpha) {
        this.bgAlpha = bgAlpha;
    }

    public int getBgType() {
        return bgType;
    }

    public void setBgType(int bgType) {
        this.bgType = bgType;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public int getClickType() {
        return clickType;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    @Override
    public void exec(NotificationData notificationData) {
        super.exec(notificationData);
        FloatBallHelper.get().addFloatBall(notificationData, this);
        LogUtils.i("FloatBallModel exec ");
    }
}
