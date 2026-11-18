package com.zhangshuo.magicnotice.ui.dialog;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.pick.picker.BasePicker;
import com.zhangshuo.common.pick.picker.TimePicker;
import com.zhangshuo.common.pick.widget.DefaultCenterDecoration;
import com.zhangshuo.common.pick.widget.PickerView;
import com.zhangshuo.magicnotice.databinding.DialogSelectDateBinding;

import java.util.Calendar;
import java.util.Locale;

public class SelectDateDialog extends BaseBottomDialog<DialogSelectDateBinding> {

    private SelectDateDialog.OnCallback onCallback;
    private long startTime;
    private long selectTime;
    private long endTime;
    private TimePicker timePicker;

    public SelectDateDialog() {
    }

    public SelectDateDialog setOnCallback(SelectDateDialog.OnCallback onCallback) {
        this.onCallback = onCallback;
        return this;
    }

    public SelectDateDialog setSelectTime(long time) {
        this.selectTime = time;
        if (timePicker != null) {
            timePicker.setSelectedDate(selectTime);
        }
        return this;
    }

    public SelectDateDialog setRangeDate(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
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
        if (startTime <= 0) {
            try {
                PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
                startTime = packageInfo.firstInstallTime;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (endTime <= 0) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            endTime = calendar.getTimeInMillis();
        }
        timePicker = new TimePicker.Builder(getContext(), TimePicker.TYPE_DATE, null)
                .dialog(null)
                .setSelectedDate(selectTime)
                .setRangDate(startTime, endTime)
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
