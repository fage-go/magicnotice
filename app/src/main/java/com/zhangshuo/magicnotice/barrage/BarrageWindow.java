package com.zhangshuo.magicnotice.barrage;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.floatwindow.annotation.MoveType;
import com.zhangshuo.common.floatwindow.impl.FloatPhone;
import com.zhangshuo.common.floatwindow.interfaces.BaseFloatWindow;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.BarrageGlobalConfig;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.util.GestureDetectorUtil;

public class BarrageWindow extends BaseFloatWindow {
    private Builder mBuilder;
    private FloatPhone mFloatView;
    private boolean isShowing = false;
    private GestureDetectorUtil gestureDetectorUtil;
    private Runnable animEnterRunnable;
    private Runnable animExitRunnable;
    private int enterStayTime = 100;

    private BarrageWindow(Builder builder) {
        this.mBuilder = builder;
        mFloatView = new FloatPhone(mBuilder.mApplicationContext);
        animEnterRunnable = new Runnable() {
            @Override
            public void run() {
                if (mBuilder != null && mBuilder.mOnOptCallback != null) {
                    mBuilder.mOnOptCallback.onMoveEnterAnimEnd(getView());
                }
            }
        };
        animExitRunnable = new Runnable() {
            @Override
            public void run() {
                if (mBuilder != null && mBuilder.mOnOptCallback != null) {
                    mBuilder.mOnOptCallback.onMoveExitAnimEnd(getView());
                }
            }
        };
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    public void preConfigWindow(BarrageBehaviorModel model) {
        initTouchEvent();
        if (mFloatView == null) return;
        mFloatView.setMoveType(mBuilder.mMoveType);
        mFloatView.setSize(mBuilder.mWidth, mBuilder.mHeight);
        mFloatView.setGravity(mBuilder.gravity, mBuilder.xOffset, mBuilder.yOffset + getViewTop());
        mFloatView.setView(mBuilder.mView);
        int animStyle = R.style.BarrageStyle_Move;
        switch (model.getEnterEffectType()) {
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_MOVE:
                enterStayTime = 1500;
                animStyle = R.style.BarrageStyle_Move;
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_ALPHA:
                enterStayTime = 400;
                animStyle = R.style.BarrageStyle_Alpha;
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_SLIDE:
                enterStayTime = 400;
                animStyle = R.style.BarrageStyle_Slide;
                break;
            case BarrageBehaviorModel.ENTER_EFFECT_TYPE_JUMP:
                enterStayTime = 600;
                animStyle = R.style.BarrageStyle_Jump;
                break;
        }
        enterStayTime = (int) (enterStayTime * 1.2);
        mFloatView.setAnimStyle(animStyle);
    }

    public int getViewTop() {
        BarrageGlobalConfig barrageGlobalConfig = BarrageHelper.get().getBarrageGlobalConfig();
        if (barrageGlobalConfig == null) return 0;
        return barrageGlobalConfig.getTop();
    }

    public String getViewTag() {
        return mBuilder.mTag;
    }

    private void initTouchEvent() {
        if (mBuilder.mMoveType == MoveType.FIXED) {
            if (gestureDetectorUtil != null) return;
            gestureDetectorUtil = new GestureDetectorUtil(AppUtil.get().getApplication(), new GestureDetectorUtil.OnTapCallback() {
                @Override
                public void onSingleTap() {
                    if (mBuilder.mOnOptCallback != null) {
                        mBuilder.mOnOptCallback.onSingleClick(getView());
                    }
                }

                @Override
                public void onDoubleTap() {
                    if (mBuilder.mOnOptCallback != null) {
                        mBuilder.mOnOptCallback.onDoubleClick(getView());
                    }
                }
            });

            getView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (gestureDetectorUtil != null && gestureDetectorUtil.getGestureDetector() != null)
                        gestureDetectorUtil.getGestureDetector().onTouchEvent(event);
                    return false;
                }
            });
        } else {
            gestureDetectorUtil = null;
            getView().setOnTouchListener(null);
        }
    }


    @Override
    public void show() {
        if (isShowing) {
            return;
        }
        mFloatView.init();
        isShowing = true;
        getView().setVisibility(View.VISIBLE);
        BarrageHelper.get().getHandler().removeCallbacks(animEnterRunnable);
        BarrageHelper.get().getHandler().removeCallbacks(animExitRunnable);
        BarrageHelper.get().getHandler().postDelayed(animEnterRunnable, enterStayTime);
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

    @Override
    public View getView() {
        return mBuilder.mView;
    }

    @Override
    public void dismiss() {
        gestureDetectorUtil = null;
        if (!isShowing) return;
        mFloatView.dismiss();
        isShowing = false;
        BarrageHelper.get().getHandler().removeCallbacks(animEnterRunnable);
        BarrageHelper.get().getHandler().removeCallbacks(animExitRunnable);
        BarrageHelper.get().getHandler().postDelayed(animExitRunnable, 1000);
    }

    public static Builder create() {
        return new Builder();
    }


    public static class Builder {
        public Context mApplicationContext;
        public View mView;
        public int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int gravity = Gravity.TOP | Gravity.START;
        public int xOffset;
        public int yOffset;
        public int mMoveType = MoveType.SLIDE;
        public OnOptCallback mOnOptCallback;
        private String mTag = "";

        public Builder() {
            mApplicationContext = AppUtil.get().getApplication();
        }

        public BarrageWindow.Builder setView(View view) {
            mView = view;
            return this;
        }


        public BarrageWindow.Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public BarrageWindow.Builder setHeight(int height) {
            mHeight = height;
            return this;
        }


        public BarrageWindow.Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public BarrageWindow.Builder setY(int y) {
            yOffset = y;
            return this;
        }


        public BarrageWindow.Builder setMoveType(@MoveType.MOVE_TYPE int moveType) {
            mMoveType = moveType;
            return this;
        }


        public BarrageWindow.Builder setTag(String tag) {
            mTag = tag;
            return this;
        }


        public BarrageWindow.Builder setOnOptCallback(OnOptCallback onOptCallback) {
            this.mOnOptCallback = onOptCallback;
            return this;
        }

        public BarrageWindow build() {
            if (mView == null) {
                throw new IllegalArgumentException("View has not been set!");
            }
            return new BarrageWindow(this);
        }
    }

    public interface OnOptCallback {
        void onSingleClick(View view);

        void onDoubleClick(View view);

        void onMoveEnterAnimEnd(View view);

        void onMoveExitAnimEnd(View view);
    }
}
