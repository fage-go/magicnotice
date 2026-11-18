package com.zhangshuo.common.util;

import android.graphics.Color;

import com.zhangshuo.common.AppUtil;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class UIUtil {

    public static int dp2Px(float dp) {
        return AutoSizeUtils.dp2px(AppUtil.get().getApplication(), dp);
    }

    public static int getAlphaColor(float alpha, String colorHex) {
        int a = Math.max(0, (int) (alpha * 255)) << 24;
        return a + 0x00ffffff & Color.parseColor(colorHex);
    }
}
