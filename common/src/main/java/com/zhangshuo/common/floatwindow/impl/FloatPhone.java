package com.zhangshuo.common.floatwindow.impl;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.zhangshuo.common.floatwindow.annotation.MoveType;
import com.zhangshuo.common.floatwindow.interfaces.BaseFloat;


public class FloatPhone extends BaseFloat {
    private final Context mContext;

    private final WindowManager mWindowManager;
    private final WindowManager.LayoutParams mLayoutParams;
    private View mView;
    private int mX, mY;
    private boolean isAddView = false;

    public FloatPhone(Context applicationContext) {
        mContext = applicationContext;
        mWindowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.windowAnimations = 0;
    }

    public void setMoveType(int moveType) {
        if (moveType == MoveType.INACTIVE) {
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        } else {
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
    }

    public void setAnimStyle(int animStyle) {
        mLayoutParams.windowAnimations = animStyle;
    }

    @Override
    public void setSize(int width, int height) {
        mLayoutParams.width = width;
        mLayoutParams.height = height;
    }

    @Override
    public void setView(View view) {
        mView = view;
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mLayoutParams.gravity = gravity;
        mLayoutParams.x = mX = xOffset;
        mLayoutParams.y = mY = yOffset;
    }

    @Override
    public void init() {
        if (Build.VERSION.SDK_INT >= 26) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= 25) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        isAddView = false;
        try {
            mWindowManager.addView(mView, mLayoutParams);
            isAddView = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        isAddView = false;
        try {
            mWindowManager.removeView(mView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateXY(int x, int y) {
        if (!isAddView) {
            return;
        }
        try {
            mLayoutParams.x = mX = x;
            mLayoutParams.y = mY = y;
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateX(int x) {
        if (!isAddView) {
            return;
        }
        try {
            mLayoutParams.x = mX = x;
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateY(int y) {
        if (!isAddView) {
            return;
        }
        try {
            mLayoutParams.y = mY = y;
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getX() {
        return mX;
    }

    @Override
    public int getY() {
        return mY;
    }

}
