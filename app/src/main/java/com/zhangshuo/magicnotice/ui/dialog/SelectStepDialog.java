package com.zhangshuo.magicnotice.ui.dialog;

import com.zhangshuo.common.dialog.BaseBottomSheetDialog;
import com.zhangshuo.magicnotice.databinding.DialogSelectStepBinding;
import com.zhangshuo.magicnotice.ui.view.StepSeekBar;

import java.util.ArrayList;
import java.util.List;

public class SelectStepDialog extends BaseBottomSheetDialog<DialogSelectStepBinding> {
    private int selectValue;
    private List<Integer> dataList = new ArrayList<>();
    private boolean openProMode;
    private OnSelectCallback onSelectCallback;
    private boolean autoDismiss = true;

    public static SelectStepDialog create(List<Integer> dataList) {
        SelectStepDialog selectColorDialog = new SelectStepDialog();
        selectColorDialog.dataList = dataList;
        return selectColorDialog;
    }

    public void setSelectValue(int selectValue) {
        int index = dataList.indexOf(selectValue);
        if (index == -1) return;
        this.selectValue = selectValue;
        binding.barrageStepFontSize.setProgress(index);
    }

    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }

    public SelectStepDialog setOnSelectCallback(OnSelectCallback onSelectCallback) {
        this.onSelectCallback = onSelectCallback;
        return this;
    }


    @Override
    public void initView() {
        binding.barrageStepFontSize.setMaxProgress(dataList.size());
        binding.barrageStepFontSize.setProgress(0);
        binding.barrageStepFontSize.setOnProgressCallback(new StepSeekBar.OnProgressCallback() {
            @Override
            public void onProgress(int maxProgress, int progress) {
                if (onSelectCallback != null) {
                    onSelectCallback.onSelect(dataList.get(progress));
                }
                if(autoDismiss){
                    dismiss();
                }
            }
        });
    }

    public interface OnSelectCallback {
        String onSelect(int value);
    }
}
