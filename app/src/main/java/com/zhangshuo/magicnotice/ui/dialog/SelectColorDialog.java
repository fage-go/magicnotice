package com.zhangshuo.magicnotice.ui.dialog;

import com.zhangshuo.common.dialog.BaseBottomSheetDialog;
import com.zhangshuo.magicnotice.databinding.DialogSelectColorBinding;
import com.zhangshuo.magicnotice.ui.view.SelectColorView;

import java.util.List;

public class SelectColorDialog extends BaseBottomSheetDialog<DialogSelectColorBinding> {
    private String selectColor;
    private boolean openProMode;
    private OnSelectCallback onSelectCallback;
    private boolean autoDismiss = true;
    private List<String> colorList;

    public static SelectColorDialog create() {
        SelectColorDialog selectColorDialog = new SelectColorDialog();
        return selectColorDialog;
    }

    public SelectColorDialog setSelectColor(String selectColor) {
        this.selectColor = selectColor;
        return this;
    }

    public SelectColorDialog setColorList(List<String> colorList) {
        this.colorList = colorList;
        return this;
    }


    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }

    public SelectColorDialog openProMode(boolean proMode) {
        this.openProMode = proMode;
        return this;
    }

    public SelectColorDialog setOnSelectCallback(OnSelectCallback onSelectCallback) {
        this.onSelectCallback = onSelectCallback;
        return this;
    }

    @Override
    public void initView() {
        binding.selectColor.setContext(getActivity());
        binding.selectColor.setColorList(colorList);
        binding.selectColor.setEnableColorful(true);
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

    public interface OnSelectCallback {
        void onSelect(String color);
    }
}
