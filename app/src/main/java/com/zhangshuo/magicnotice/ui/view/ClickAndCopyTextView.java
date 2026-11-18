package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;


public class ClickAndCopyTextView extends androidx.appcompat.widget.AppCompatTextView {
    private long mLastActionDownTime = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            long actionUpTime = System.currentTimeMillis();
            if (actionUpTime - mLastActionDownTime <= ViewConfiguration.getLongPressTimeout()) {
                performClick();
                return super.onTouchEvent(event);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            mLastActionDownTime = System.currentTimeMillis();
        }
        return super.onTouchEvent(event);
    }

    public ClickAndCopyTextView(Context context) {
        super(context);
    }

    public ClickAndCopyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickAndCopyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
