package com.zhangshuo.magicnotice.led;

import android.view.Gravity;
import android.view.View;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.floatwindow.annotation.MoveType;
import com.zhangshuo.common.floatwindow.impl.FloatPhone;
import com.zhangshuo.common.floatwindow.interfaces.BaseFloatWindow;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;

public class LightViewWindow extends BaseFloatWindow {
    private LightModel lightModel;
    private LightView lightView;
    private boolean isShowing = false;
    private FloatPhone mFloatView;
    private LightView.OnDismissListener onDismissListener;

    public static LightViewWindow makeViewWindow(LightModel lightModel) {
        LightViewWindow lightViewWindow = new LightViewWindow();
        lightViewWindow.lightModel = lightModel;
        lightViewWindow.mFloatView = new FloatPhone(AppUtil.get().getApplication());
        lightViewWindow.mFloatView.setMoveType(MoveType.INACTIVE);
        lightViewWindow.mFloatView.setSize(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
        lightViewWindow.mFloatView.setGravity(Gravity.TOP | Gravity.START, 0, 0);
        lightViewWindow.lightView = new LightView(AppUtil.get().getApplication());
        lightViewWindow.lightView.setAutoAnim(true);
        lightViewWindow.lightView.bindData(lightModel);
        lightViewWindow.lightView.setOnDismissListener(new LightView.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (lightViewWindow.onDismissListener != null) {
                    lightViewWindow.onDismissListener.onDismiss();
                }
            }
        });
        lightViewWindow.mFloatView.setView(lightViewWindow.lightView);
        return lightViewWindow;
    }

    @Override
    public void show() {
        if (isShowing) {
            return;
        }
        mFloatView.init();
        isShowing = true;
    }

    @Override
    public void hide() {
        dismiss();
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public View getView() {
        return lightView;
    }

    @Override
    public void dismiss() {
        if (!isShowing) return;
        mFloatView.dismiss();
        isShowing = false;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void updateX(int x) {

    }

    @Override
    public void updateY(int y) {

    }

    public void setOnDismissListener(LightView.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
