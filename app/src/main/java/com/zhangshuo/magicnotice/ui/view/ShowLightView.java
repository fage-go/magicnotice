package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zhangshuo.common.util.UIUtil;

public class ShowLightView extends View {
    public static final int DIRECTION_NONE = 0;
    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_TOP = 2;
    public static final int DIRECTION_RIGHT = 3;
    public static final int DIRECTION_BOTTOM = 4;

    private RectF rect;
    private float radius;
    private int direction = DIRECTION_NONE;
    private int strokeWidth = UIUtil.dp2Px(1f);
    private Paint drawPaint;
    private int defaultColor = Color.parseColor("#0081FF");
    private int selectColor = Color.parseColor("#E54D42");
    private float moveX, moveY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect == null) return;


        float leftMove = checkSelect(DIRECTION_LEFT);
        float rightMove = checkSelect(DIRECTION_RIGHT);
        float topMove = checkSelect(DIRECTION_TOP);
        float bottomMove = checkSelect(DIRECTION_BOTTOM);
        canvas.drawRoundRect(rect.left + leftMove, rect.top + topMove, rect.right + rightMove, rect.bottom + bottomMove, radius, radius, drawPaint);

        canvas.drawLine(rect.left + leftMove, 0, rect.left + leftMove, getHeight(), drawPaint);
        canvas.drawLine(rect.right + rightMove, 0, rect.right + rightMove, getHeight(), drawPaint);
        canvas.drawLine(0, rect.top + topMove, getWidth(), rect.top + topMove, drawPaint);
        canvas.drawLine(0, rect.bottom + bottomMove, getWidth(), rect.bottom + bottomMove, drawPaint);

    }

    private float checkSelect(int direction) {
        if (this.direction == direction) {
            drawPaint.setColor(selectColor);
            switch (direction) {
                case DIRECTION_LEFT:
                case DIRECTION_RIGHT:
                    return moveX;
                case DIRECTION_TOP:
                case DIRECTION_BOTTOM:
                    return moveY;
            }
        } else {
            drawPaint.setColor(defaultColor);
        }
        return 0;
    }


    public void setDirection(int newDirection) {
        if (newDirection == DIRECTION_NONE) {
            if (this.direction == DIRECTION_LEFT) {
                float left = rect.left + moveX;
                left = Math.max(0, left);
                left = Math.min(left, rect.right);
                rect.left = left;
            } else if (this.direction == DIRECTION_RIGHT) {
                float right = rect.right + moveX;
                right = Math.max(rect.left, right);
                right = Math.min(right, getWidth());
                rect.right = right;
            } else if (this.direction == DIRECTION_TOP) {
                float top = rect.top + moveY;
                top = Math.max(0, top);
                top = Math.min(top, rect.bottom);
                rect.top = top;
            } else if (this.direction == DIRECTION_BOTTOM) {
                float bottom = rect.bottom + moveY;
                bottom = Math.max(rect.top, bottom);
                bottom = Math.min(bottom, getHeight());
                rect.bottom = bottom;
            }
            moveX = 0;
            moveY = 0;
        }
        this.direction = newDirection;
        postInvalidate();
    }

    public void onMove(float x, float y) {
        if (this.direction == DIRECTION_LEFT) {
            float left = rect.left + x;
            left = Math.max(0, left);
            left = Math.min(left, rect.right);
            moveX = left - rect.left;
        } else if (this.direction == DIRECTION_RIGHT) {
            float right = rect.right + x;
            right = Math.max(rect.left, right);
            right = Math.min(right, getWidth());
            moveX = right - rect.right;
        } else if (this.direction == DIRECTION_TOP) {
            float top = rect.top + y;
            top = Math.max(0, top);
            top = Math.min(top, rect.bottom);
            moveY = top - rect.top;
        } else if (this.direction == DIRECTION_BOTTOM) {
            float bottom = rect.bottom + y;
            bottom = Math.max(rect.top, bottom);
            bottom = Math.min(bottom, getHeight());
            moveY = bottom - rect.bottom;
        }
        postInvalidate();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        postInvalidate();
    }

    public void setRect(RectF rect) {
        this.rect = rect;
        postInvalidate();
    }

    public RectF getRect() {
        return rect;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (rect == null && getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            int size = UIUtil.dp2Px(30);
            rect = new RectF(getMeasuredWidth() / 2f - size, size / 2f, getMeasuredWidth() / 2f + size, size);
        }
    }

    private void init() {
        drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setColor(defaultColor);
        strokeWidth = UIUtil.dp2Px(2);
        drawPaint.setStrokeWidth(strokeWidth);

    }

    public ShowLightView(Context context) {
        super(context);
        init();
    }

    public ShowLightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShowLightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ShowLightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
