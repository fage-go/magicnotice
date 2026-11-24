package com.zhangshuo.magicnotice.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.zhangshuo.common.dialog.BasePopupDialog;
import com.zhangshuo.common.pick.dataset.OptionDataSet;
import com.zhangshuo.common.pick.picker.BasePicker;
import com.zhangshuo.common.pick.picker.OptionPicker;
import com.zhangshuo.common.pick.widget.DefaultCenterDecoration;
import com.zhangshuo.common.pick.widget.PickerView;
import com.zhangshuo.magicnotice.databinding.PopupSelectAnimBinding;
import com.zhangshuo.magicnotice.ui.model.OptionModel;
import com.zhangshuo.common.util.UIUtil;

import java.util.List;

public class SelectOptionsPopup extends BasePopupDialog<PopupSelectAnimBinding> {

    private String defaultAnimValue;
    private List<OptionModel> optionAnimList;

    private OnCallback onCallback;
    private OptionPicker animOptionPicker;


    public SelectOptionsPopup(Fragment fragment, List<OptionModel> animOptionList, String defaultAnimValue) {
        super(fragment);
        init(animOptionList, defaultAnimValue);
    }

    public SelectOptionsPopup(Dialog dialog, List<OptionModel> animOptionList, String defaultAnimValue) {
        super(dialog);
        init(animOptionList, defaultAnimValue);
    }

    private void init(List<OptionModel> animOptionList, String defaultAnimValue) {
        this.defaultAnimValue = defaultAnimValue;
        this.optionAnimList = animOptionList;

        setBackgroundColor(Color.TRANSPARENT);
        setWidth(-2);
        setHeight(UIUtil.dp2Px(170));
        setFitSize(false);

        animOptionPicker = createOptions();
        animOptionPicker.setData(optionAnimList);
        animOptionPicker.setSelectedWithValues(defaultAnimValue);
        binding.flAnim.addView(animOptionPicker.view());

    }


    public SelectOptionsPopup setOnCallback(SelectOptionsPopup.OnCallback onCallback, OnDismissListener onDismissListener) {
        this.onCallback = onCallback;
        setOnDismissListener(onDismissListener);
        return this;
    }

    private OptionPicker createOptions() {
        DefaultCenterDecoration mDefaultCenterDecoration = new DefaultCenterDecoration(getContext());
        mDefaultCenterDecoration.setLineColor(Color.parseColor("#EAEBED"));
        return new OptionPicker.Builder(getContext(), 1, new OptionPicker.OnOptionSelectListener() {
            @Override
            public void onOptionSelect(OptionPicker picker, int[] selectedPosition, OptionDataSet[] selectedOptions) {
                if (onCallback != null) {
                    onCallback.onSelect(
                            (OptionModel) animOptionPicker.getSelectedOptions()[0]
                    );
                }
            }
        }).dialog(null)
                .setInterceptor(new BasePicker.Interceptor() {
                    @Override
                    public void intercept(PickerView pickerView, LinearLayout.LayoutParams params) {
                        pickerView.setCenterDecoration(mDefaultCenterDecoration);
                        pickerView.setColor(Color.parseColor("#0081FF"), Color.parseColor("#989CA8"));
                        pickerView.setVisibleItemCount(5);
                        pickerView.setTextSize(14, 16);
                        pickerView.setItemSize(30);
                    }
                }).create();
    }

    public interface OnCallback {
        void onSelect(OptionModel animOptionModel);
    }

}
