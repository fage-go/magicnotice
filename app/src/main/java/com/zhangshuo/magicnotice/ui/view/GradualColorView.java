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

public class GradualColorView extends View {

    private Paint paint;
    private String colorHex;
    private int[] colorArray;
    private int radius = UIUtil.dp2Px(4);
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
        if (colorArray != null && colorArray.length > 1) {
            if (linearGradient == null) {
                linearGradient = new LinearGradient(0, 0, getWidth(), getHeight(), colorArray, null, LinearGradient.TileMode.CLAMP);
            }
            paint.setColor(Color.WHITE);
            paint.setShader(linearGradient);
            paint.setStyle(Paint.Style.FILL);
        } else if (colorArray != null && colorArray.length == 1 && colorArray[0] != 0xFFFFFF) {
            paint.setColor(colorArray[0]);
            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setColor(Color.parseColor("#CBCDD3"));
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
        }

        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius, paint);

    }


    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(UIUtil.dp2Px(1));
    }

    public GradualColorView(Context context) {
        super(context);
        init();
    }

    public GradualColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradualColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GradualColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
