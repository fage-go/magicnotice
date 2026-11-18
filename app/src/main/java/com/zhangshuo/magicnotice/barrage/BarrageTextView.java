package com.zhangshuo.magicnotice.barrage;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BarrageTextView extends androidx.appcompat.widget.AppCompatTextView {
    private int measureType = 0;//0默认，1为无限长

    public void setMeasureType(int measureType) {
        this.measureType = measureType;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (measureType == 1) {
            int width = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(width, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public BarrageTextView(@NonNull Context context) {
        super(context);
    }

    public BarrageTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BarrageTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
