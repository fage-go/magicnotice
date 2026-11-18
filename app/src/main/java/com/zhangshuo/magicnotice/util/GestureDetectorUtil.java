package com.zhangshuo.magicnotice.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.core.view.GestureDetectorCompat;

public class GestureDetectorUtil {
    private GestureDetectorCompat mGestureDetector;
    private OnTapCallback mOnTapCallback;

    public GestureDetectorUtil(Context context, OnTapCallback onTapCallback) {
        this.mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener());
        this.mOnTapCallback = onTapCallback;
        this.mGestureDetector.setIsLongpressEnabled(false);
        this.mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mOnTapCallback != null) {
                    mOnTapCallback.onSingleTap();
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mOnTapCallback != null) {
                    mOnTapCallback.onDoubleTap();
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
    }

    public GestureDetectorCompat getGestureDetector() {
        return mGestureDetector;
    }

    public interface OnTapCallback {
        void onSingleTap();

        void onDoubleTap();
    }
}
