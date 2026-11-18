package com.zhangshuo.common.pick.picker;

import android.view.View;

import com.zhangshuo.common.databinding.DialogColorPickerBinding;
import com.zhangshuo.common.dialog.BaseTipsDialog;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.pick.color.OnColorSelectedListener;

public class ColorPicker extends BaseTipsDialog<DialogColorPickerBinding> {
    private OnColorSelectedListener onColorSelectedListener;
    private int selectColor;
    private boolean enableColorfulAlpha = true;

    @Override
    public void initView() {
        binding.colorPickerView.setInitialColor(selectColor, false);
        binding.vAlphaSlider.setColor(selectColor);
        binding.vLightnessSlider.setColor(selectColor);
        binding.vAlphaSlider.setVisibility(enableColorfulAlpha ? View.VISIBLE : View.GONE);
        binding.tvCancel.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                dismiss();
            }
        });
        binding.tvConfirm.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                if (onColorSelectedListener != null) {
                    onColorSelectedListener.onColorSelected(binding.colorPickerView.getSelectedColor());
                }
                dismiss();
            }
        });
    }

    public ColorPicker setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.onColorSelectedListener = listener;
        return this;
    }

    public ColorPicker setSelectColor(int color) {
        this.selectColor = color;
        return this;
    }


    public ColorPicker setEnableColorfulAlpha(boolean enableColorfulAlpha) {
        this.enableColorfulAlpha = enableColorfulAlpha;
        return this;
    }
}

