package com.zhangshuo.magicnotice.ui.model.behavior;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.led.LightUtil;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.util.ColorUtil;

public class LightModel extends BehaviorModel {
    public static final int TYPE_BORDER = 0;
    public static final int TYPE_CUTOUT = 1;
    public static final int TYPE_SLIDE = 2;

    public static final int ANIM_FILL = 0;//填充：即线条移动
    public static final int ANIM_FADE = 1;//闪现：渐隐
    public static final int ANIM_MOVE = 2;//移动：短条移动
    public static final int ANIM_BREATH = 3;//移动：短条移动
    private int viewType = TYPE_BORDER;
    private int animType = ANIM_FILL;
    private int strokeWidth;
    //配置颜色
    private String colorArray;
    //配置速度
    private int speed;
    //配置阴影
    private String colorShadow;
    private int repeatCount;

    public LightModel() {
        super(BehaviorType.BEHAVIOR_LIGHT);
    }

    public static LightModel getDefault() {
        LightModel lightModel = new LightModel();
        lightModel.viewType = TYPE_BORDER;
        lightModel.animType = ANIM_FILL;
        lightModel.speed = 0;
        lightModel.strokeWidth = UIUtil.dp2Px(2);
        lightModel.colorArray = ColorUtil.getColorArray(ColorUtil.getRainColorArray());
        lightModel.repeatCount = 1;
        return lightModel;
    }


    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getColorArray() {
        return colorArray;
    }

    public void setColorArray(String colorArray) {
        this.colorArray = colorArray;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getColorShadow() {
        return colorShadow;
    }

    public void setColorShadow(String colorShadow) {
        this.colorShadow = colorShadow;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getViewType() {
        return viewType;
    }

    public String getViewTypeName() {
        switch (viewType) {
            case TYPE_BORDER:
                return StringUtil.getString(R.string.light_behavior_view_border);
            case TYPE_CUTOUT:
                return StringUtil.getString(R.string.light_behavior_view_cutout);
            case TYPE_SLIDE:
                return StringUtil.getString(R.string.light_behavior_view_slide);
        }
        return StringUtil.getString(R.string.light_behavior_view_border);
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getAnimType() {
        return animType;
    }

    public String getAnimTypeName() {
        switch (animType) {
            case ANIM_FILL:
                return StringUtil.getString(R.string.light_behavior_anim_fill);
            case ANIM_FADE:
                return StringUtil.getString(R.string.light_behavior_anim_fade);
            case ANIM_MOVE:
                return StringUtil.getString(R.string.light_behavior_anim_move);
            case ANIM_BREATH:
                return StringUtil.getString(R.string.light_behavior_anim_breath);
        }
        return StringUtil.getString(R.string.light_behavior_anim_fill);
    }

    public void setAnimType(int animType) {
        this.animType = animType;
    }

    @Override
    public void exec(NotificationData notificationData) {
        super.exec(notificationData);
        LightUtil.getInstance().addLight(notificationData, this);
        LogUtils.i("LightModel exec ");
    }
}
