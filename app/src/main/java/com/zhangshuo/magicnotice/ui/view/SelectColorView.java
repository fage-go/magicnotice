package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.zhangshuo.common.pick.color.OnColorSelectedListener;
import com.zhangshuo.common.pick.picker.ColorPicker;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectColorView extends View {
    private Context context;
    private Paint mainPaint;
    private List<String> colorList = ColorUtil.getColorList();
    private int stepWidth = 0;
    private List<Rect> rectList = new ArrayList<>();
    private float lastX, lastY;
    private String selectColor;
    private boolean enableColorful = false;
    private boolean enableColorfulAlpha = true;
    private Bitmap colorfulBimap;
    private OnColorCallback onColorCallback;
    private int space;
    private ColorPicker colorPicker;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setColorList(List<String> colorList) {
        this.colorList = colorList;
        this.rectList.clear();
        postInvalidate();
    }

    public void setEnableColorful(boolean enableColorful) {
        this.enableColorful = enableColorful;
        this.colorfulBimap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_select_color);
        postInvalidate();
    }

    public void setEnableColorfulAlpha(boolean enableColorfulAlpha) {
        this.enableColorfulAlpha = enableColorfulAlpha;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int tmpStepWidth = MeasureSpec.getSize(heightMeasureSpec);
        stepWidth = tmpStepWidth - getPaddingTop() - getPaddingBottom();
        this.rectList.clear();
        if (enableColorful) {
            colorList.remove("colorful");
            colorList.add("colorful");
        }
        space = UIUtil.dp2Px(2);
        int width = stepWidth / 2;
        for (int i = 0; i < colorList.size(); i++) {
            int right = stepWidth / 2 + 2 * stepWidth * i;
            Rect rect = new Rect(right, space, right + stepWidth, stepWidth + space);
            rectList.add(rect);
            width = width + rect.width() + stepWidth;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), heightMeasureSpec);
    }

    private void init() {
        mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainPaint.setStrokeWidth(UIUtil.dp2Px(2));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < colorList.size(); i++) {
            Rect rect = rectList.get(i);
            String color = colorList.get(i);
            String ringColor = "#80AAAAAA";
            float radius = rect.width() / 2f - space;
            if (TextUtils.isEmpty(color)) {
                //为空则画线
                mainPaint.setColor(Color.parseColor(ringColor));
                mainPaint.setStyle(Paint.Style.STROKE);
                canvas.drawLine((float) (rect.centerX() + radius * Math.cos(getPI(45))),
                        (float) (rect.centerY() - radius * Math.sin(getPI(45))),
                        (float) (rect.centerX() - radius * Math.sin(getPI(45))),
                        (float) (rect.centerY() + radius * Math.cos(getPI(45))),
                        mainPaint);
            } else if ("colorful".equals(color)) {
                //绘制多彩颜色
                mainPaint.setStyle(Paint.Style.FILL);
                if (colorfulBimap != null) {
                    canvas.drawBitmap(colorfulBimap, null, rect, mainPaint);
                }
                if (!TextUtils.isEmpty(selectColor) && !colorList.contains(selectColor)) {
                    ringColor = this.selectColor;
                }
            } else {
                mainPaint.setColor(Color.parseColor(color));
                mainPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(rect.centerX(), rect.centerY(), radius, mainPaint);
                if (!TextUtils.isEmpty(selectColor) && selectColor.equals(color)) {
                    ringColor = selectColor;
                }
            }

            mainPaint.setColor(Color.parseColor(ringColor));
            mainPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2f, mainPaint);
        }
    }

    private double getPI(int angle) {
        return angle * 2 * Math.PI / 360;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if (Math.abs(lastX - upX) < 20
                        && Math.abs(lastY - upY) < 20) {
                    for (int i = 0; i < rectList.size(); i++) {
                        Rect rect = rectList.get(i);
                        if (rect.contains(upX, upY)) {
                            String color = colorList.get(i);
                            invalidate();
                            //判断是不是colorful
                            if ("colorful".equals(color)) {
                                showColorPicker();
                            } else {
                                this.selectColor = color;
                                postInvalidate();
                                if (onColorCallback != null) {
                                    onColorCallback.onSelect(selectColor);
                                }
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    private void showColorPicker() {
        if (context == null) return;
        if (colorPicker != null && colorPicker.isShowing()) return;
        int defaultColor = Color.BLUE;
        if (!TextUtils.isEmpty(selectColor)) {
            defaultColor = Color.parseColor(selectColor);
        }
        colorPicker = new ColorPicker();
        colorPicker
                .setSelectColor(defaultColor)
                .setEnableColorfulAlpha(false)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        selectColor = ColorUtil.getHexString(selectedColor);
                        postInvalidate();
                        if (onColorCallback != null) {
                            onColorCallback.onSelect(selectColor);
                        }
                    }
                })
                .show(context);
    }

    public void setOnColorCallback(OnColorCallback onColorCallback) {
        this.onColorCallback = onColorCallback;
    }

    public void setSelectColor(String color) {
        this.selectColor = color;
    }


    public interface OnColorCallback {
        void onSelect(String color);
    }

    public SelectColorView(Context context) {
        super(context);
        init();
    }

    public SelectColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SelectColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
