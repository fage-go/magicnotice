package com.zhangshuo.magicnotice.ui.view.web;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.Scroller;

import java.util.concurrent.atomic.AtomicInteger;

public class SmoothProgressBar extends ProgressBar {

    private final String TAG = SmoothProgressBar.class.getSimpleName();
    private final AtomicInteger mAtomicInteger = new AtomicInteger();
    private Scroller mScroller;

    public SmoothProgressBar(Context context) {
        super(context);
        init(context);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, new LinearInterpolator());
    }

    public void smoothScrollTo(int progress) {

        int current = mAtomicInteger.get();
        if (current == progress)
            return;

        int previous = mAtomicInteger.getAndSet(progress);
        int target = mAtomicInteger.get();

        if (previous == 0 && target > 0) {
            setVisibility(View.VISIBLE);
            mScroller.startScroll(0, 0, target, 0, dynamicGetAnimationDuration(target));
            invalidate();
        } else if (mScroller.isFinished() && target > previous) {
            int delta = target - previous;
            setVisibility(VISIBLE);
            mScroller.startScroll(previous, 0, delta, 0, dynamicGetAnimationDuration(delta));
            invalidate();
        }
    }

    /**
     * 根据要移动的间距动态的确定时间
     *
     * @param delta
     * @return
     */
    private int dynamicGetAnimationDuration(int delta) {

        if (delta <= 5)
            return 80;
        if (delta <= 20)
            return 150;
        else
            return 200;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int progress = Math.min(mScroller.getCurrX(), mScroller.getFinalX());
            setProgress(progress);
            if (progress == mScroller.getFinalX()) {
                mScroller.abortAnimation();
            }
            postInvalidate();
        } else {
            int target = mAtomicInteger.get();
            if (target > getProgress()) {
                int delta = target - getProgress();
                mScroller.startScroll(getProgress(), 0, delta, 0, dynamicGetAnimationDuration(delta));
                postInvalidate();
            } else if (target >= getMax()) {
                mScroller = new Scroller(getContext(), new LinearInterpolator());
                mAtomicInteger.set(0);
                setVisibility(GONE);
            } else if (target < getProgress()) {
                //target移动的位置，小于当前progress值，说明已经开始加载另外一个网页了
                setVisibility(VISIBLE);
                mScroller = new Scroller(getContext(), new LinearInterpolator());
                mScroller.startScroll(0, 0, target, 0, dynamicGetAnimationDuration(target));
            }
        }
    }
}
