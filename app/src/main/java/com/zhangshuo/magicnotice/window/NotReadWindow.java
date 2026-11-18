package com.zhangshuo.magicnotice.window;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.floatwindow.annotation.MoveType;
import com.zhangshuo.common.floatwindow.impl.FloatPhone;
import com.zhangshuo.common.floatwindow.interfaces.BaseFloatWindow;
import com.zhangshuo.common.floatwindow.interfaces.ViewStateListener;
import com.zhangshuo.common.util.LogUtils;

public class NotReadWindow extends BaseFloatWindow {
    private int screenWidth, screenHeight;
    private Builder mBuilder;
    private FloatPhone mFloatView;
    private boolean isShowing = false;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private boolean mClick = false;
    private int mSlop;
    private ValueAnimator mAnimator;
    private boolean canMove = false;


    public NotReadWindow(Builder builder) {
        this.mBuilder = builder;
        screenWidth = ScreenUtils.getScreenWidth();
        screenHeight = ScreenUtils.getScreenHeight();
        mFloatView = new FloatPhone(mBuilder.mApplicationContext);
        mFloatView.setMoveType(MoveType.ACTIVE);
        mFloatView.setSize(-2, -2);
        mFloatView.setGravity(mBuilder.gravity, mBuilder.xOffset, mBuilder.yOffset);
        mFloatView.setView(mBuilder.mView);
        initTouchEvent();
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    private void initTouchEvent() {
        getView().setOnTouchListener(new View.OnTouchListener() {
            float lastX, lastY, changeX, changeY;
            int newX, newY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        canMove = false;
                        mClick = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!canMove) {
                            float slopX = event.getRawX() - downX;
                            float slopY = event.getRawY() - downY;
                            if (Math.abs(slopX) <= mSlop && Math.abs(slopY) <= mSlop) {
                                return mClick;
                            }
                            canMove = true;
                        }
                        changeX = event.getRawX() - lastX;
                        changeY = event.getRawY() - lastY;
                        newX = (int) (mFloatView.getX() + changeX);
                        newY = (int) (mFloatView.getY() + changeY);
                        if (!isOutOfRange(newX, newY)) {
                            mFloatView.updateXY(newX, newY);
                            if (mBuilder.mViewStateListener != null) {
                                mBuilder.mViewStateListener.onPositionUpdate(newX, newY);
                            }
                            lastX = event.getRawX();
                            lastY = event.getRawY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        upX = event.getRawX();
                        upY = event.getRawY();
                        mClick = (Math.abs(upX - downX) > mSlop) || (Math.abs(upY - downY) > mSlop);
                        LogUtils.i(">>>>onTouch mClick" + mClick);
                        break;
                    default:
                        break;
                }
                return mClick;
            }
        });
    }


    private boolean isOutOfRange(float x, float y) {
        int viewWidth = getView().getMeasuredWidth();
        int viewHeight = getView().getMeasuredHeight();
        return x <= 0 || x >= (screenWidth - viewWidth) ||
                y <= 0 || y >= (screenHeight - viewHeight);
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
    public int getX() {
        return mFloatView.getX();
    }

    @Override
    public int getY() {
        return mFloatView.getY();
    }

    @Override
    public void updateX(int x) {
        mBuilder.xOffset = x;
        mFloatView.updateX(x);
    }

    @Override
    public void updateY(int y) {
        mBuilder.yOffset = y;
        mFloatView.updateY(y);
    }

    private void checkGravity() {

    }

    @Override
    public View getView() {
        mSlop = ViewConfiguration.get(mBuilder.mApplicationContext).getScaledTouchSlop();
        return mBuilder.mView;
    }

    @Override
    public void dismiss() {
        if (!isShowing) return;
        mFloatView.dismiss();
        isShowing = false;
    }

    public static class Builder {
        public Context mApplicationContext;
        public View mView;
        public int gravity = Gravity.TOP | Gravity.START;
        public int xOffset;
        public int yOffset;
        public long mDuration;
        public ViewStateListener mViewStateListener;


        public Builder() {
            mApplicationContext = AppUtil.get().getApplication();
        }

        public NotReadWindow.Builder setView(View view) {
            mView = view;
            return this;
        }


        public NotReadWindow.Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public NotReadWindow.Builder setY(int y) {
            yOffset = y;
            return this;
        }


        public NotReadWindow.Builder setViewStateListener(ViewStateListener viewStateListener) {
            this.mViewStateListener = viewStateListener;
            return this;
        }

        public NotReadWindow.Builder setDuration(long duration) {
            mDuration = duration;
            return this;
        }

        public NotReadWindow build() {
            if (mView == null) {
                throw new IllegalArgumentException("View has not been set!");
            }
            return new NotReadWindow(this);
        }
    }

}
