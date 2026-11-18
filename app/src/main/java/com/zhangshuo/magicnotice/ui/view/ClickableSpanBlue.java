package com.zhangshuo.magicnotice.ui.view;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

public abstract class ClickableSpanBlue extends ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
        ds.setColor(Color.parseColor("#0091FF"));
    }
}