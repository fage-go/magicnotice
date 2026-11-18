package com.zhangshuo.magicnotice.ball;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.floatwindow.annotation.MoveType;
import com.zhangshuo.common.floatwindow.impl.FloatPhone;
import com.zhangshuo.common.floatwindow.interfaces.BaseFloatWindow;
import com.zhangshuo.common.floatwindow.interfaces.ViewStateListener;
import com.zhangshuo.common.util.LogUtils;

public class FloatBallWindow extends BaseFloatWindow {
    private int screenWidth, screenHeight;
    private Builder mBuilder;
    private FloatPhone mFloatView;
    private boolean isShowing = false;
    private int unReadNum = 0;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private boolean mClick = false;
    private int mSlop;
    private ValueAnimator mAnimator;
    private boolean canMove = false;


    public FloatBallWindow(Builder builder) {
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
                        if (needSlide(upX)) {
                            animSlide(v);
                        }
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

    private boolean needSlide(float x) {
        int viewWidth = getView().getMeasuredWidth();
        int viewHeight = getView().getMeasuredHeight();
        return x <= viewWidth / 2f || x >= (screenWidth - viewWidth * 2 / 3f);
    }

    private void animSlide(View view) {
        int viewWidth = getView().getMeasuredWidth();
        int startX = mFloatView.getX();
        int endX = (startX * 2 + viewWidth / 2 > screenWidth)
                ? screenWidth - view.getWidth() * 2 / 3
                : -viewWidth / 3;
        mAnimator = ObjectAnimator.ofInt(startX, endX);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (Integer) animation.getAnimatedValue();
                mFloatView.updateX(x);
                if (mBuilder.mViewStateListener != null) {
                    mBuilder.mViewStateListener.onPositionUpdate(x, (int) getY());
                }
            }
        });
        startAnimator();
    }

    private void startAnimator() {
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (mBuilder.mViewStateListener != null) {
                    mBuilder.mViewStateListener.onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mBuilder.mDuration).start();
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onMoveAnimStart();
        }
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

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
        if (mBuilder != null && mBuilder.mView instanceof FloatBallView) {
            ((FloatBallView) mBuilder.mView).showReadNum(unReadNum);
        }
    }

    public int getUnReadNum() {
        return unReadNum;
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

        public FloatBallWindow.Builder setView(View view) {
            mView = view;
            return this;
        }


        public FloatBallWindow.Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public FloatBallWindow.Builder setY(int y) {
            yOffset = y;
            return this;
        }


        public FloatBallWindow.Builder setViewStateListener(ViewStateListener viewStateListener) {
            this.mViewStateListener = viewStateListener;
            return this;
        }

        public FloatBallWindow.Builder setDuration(long duration) {
            mDuration = duration;
            return this;
        }

        public FloatBallWindow build() {
            if (mView == null) {
                throw new IllegalArgumentException("View has not been set!");
            }
            return new FloatBallWindow(this);
        }
    }

}
