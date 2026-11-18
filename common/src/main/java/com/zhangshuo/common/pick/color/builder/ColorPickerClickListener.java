package com.zhangshuo.common.pick.color.builder;

import android.content.DialogInterface;

/**
 * Created by Charles Anderson on 4/17/15.
 */
public interface ColorPickerClickListener {
    void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors);
}
