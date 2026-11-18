package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.zhangshuo.common.util.UIUtil;

public class RockerView extends View {
    public static final int DIRECTION_HORIZONTAL = 1;
    public static final int DIRECTION_VERTICAL = 2;
    private Paint paint;
    private Path pathStart;
    private Path pathEnd;
    private int direction = DIRECTION_HORIZONTAL;
    private int padding = UIUtil.dp2Px(10);
    private OnCallback onCallback;
    private float downTouchX, downTouchY;
    private float moveX, moveY;
    private Rect rect;
    private int strokeWidth;
    private int defaultColor = Color.parseColor("#0081FF");
    private int selectColor = Color.parseColor("#E54D42");
    private boolean isSelect;
    private Scroller scroller;

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(isSelect ? selectColor : defaultColor);
        paint.setAlpha(50);
        canvas.drawPaint(paint);

        paint.setAlpha(180);
        if (rect == null) {
            rect = new Rect(0, 0, getWidth(), getHeight());
        }
        paint.setStrokeWidth(strokeWidth * 2);
        canvas.drawRect(rect, paint);

        paint.setAlpha(255);
        paint.setStrokeWidth(strokeWidth);
        if (direction == DIRECTION_HORIZONTAL) {
            canvas.drawLine(getWidth() / 2f + moveX, 0, getWidth() / 2f + moveX, getHeight(), paint);
            canvas.drawLine(getWidth() / 2f + moveX - padding / 2f, getHeight() / 2f, getWidth() / 2f + padding / 2f + moveX, getHeight() / 2f, paint);

            if (pathStart == null) {
                pathStart = new Path();
                pathStart.moveTo(padding, getHeight() / 2f - padding / 2f);
                pathStart.lineTo(padding / 2f, getHeight() / 2f);
                pathStart.lineTo(padding, getHeight() / 2f + padding / 2f);
            }
            canvas.drawPath(pathStart, paint);

            if (pathEnd == null) {
                pathEnd = new Path();
                pathEnd.moveTo(getWidth() - padding, getHeight() / 2f - padding / 2f);
                pathEnd.lineTo(getWidth() - padding / 2f, getHeight() / 2f);
                pathEnd.lineTo(getWidth() - padding, getHeight() / 2f + padding / 2f);

            }
            canvas.drawPath(pathEnd, paint);

        } else if (direction == DIRECTION_VERTICAL) {

            canvas.drawLine(0, getHeight() / 2f + moveY, getWidth(), getHeight() / 2f + moveY, paint);
            canvas.drawLine(getWidth() / 2f, getHeight() / 2f - padding / 2f + moveY, getWidth() / 2f, getHeight() / 2f + padding / 2f + moveY, paint);

            if (pathStart == null) {
                pathStart = new Path();
                pathStart.moveTo(getWidth() / 2f - padding / 2f, padding);
                pathStart.lineTo(getWidth() / 2f, padding / 2f);
                pathStart.lineTo(getWidth() / 2f + padding / 2f, padding);
            }
            canvas.drawPath(pathStart, paint);

            if (pathEnd == null) {
                pathEnd = new Path();
                pathEnd.moveTo(getWidth() / 2f - padding / 2f, getHeight() - padding);
                pathEnd.lineTo(getWidth() / 2f, getHeight() - padding / 2f);
                pathEnd.lineTo(getWidth() / 2f + padding / 2f, getHeight() - padding);

            }
            canvas.drawPath(pathEnd, paint);

        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isSelect = true;
                downTouchX = event.getX();
                downTouchY = event.getY();
                if (onCallback != null) {
                    onCallback.onSelect(true);
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float diffX = event.getX() - downTouchX;
                float diffY = event.getY() - downTouchY;
                if (direction == DIRECTION_HORIZONTAL) {
                    if (diffX < 0) {
                        diffX = Math.max(-getWidth() / 2f + strokeWidth * 2, diffX);
                    } else if (diffX > 0) {
                        diffX = Math.min(getWidth() / 2f - strokeWidth * 2, diffX);
                    }
                    moveX = diffX;
                    moveY = 0;
                } else if (direction == DIRECTION_VERTICAL) {
                    if (diffY < 0) {
                        diffY = Math.max(-getHeight() / 2f + strokeWidth * 2, diffY);
                    } else {
                        diffY = Math.min(getHeight() / 2f - strokeWidth * 2, diffY);
                    }
                    moveX = 0;
                    moveY = diffY;
                }
                postInvalidate();
                if (onCallback != null) {
                    onCallback.onMove(moveX, moveY);
                }
                break;
            case MotionEvent.ACTION_UP:
                isSelect = false;
                downTouchX = 0;
                downTouchY = 0;
                if (onCallback != null) {
                    onCallback.onSelect(false);
                }
                scroller.startScroll((int) moveX, (int) moveY, -(int) moveX, -(int) moveY);
                postInvalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            moveX = scroller.getCurrX();
            moveY = scroller.getCurrY();
            postInvalidate();
        }
    }

    private void init() {
        strokeWidth = UIUtil.dp2Px(1.5f);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(defaultColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);

        scroller = new Scroller(getContext(), new LinearInterpolator());
    }

    public interface OnCallback {
        void onSelect(boolean select);

        void onMove(float x, float y);
    }

    public RockerView(Context context) {
        super(context);
        init();
    }

    public RockerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RockerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RockerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
