package com.zhangshuo.common.pick.widget;

import androidx.recyclerview.widget.GridLayoutManager;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.databinding.FragmentCalendarBinding;
import com.zhangshuo.common.pick.adapter.CalendarAdapter;
import com.zhangshuo.common.pick.dialog.CalendarDialog;
import com.zhangshuo.common.pick.model.CalendarDate;
import com.zhangshuo.common.pick.model.Lunar;
import com.zhangshuo.common.pick.model.Solar;
import com.zhangshuo.common.pick.util.CalendarUtils;
import com.zhangshuo.common.pick.util.LunarSolarConverter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends BaseFragment<FragmentCalendarBinding> {
    private int mYear;
    private int mMonth;
    private boolean isChoiceModelSingle;
    private CalendarAdapter calendarAdapter;
    private CalendarDialog.IDataBridge dataBridge;

    public static CalendarFragment newInstance(int year, int month, boolean isChoiceModelSingle, CalendarDialog.IDataBridge dataBridge) {
        CalendarFragment calendarFragment = new CalendarFragment();
        calendarFragment.setInfo(year, month, isChoiceModelSingle, dataBridge);
        return calendarFragment;
    }

    public void setInfo(int year, int month, boolean isChoiceModelSingle, CalendarDialog.IDataBridge dataBridge) {
        this.mYear = year;
        this.mMonth = month;
        this.isChoiceModelSingle = isChoiceModelSingle;
        this.dataBridge = dataBridge;
    }

    @Override
    protected void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 7);
        calendarAdapter = new CalendarAdapter(getContext());
        binding.rvDay.setLayoutManager(gridLayoutManager);
        binding.rvDay.setAdapter(calendarAdapter);
    }

    @Override
    protected void initData() {
        calendarAdapter.addData(getCalendarDate(mYear, mMonth));
        calendarAdapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                List<CalendarDate> dataList = calendarAdapter.getDataList();
                if (isChoiceModelSingle) {
                    for (int i = 0; i < dataList.size(); i++) {
                        CalendarDate calendarDate = dataList.get(i);
                        if (calendarDate.isSelect()) {
                            calendarDate.setIsSelect(false);
                            calendarAdapter.notifyItemChanged(i);
                            if (dataBridge != null) {
                                dataBridge.onSelect(calendarDate);
                            }
                        }
                    }
                    dataList.get(position).setIsSelect(true);
                } else {
                    CalendarDate calendarDate = dataList.get(position);
                    calendarDate.setIsSelect(!calendarDate.isSelect());
                }
                calendarAdapter.notifyItemChanged(position);
                if (dataBridge != null) {
                    dataBridge.onSelect(calendarAdapter.getItem(position));
                }
            }
        });
        checkSelectDate();
    }


    public void checkSelectDate() {
        List<CalendarDate> dataList = calendarAdapter.getDataList();
        for (CalendarDate calendarDate : dataBridge.getSelectedList()) {
            for (CalendarDate date : dataList) {
                if (date.getSolar().solarDay == calendarDate.getSolar().solarDay
                        && date.getSolar().solarMonth == calendarDate.getSolar().solarMonth
                        && date.getSolar().solarYear == calendarDate.getSolar().solarYear) {
                    date.setSelect(true);
                }
            }
        }
    }

    public static List<CalendarDate> getCalendarDate(int year, int month) {
        List<CalendarDate> mListDate = new ArrayList<>();
        List<CalendarUtils.CalendarSimpleDate> list = null;
        try {
            list = CalendarUtils.getEverydayOfMonth(year, month);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int count = list.size();

        for (int i = 0; i < count; i++) {
            Solar solar = new Solar();
            solar.solarYear = list.get(i).getYear();
            solar.solarMonth = list.get(i).getMonth();
            solar.solarDay = list.get(i).getDay();
            Lunar lunar = LunarSolarConverter.SolarToLunar(solar);
            mListDate.add(new CalendarDate(month == list.get(i).getMonth(), false, solar, lunar));
        }

        return mListDate;
    }
}
