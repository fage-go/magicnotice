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
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogSelectTimeBinding;

import java.util.Calendar;

public class SelectTimeDialog extends BaseBottomDialog<DialogSelectTimeBinding> {

    private OnCallback onCallback;
    private long selectTime;
    private TimePicker timePicker;
    private boolean openCancelCallback = false;
    private String cancelTip = StringUtil.getString(R.string.dialog_cancel);

    public SelectTimeDialog() {
    }

    public SelectTimeDialog setCancelTip(String tip) {
        this.cancelTip = tip;
        return this;
    }

    public SelectTimeDialog setCancelCallback(boolean open) {
        this.openCancelCallback = open;
        return this;
    }

    public SelectTimeDialog setSelectTime(long time) {
        this.selectTime = time;
        if (timePicker != null) {
            timePicker.setSelectedDate(selectTime);
        }
        return this;
    }


    public SelectTimeDialog setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
        return this;
    }

    @Override
    public void initView() {
        binding.tvAll.setText(cancelTip);
        binding.tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (openCancelCallback) {
                    if (onCallback != null) {
                        onCallback.onResult(0);
                    }
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
        DefaultCenterDecoration mDefaultCenterDecoration = new DefaultCenterDecoration(getContext());
        mDefaultCenterDecoration.setLineColor(Color.parseColor("#EAEBED"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        long startDate = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endDate = calendar.getTimeInMillis();
        timePicker = new TimePicker.Builder(getContext(), TimePicker.TYPE_TIME, null)
                .dialog(null)
                .setRangDate(startDate, endDate)
                .setSelectedDate(selectTime)
                .setTimeMinuteOffset(1)
                .setInterceptor(new BasePicker.Interceptor() {
                    @Override
                    public void intercept(PickerView pickerView, LinearLayout.LayoutParams params) {
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
