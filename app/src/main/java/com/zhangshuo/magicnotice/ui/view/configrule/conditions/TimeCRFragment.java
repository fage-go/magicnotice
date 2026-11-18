package com.zhangshuo.magicnotice.ui.view.configrule.conditions;

import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.pick.picker.BasePicker;
import com.zhangshuo.common.pick.picker.TimePicker;
import com.zhangshuo.common.pick.widget.DefaultCenterDecoration;
import com.zhangshuo.common.pick.widget.PickerView;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCrTimeBinding;
import com.zhangshuo.magicnotice.ui.model.conditions.TimeConditionsModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.Calendar;

public class TimeCRFragment extends BaseFragment<FragmentCrTimeBinding>
        implements IConfigRule {

    private TimePicker startTimePicker;
    private TimePicker endTimePicker;

    private DefaultCenterDecoration mDefaultCenterDecoration;

    private TimeConditionsModel timeConditionsModel;


    @Override
    protected void initView() {
        mDefaultCenterDecoration = new DefaultCenterDecoration(getContext());
        mDefaultCenterDecoration.setLineColor(Color.parseColor("#EAEBED"));
    }

    @Override
    protected void initData() {
        binding.filterConfig.setConfig(timeConditionsModel.isFilterType(),
                R.string.c_filter_tip_time_y, R.string.c_filter_tip_time_n);

        Calendar startCalendar = Calendar.getInstance();
        if (timeConditionsModel.getStartTime() > 0) {
            startCalendar.setTimeInMillis(TimeUtil.getHMDate(timeConditionsModel.getStartTime()));
        }
        Calendar endCalendar = Calendar.getInstance();
        if (timeConditionsModel.getEndTime() > 0) {
            endCalendar.setTimeInMillis(TimeUtil.getHMDate(timeConditionsModel.getEndTime()));
        }
        int dateWidth = ScreenUtils.getScreenWidth() / 4;

        startTimePicker = createTimePicker(startCalendar.getTimeInMillis());
        binding.flStartTime.addView(startTimePicker.view(), new FrameLayout.LayoutParams(dateWidth, -1));

        endTimePicker = createTimePicker(endCalendar.getTimeInMillis());
        binding.flEndTime.addView(endTimePicker.view(), new FrameLayout.LayoutParams(dateWidth, -1));

    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof TimeConditionsModel) {
            timeConditionsModel = (TimeConditionsModel) object;
        }
        if (timeConditionsModel == null) {
            timeConditionsModel = new TimeConditionsModel();
        }
    }

    @Override
    public Object onResultData() {
        timeConditionsModel.setFilterType(binding.filterConfig.isChecked());
        long startTime = startTimePicker.getSelectedDates().getTime();
        long endTime = endTimePicker.getSelectedDates().getTime();
        timeConditionsModel.setStartTime(TimeUtil.getHMTime(startTime));
        timeConditionsModel.setEndTime(TimeUtil.getHMTime(endTime));
        return timeConditionsModel;
    }

    private TimePicker createTimePicker(long selectTime) {
        return new TimePicker.Builder(getContext(), TimePicker.TYPE_TIME, null)
                .dialog(null)
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
}
