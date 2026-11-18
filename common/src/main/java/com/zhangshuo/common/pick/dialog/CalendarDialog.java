package com.zhangshuo.common.pick.dialog;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.zhangshuo.common.pick.model.CalendarDate;
import com.zhangshuo.common.databinding.DialogCalendarBinding;
import com.zhangshuo.common.dialog.BaseTipsDialog;
import com.zhangshuo.common.pick.adapter.CalendarPageAdapter;
import com.zhangshuo.common.pick.widget.CalendarFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarDialog extends BaseTipsDialog<DialogCalendarBinding> {
    private long startDate;
    private long endDate;
    private boolean isChoiceModelSingle = false;
    private CalendarPageAdapter calendarPageAdapter;
    private OnDataSelectListener onDataSelectListener;
    private List<CalendarDate> calendarDateList = new ArrayList<>();

    public CalendarDialog setChoiceModelSingle(boolean choiceModelSingle) {
        isChoiceModelSingle = choiceModelSingle;
        return this;
    }

    public CalendarDialog setOnDataSelectListener(OnDataSelectListener onDataSelectListener) {
        this.onDataSelectListener = onDataSelectListener;
        return this;
    }

    public CalendarDialog setSelectDataList(List<CalendarDate> dataList) {
        calendarDateList.clear();
        calendarDateList.addAll(dataList);
        return this;
    }

    public CalendarDialog setRangeDate(long startDate, long endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    @Override
    public void initView() {
        binding.tvDate.getPaint().setFakeBoldText(true);
        calendarPageAdapter = new CalendarPageAdapter(getChildFragmentManager(), getLifecycle());
        calendarPageAdapter.setInfo(isChoiceModelSingle, new IDataBridge() {
            @Override
            public void onSelect(CalendarDate calendarDate) {
                int index = -1;
                for (int i = 0; i < calendarDateList.size(); i++) {
                    if (calendarDateList.get(i).isSameDay(calendarDate)) {
                        index = i;
                        break;
                    }
                }
                if (calendarDate.isSelect()) {
                    if (index == -1) {
                        calendarDateList.add(calendarDate);
                    }
                } else {
                    if (index > -1) {
                        calendarDateList.remove(index);
                    }
                }
            }

            @Override
            public List<CalendarDate> getSelectedList() {
                return calendarDateList;
            }
        });
        binding.viewPager.setAdapter(calendarPageAdapter);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Fragment currentFragment = getChildFragmentManager().findFragmentById(position);
                if (currentFragment instanceof CalendarFragment) {
                    ((CalendarFragment) currentFragment).checkSelectDate();
                }
                setDateInfo();

            }
        });
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        binding.mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (onDataSelectListener != null) {
                    onDataSelectListener.onSelect(calendarDateList);
                }
            }
        });
        setDateInfo();
    }

    private void setDateInfo() {
        int position = binding.viewPager.getCurrentItem();
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, position);
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH) + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year)
                .append(" - ");
        if (month < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(month);
        binding.tvDate.setText(stringBuilder.toString());
    }

    public interface IDataBridge {
        void onSelect(CalendarDate calendarDate);

        List<CalendarDate> getSelectedList();
    }


    public interface OnDataSelectListener {
        void onSelect(List<CalendarDate> calendarDateList);
    }
}
