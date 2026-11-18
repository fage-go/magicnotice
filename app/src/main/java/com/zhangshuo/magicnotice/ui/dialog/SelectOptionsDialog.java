package com.zhangshuo.magicnotice.ui.dialog;

import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.pick.dataset.OptionDataSet;
import com.zhangshuo.common.pick.picker.BasePicker;
import com.zhangshuo.common.pick.picker.OptionPicker;
import com.zhangshuo.common.pick.widget.DefaultCenterDecoration;
import com.zhangshuo.common.pick.widget.PickerView;
import com.zhangshuo.magicnotice.databinding.DialogSelectOptionsBinding;

import java.util.ArrayList;
import java.util.List;

public class SelectOptionsDialog extends BaseBottomDialog<DialogSelectOptionsBinding> {

    private OnCallback onCallback;
    private int selectIndex = 0;
    private List<OptionDataSet> optionList = new ArrayList<>();
    private OptionPicker optionPicker;

    public SelectOptionsDialog(List<OptionDataSet> optionList) {
        this.optionList = optionList;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public void setSelectValue(String selectValue) {
        for (int i = 0; i < optionList.size(); i++) {
            if (optionList.get(i).getValue().equals(selectValue)) {
                selectIndex = i;
                break;
            }
        }
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    public void initView() {
        binding.tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                selectIndex = optionPicker.getSelectedPosition()[0];
                if (onCallback != null) {
                    onCallback.onResult(selectIndex, optionList.get(selectIndex));
                }
            }
        });
        createOptions();
        optionPicker.setData(optionList);
        optionPicker.setSelectedWithValues(optionList.get(selectIndex).getValue());
        binding.flOptions.addView(optionPicker.view(), new FrameLayout.LayoutParams(-1, -1));
    }

    private void createOptions() {
        DefaultCenterDecoration mDefaultCenterDecoration = new DefaultCenterDecoration(getContext());
        mDefaultCenterDecoration.setLineColor(Color.parseColor("#EAEBED"));
        optionPicker = new OptionPicker.Builder(getContext(), 1, new OptionPicker.OnOptionSelectListener() {
            @Override
            public void onOptionSelect(OptionPicker picker, int[] selectedPosition, OptionDataSet[] selectedOptions) {
            }
        }).dialog(null)
                .setInterceptor(new BasePicker.Interceptor() {
                    @Override
                    public void intercept(PickerView pickerView, LinearLayout.LayoutParams params) {
                        pickerView.setCenterDecoration(mDefaultCenterDecoration);
                        pickerView.setColor(Color.parseColor("#0081FF"), Color.parseColor("#989CA8"));
                        pickerView.setVisibleItemCount(5);
                        pickerView.setTextSize(14, 18);
                        pickerView.setItemSize(30);
                    }
                }).create();
    }


    public interface OnCallback {
        void onResult(int index, OptionDataSet optionDataSet);
    }
}
