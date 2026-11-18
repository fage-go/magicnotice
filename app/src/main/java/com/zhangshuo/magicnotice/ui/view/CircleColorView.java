package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zhangshuo.common.util.UIUtil;

public class CircleColorView extends View {
    private Paint paint;
    private String colorHex;
    private int[] colorArray;
    private LinearGradient linearGradient;

    public void setColor(String colorHex) {
        try {
            this.colorHex = colorHex;
            this.linearGradient = null;
            this.colorArray = null;
            if (!TextUtils.isEmpty(colorHex) && !"null".equalsIgnoreCase(colorHex)) {
                String[] colorHexArray = colorHex.split(",");
                colorArray = new int[colorHexArray.length];
                for (int i = 0; i < colorHexArray.length; i++) {
                    colorArray[i] = Color.parseColor(colorHexArray[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        postInvalidate();
    }


    public String getColorHex() {
        return colorHex;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        int radius = getWidth() / 2 - UIUtil.dp2Px(1);
        if (colorArray != null && colorArray.length != 0) {
            if (colorArray.length == 1) {
                paint.setColor(colorArray[0]);
                paint.setShader(null);
            } else {
                if (linearGradient == null) {
                    linearGradient = new LinearGradient(0, 0, getWidth(), getHeight(), colorArray, null, LinearGradient.TileMode.CLAMP);
                }
                paint.setColor(Color.WHITE);
                paint.setShader(linearGradient);
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(center, center, radius, paint);
            if ("#FFFFFF".equals(colorHex)) {
                paint.setColor(Color.parseColor("#CBCDD3"));
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(center, center, radius, paint);
            }
        } else {
            //如果值为空
            paint.setColor(Color.parseColor("#CBCDD3"));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(center, center, radius, paint);
            canvas.drawLine((float) (center + radius * Math.cos(getPI(45))),
                    (float) (center - radius * Math.sin(getPI(45))),
                    (float) (center - radius * Math.sin(getPI(45))),
                    (float) (center + radius * Math.cos(getPI(45))),
                    paint);
        }
    }

    private double getPI(int angle) {
        return angle * 2 * Math.PI / 360;
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(UIUtil.dp2Px(1));
    }

    public CircleColorView(Context context) {
        super(context);
        init();
    }

    public CircleColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircleColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
