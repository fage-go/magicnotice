package com.zhangshuo.magicnotice.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.zhangshuo.common.dialog.BasePopupDialog;
import com.zhangshuo.magicnotice.databinding.PopupSelectStepBinding;
import com.zhangshuo.magicnotice.ui.view.StepSeekBar;
import com.zhangshuo.common.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectStepPopup extends BasePopupDialog<PopupSelectStepBinding> {
    private int selectValue;
    private List<Integer> dataList = new ArrayList<>();
    private boolean openProMode;
    private OnSelectCallback onSelectCallback;
    private boolean autoDismiss = false;


    public SelectStepPopup setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
        return this;
    }

    public SelectStepPopup setOnCallback(OnSelectCallback onSelectCallback, OnDismissListener onDismissListener) {
        this.onSelectCallback = onSelectCallback;
        setOnDismissListener(onDismissListener);
        return this;
    }

    public SelectStepPopup(Fragment fragment, List<Integer> dataList, int defaultSelect) {
        super(fragment);
        init(dataList, defaultSelect);
    }

    public SelectStepPopup(Dialog dialog, List<Integer> dataList, int defaultSelect) {
        super(dialog);
        init(dataList, defaultSelect);
    }

    private void init(List<Integer> dataList, int defaultSelect) {
        setBackgroundColor(Color.TRANSPARENT);
        setWidth(-2);
        setHeight(UIUtil.dp2Px(60));
        setFitSize(false);

        this.dataList = dataList;
        this.selectValue = defaultSelect;
        binding.stepSeekBar.setMaxProgress(dataList.size());
        binding.stepSeekBar.setProgress(Math.max(dataList.indexOf(selectValue), 0));
        binding.stepSeekBar.setOnProgressCallback(new StepSeekBar.OnProgressCallback() {
            @Override
            public void onProgress(int maxProgress, int progress) {
                if (onSelectCallback != null) {
                    onSelectCallback.onSelect(dataList.get(progress));
                }
                if (autoDismiss) {
                    dismiss();
                }
            }
        });
    }


    public interface OnSelectCallback {
        String onSelect(int value);
    }
}
