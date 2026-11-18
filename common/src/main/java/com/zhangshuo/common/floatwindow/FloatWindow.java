package com.zhangshuo.common.floatwindow;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.floatwindow.annotation.MoveType;
import com.zhangshuo.common.floatwindow.impl.IFloatWindowImpl;
import com.zhangshuo.common.floatwindow.interfaces.BaseFloatWindow;
import com.zhangshuo.common.floatwindow.interfaces.ViewStateListener;
import com.zhangshuo.common.util.LogUtils;

import java.util.HashMap;
import java.util.Map;


public class FloatWindow {

    private static final String DEFAULT_TAG = "default_float_window_tag";
    private static Map<String, BaseFloatWindow> mFloatWindowMap = new HashMap<String, BaseFloatWindow>();
    @SuppressWarnings("unused")
    private static Builder mBuilder = null;

    private FloatWindow() {

    }

    public static boolean isEmpty() {
        return mFloatWindowMap == null || mFloatWindowMap.isEmpty();
    }

    public static BaseFloatWindow get() {
        return get(DEFAULT_TAG);
    }

    public static BaseFloatWindow get(String tag) {
        return mFloatWindowMap == null ? null : mFloatWindowMap.get(tag);
    }

    public static Map<String,BaseFloatWindow> getAll(){
        return mFloatWindowMap;
    }

    public static Builder with() {
        return mBuilder = new Builder();
    }

    /**
     * 销毁默认tag的
     */
    public static void destroy() {
        destroy(DEFAULT_TAG);
    }

    /**
     * 销毁指定tag的
     *
     * @param tag
     */
    public static void destroy(String tag) {
        try {
            if (mFloatWindowMap == null || !mFloatWindowMap.containsKey(tag)) {
                return;
            }
            mFloatWindowMap.get(tag).dismiss();
            mFloatWindowMap.remove(tag);
        } catch (Exception e) {

        }
    }

    /**
     * 销毁全部
     */
    public static void destroyAll() {
        if (mFloatWindowMap != null) {
            for (BaseFloatWindow iFloatWindow : mFloatWindowMap.values()) {
                try {
                    iFloatWindow.dismiss();
                    mFloatWindowMap.remove(iFloatWindow);
                } catch (Throwable e) {
                    LogUtils.e(Log.getStackTraceString(e));
                }
            }
            mFloatWindowMap = null;
        }
    }

    /**
     * 支持链式调用的Builder类
     */
    public static class Builder {
        public Context mApplicationContext;
        public View mView;
        public int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int gravity = Gravity.TOP | Gravity.START;
        public int xOffset;
        public int yOffset;
        public boolean mShow = true;
        public Class<?>[] mActivities;
        public int mMoveType = MoveType.SLIDE;
        public int mSlideLeftMargin;
        public int mSlideRightMargin;
        public long mDuration = 300;
        public TimeInterpolator mInterpolator;
        public boolean mDesktopShow;
        public ViewStateListener mViewStateListener;
        public int mLayoutId;
        private String mTag = DEFAULT_TAG;

        public Builder() {
            mApplicationContext = AppUtil.get().getApplication();
        }

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public Builder setView(int layoutId) {
            mLayoutId = layoutId;
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }


        public Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public Builder setY(int y) {
            yOffset = y;
            return this;
        }


        /**
         * 设置 Activity 过滤器，用于指定在哪些界面显示悬浮窗，默认全部界面都显示
         *
         * @param show       过滤类型,子类类型也会生效
         * @param activities 过滤界面
         */
        public Builder setFilter(boolean show, Class<?>... activities) {
            mShow = show;
            mActivities = activities;
            return this;
        }

        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType) {
            return setMoveType(moveType, 0, 0);
        }

        /**
         * 设置带边距的贴边动画，只有 moveType 为 MoveType.SLIDE，设置边距才有意义，这个方法不标准，后面调整
         *
         * @param moveType         贴边动画 MoveType.SLIDE
         * @param slideLeftMargin  贴边动画左边距，默认为 0
         * @param slideRightMargin 贴边动画右边距，默认为 0
         */
        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType, int slideLeftMargin, int slideRightMargin) {
            mMoveType = moveType;
            mSlideLeftMargin = slideLeftMargin;
            mSlideRightMargin = slideRightMargin;
            return this;
        }

        public Builder setMoveStyle(long duration, TimeInterpolator interpolator) {
            mDuration = duration;
            mInterpolator = interpolator;
            return this;
        }

        public Builder setTag(String tag) {
            mTag = tag;
            return this;
        }

        public Builder setDesktopShow(boolean show) {
            mDesktopShow = show;
            return this;
        }

        public Builder setViewStateListener(ViewStateListener listener) {
            mViewStateListener = listener;
            return this;
        }

        public BaseFloatWindow build() {
            if (mFloatWindowMap == null) {
                mFloatWindowMap = new HashMap<String, BaseFloatWindow>();
            }
            if (mFloatWindowMap.containsKey(mTag)) {
                return mFloatWindowMap.get(mTag);
            }
            if (mView == null && mLayoutId == 0) {
                throw new IllegalArgumentException("View has not been set!");
            }
            if (mView == null) {
                LayoutInflater inflate = (LayoutInflater) mApplicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = inflate.inflate(mLayoutId, null);
            }
            BaseFloatWindow floatWindowImpl = new IFloatWindowImpl(this);
            mFloatWindowMap.put(mTag, floatWindowImpl);
            LogUtils.i("build windowView [" + mTag + "] success.");
            return floatWindowImpl;
        }
    }
}
