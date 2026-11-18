package com.zhangshuo.magicnotice.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.zhangshuo.common.dialog.BasePopupDialog;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.databinding.PopupSelectColorBinding;
import com.zhangshuo.magicnotice.ui.view.SelectColorView;
import com.zhangshuo.magicnotice.util.ColorUtil;

import java.util.LinkedList;
import java.util.List;

public class SelectColorPopup extends BasePopupDialog<PopupSelectColorBinding> {
    private String selectColor;
    private boolean openProMode;
    private OnSelectCallback onSelectCallback;
    private boolean autoDismiss = false;
    private boolean canNone = false;
    private List<String> colorList = ColorUtil.getColorList();

    public SelectColorPopup(Fragment fragment, String defaultSelect) {
        super(fragment);
        this.selectColor = defaultSelect;
        init();
    }

    public SelectColorPopup(Dialog dialog, String defaultSelect) {
        super(dialog);
        this.selectColor = defaultSelect;
        init();

    }


    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        setWidth(-2);
        setHeight(UIUtil.dp2Px(80));
        setFitSize(false);

        binding.selectColor.setContext(getContext());
        binding.selectColor.setColorList(colorList);
        binding.selectColor.setEnableColorful(true);
        binding.selectColor.setEnableColorfulAlpha(false);
        binding.selectColor.setSelectColor(selectColor);
        binding.selectColor.setOnColorCallback(new SelectColorView.OnColorCallback() {
            @Override
            public void onSelect(String color) {
                if (onSelectCallback != null) {
                    onSelectCallback.onSelect(color);
                }
                if (autoDismiss)
                    dismiss();
            }
        });
    }


    public SelectColorPopup setSelectColor(String selectColor) {
        this.selectColor = selectColor;
        binding.selectColor.setSelectColor(selectColor);
        return this;
    }

    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }

    public SelectColorPopup openProMode(boolean proMode) {
        this.openProMode = proMode;
        return this;
    }

    public SelectColorPopup canNone(boolean canNone) {
        this.canNone = canNone;
        this.colorList = new LinkedList<>();
        if (canNone) {
            this.colorList.add("");
        }
        this.colorList.addAll(ColorUtil.getColorList());
        binding.selectColor.setColorList(colorList);
        return this;
    }

    public SelectColorPopup setOnCallback(OnSelectCallback onSelectCallback, OnDismissListener onDismissListener) {
        this.onSelectCallback = onSelectCallback;
        setOnDismissListener(onDismissListener);
        return this;
    }

    public interface OnSelectCallback {
        void onSelect(String color);
    }
}
