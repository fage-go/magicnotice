package com.zhangshuo.magicnotice.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.R;


public class StepSeekBar extends View {
    private int maxProgress = 1;
    private int progress = 0;
    private int bgColor = Color.WHITE;
    private int pointColor = Color.BLACK;
    private int progressColor = Color.BLACK;
    private int disableColor = Color.BLACK;
    private int thumbColor = Color.WHITE;
    private float radius;
    private Paint mainPaint;

    private int stepWidth = 0;
    private int stepPointSize = 10;

    private float lastX;
    private float moveX;
    private float scrollX;
    private ObjectAnimator objectAnimator;
    private OnProgressCallback onProgressCallback;
    private boolean enable = true;

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.StepSeekbar);
            maxProgress = ta.getInt(R.styleable.StepSeekbar_maxProgress, 1);
            progress = ta.getInt(R.styleable.StepSeekbar_progress, 0);
            radius = ta.getDimension(R.styleable.StepSeekbar_radius, 0);
            bgColor = ta.getColor(R.styleable.StepSeekbar_bgColor, Color.parseColor("#EAEBED"));
            pointColor = ta.getColor(R.styleable.StepSeekbar_pointColor, Color.parseColor("#EAEBED"));
            progressColor = ta.getColor(R.styleable.StepSeekbar_progressColor, Color.parseColor("#0081FF"));
            disableColor = ta.getColor(R.styleable.StepSeekbar_disableColor, Color.parseColor("#919499"));
            enable = ta.getBoolean(R.styleable.StepSeekbar_enable, true);
            thumbColor = ta.getColor(R.styleable.StepSeekbar_thumbColor, Color.WHITE);
            ta.recycle();
        }
        mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainPaint.setStyle(Paint.Style.FILL);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        stepWidth = width / (maxProgress - 1);
        stepPointSize = height / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mainPaint.setColor(bgColor);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius, mainPaint);
        drawStepPoint(canvas);
        drawProgress(canvas);
    }

    private void drawStepPoint(Canvas canvas) {
        mainPaint.setColor(pointColor);
        for (int i = 0; i < maxProgress - 1; i++) {
            canvas.drawCircle(i * stepWidth + stepPointSize / 2f, getHeight() / 2f, stepPointSize / 2f, mainPaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        int diffX = (int) (moveX + scrollX);
        mainPaint.setColor(enable ? progressColor : disableColor);

        int right = progress * stepWidth + getHeight() + diffX;

        //边界判断
        if (right < getHeight()) {
            right = getHeight();
        } else if (right > getWidth()) {
            right = getWidth();
        }

        canvas.drawRoundRect(0, 0, right, getHeight(), radius, radius, mainPaint);

        mainPaint.setColor(thumbColor);
        int circleRadius = getHeight() * 3 / 8;
        int circleX = progress * stepWidth + getHeight() / 2 + diffX;

        if (circleX < getHeight() / 2) {
            circleX = getHeight() / 2;
        } else if (circleX > (getWidth() - getHeight() / 2)) {
            circleX = getWidth() - getHeight() / 2;
        }
        canvas.drawCircle(circleX, getHeight() / 2f, circleRadius, mainPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enable) {
            getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    moveX = 0;
                    scrollX = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = event.getX() - lastX;
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    afterAuctionUp(event.getX());
                    break;
            }
            return true;
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return false;
    }

    private void afterAuctionUp(float upX) {
        int startX = (int) (progress * stepWidth + getHeight() + moveX);
        int targetX = Math.round(upX / stepWidth) * stepWidth + getHeight();
        objectAnimator = ObjectAnimator.ofInt(this, "scrollX", 0, targetX - startX);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerImpl() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                progress = progress + Math.round((moveX + scrollX) / stepWidth);
                moveX = 0;
                scrollX = 0;
                invalidate();
                if (progress < 0) {
                    progress = 0;
                } else if (progress >= maxProgress) {
                    progress = maxProgress - 1;
                }
                if (onProgressCallback != null) {
                    onProgressCallback.onProgress(maxProgress - 1, progress);
                }
            }
        });
        objectAnimator.start();
    }

    public void setScrollX(int scrollX) {
        this.scrollX = scrollX;
        invalidate();
    }


    public void setMaxProgress(int maxProgress) {
        this.maxProgress = Math.max(maxProgress, 1);
    }

    public void setProgress(int progress) {
        this.progress = Math.max(progress, 0);
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setStepPointSize(int stepPointSize) {
        this.stepPointSize = stepPointSize;
    }

    public void setOnProgressCallback(OnProgressCallback onProgressCallback) {
        this.onProgressCallback = onProgressCallback;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public interface OnProgressCallback {
        void onProgress(int maxProgress, int progress);
    }

    public StepSeekBar(Context context) {
        super(context);
    }

    public StepSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public StepSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public StepSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

}
