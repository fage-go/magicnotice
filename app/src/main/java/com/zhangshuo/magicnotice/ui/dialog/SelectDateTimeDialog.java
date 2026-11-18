package com.zhangshuo.magicnotice.ui.dialog;

import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.pick.picker.BasePicker;
import com.zhangshuo.common.pick.picker.TimePicker;
import com.zhangshuo.common.pick.widget.DefaultCenterDecoration;
import com.zhangshuo.common.pick.widget.PickerView;
import com.zhangshuo.magicnotice.databinding.DialogSelectDateTimeBinding;

public class SelectDateTimeDialog extends BaseBottomDialog<DialogSelectDateTimeBinding> {

    private OnCallback onCallback;
    private long startTime;
    private long endTime;
    private long selectTime;
    private TimePicker timePicker;

    public SelectDateTimeDialog() {
    }

    public void setRangeTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    public void setSelectTime(long time) {
        this.selectTime = time;
        if (timePicker != null) {
            timePicker.setSelectedDate(selectTime);
        }
    }


    @Override
    public void initView() {
        binding.tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (onCallback != null) {
                    onCallback.onResult(0);
                }
            }
        });
        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                selectTime = timePicker.getSelectedDates().getTime();
                if (onCallback != null) {
                    onCallback.onResult(selectTime);
                }
            }
        });
        createTimePicker();
        binding.flTime.addView(timePicker.view(), new FrameLayout.LayoutParams(-1, -1));
    }

    private void createTimePicker() {
        timePicker = new TimePicker.Builder(getContext(), TimePicker.TYPE_ALL, null)
                .dialog(null)
                .setRangDate(startTime, endTime)
                .setSelectedDate(selectTime)
                .setInterceptor(new BasePicker.Interceptor() {
                    @Override
                    public void intercept(PickerView pickerView, LinearLayout.LayoutParams params) {
                        DefaultCenterDecoration mDefaultCenterDecoration = new DefaultCenterDecoration(getContext());
                        mDefaultCenterDecoration.setLineColor(Color.parseColor("#EAEBED"));
                        pickerView.setCenterDecoration(mDefaultCenterDecoration);
                        pickerView.setColor(Color.parseColor("#0081FF"), Color.parseColor("#989CA8"));
                        pickerView.setVisibleItemCount(5);
                        pickerView.setTextSize(14, 18);
                        pickerView.setItemSize(30);
                    }
                })
                .create();
    }

    public interface OnCallback {
        void onResult(long selectTime);
    }

}
