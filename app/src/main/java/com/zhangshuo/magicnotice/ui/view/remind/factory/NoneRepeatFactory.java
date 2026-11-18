package com.zhangshuo.magicnotice.ui.view.remind.factory;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.scheduler.NextDateResult;
import com.zhangshuo.magicnotice.ui.model.RemindTimeInfo;
import com.zhangshuo.magicnotice.ui.view.remind.IDataRemindCallback;
import com.zhangshuo.magicnotice.ui.view.remind.IRemindRepeatFactory;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 指定日期 默认为当前日期 / 可选
 * 格式 2022-09-15
 */
public class NoneRepeatFactory implements IRemindRepeatFactory {
    private View rootView;
    private CalendarView mCalendarView;
    private TextView tvMonth, tvCurDay;
    private List<String> dateList = new ArrayList<>();
    private FrameLayout flCur;
    private IDataRemindCallback dataRemindCallback;

    @Override
    public String getRepeatData() {
        //["2022-09-02","2022-09-10","2022-03-02"]
        if (dateList.isEmpty()) {
            return "";
        }
        return GsonUtils.toString(dateList);
    }

    @Override
    public void setRepeatData(String remindData) {
        if (TextUtils.isEmpty(remindData)) return;
        dateList = GsonUtils.toBeanArray(remindData, String.class);
        if (dateList.size() > 0) {
            Calendar[] calendarArray = new Calendar[dateList.size()];
            for (int i = 0; i < dateList.size(); i++) {
                String date = dateList.get(i);
                calendarArray[i] = convertCalendar(date);
            }
            mCalendarView.putMultiSelect(calendarArray);
            Calendar firstCalendar = calendarArray[0];
            mCalendarView.scrollToCalendar(firstCalendar.getYear(), firstCalendar.getMonth(), firstCalendar.getDay());
        }
    }

    private Calendar convertCalendar(String ymdTime) {
        String[] splitTime = ymdTime.split("-");
        com.haibin.calendarview.Calendar resultCalendar = new com.haibin.calendarview.Calendar();
        resultCalendar.setYear(StringUtil.stringToInt(splitTime[0]));
        resultCalendar.setMonth(StringUtil.stringToInt(splitTime[1]));
        resultCalendar.setDay(StringUtil.stringToInt(splitTime[2]));
        return resultCalendar;
    }

    @Override
    public void setTimeData(String startTime, String endTime) {

    }


    @Override
    public View buildRepeatView(Context context) {
        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.view_repeat_none, null);
            mCalendarView = rootView.findViewById(R.id.calendarView);
            tvMonth = rootView.findViewById(R.id.tv_month_day);
            tvCurDay = rootView.findViewById(R.id.tv_current_day);
            flCur = rootView.findViewById(R.id.fl_current);
            flCur.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCalendarView.scrollToCalendar(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());
                }
            });
            //获取今年
            int curYear = mCalendarView.getCurYear();
            int curMonth = mCalendarView.getCurMonth();
            int curDay = mCalendarView.getCurDay();
            tvMonth.setText(curYear + "-" + StringUtil.getFillValue(curMonth));
            tvCurDay.setText(mCalendarView.getCurDay() + "");
            mCalendarView.setRange(curYear, curMonth, curDay, curYear + 1, curMonth, curDay);
            mCalendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
                @Override
                public void onMonthChange(int year, int month) {
                    tvMonth.setText(year + "-" + StringUtil.getFillValue(month));
                    if (dataRemindCallback != null) {
                        dataRemindCallback.onViewChanged(dateList);
                    }
                }
            });
            mCalendarView.setOnCalendarMultiSelectListener(new CalendarView.OnCalendarMultiSelectListener() {
                @Override
                public void onCalendarMultiSelectOutOfRange(com.haibin.calendarview.Calendar calendar) {
                    ToastUtils.showShort(R.string.repeat_select_out);
                }

                @Override
                public void onMultiSelectOutOfSize(com.haibin.calendarview.Calendar calendar, int maxSize) {
                    ToastUtils.showShort(String.format(StringUtil.getString(R.string.repeat_select_max), (maxSize - 1)));
                }

                @Override
                public void onCalendarMultiSelect(com.haibin.calendarview.Calendar calendar, int curSize, int maxSize) {
                    String date = calendar.getYear() + "-" + StringUtil.getFillValue(calendar.getMonth())
                            + "-" + StringUtil.getFillValue(calendar.getDay());
                    if (dateList.contains(date)) {
                        dateList.remove(date);
                    } else {
                        dateList.add(date);
                    }
                    if (dataRemindCallback != null) {
                        dataRemindCallback.onViewChanged(dateList);
                    }
                }
            });
            com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
            calendar.setYear(curYear);
            calendar.setMonth(curMonth);
            calendar.setDay(curDay);
            dateList.add(calendar.getYear() + "-" + StringUtil.getFillValue(calendar.getMonth())
                    + "-" + StringUtil.getFillValue(calendar.getDay()));
            mCalendarView.putMultiSelect(calendar);
        }
        return rootView;
    }

    @Override
    public void setViewCallback(IDataRemindCallback dataRemindCallback) {
        this.dataRemindCallback = dataRemindCallback;
    }

    @Override
    public CharSequence handlerDateTip(RemindTimeInfo remindTimeInfo) {
        SpanUtils spanUtils = new SpanUtils();
        if (TextUtils.isEmpty(remindTimeInfo.getEndTime())) {
            //如果结束时间为空
            spanUtils.append(remindTimeInfo.getStartTime());
        } else {
            spanUtils.append(remindTimeInfo.getStartTime());
            spanUtils.append(" ~ ").append(remindTimeInfo.getEndTime());
            spanUtils.append("  " + remindTimeInfo.getHandlerCount() + StringUtil.getString(R.string.none_repeat_num));
        }
        return spanUtils.create();
    }

    @Override
    public CharSequence handlerRepeatTip(String repeatData) {
        SpanUtils spanUtils = new SpanUtils();
        spanUtils.append(StringUtil.getString(R.string.none_repeat_date));
        spanUtils.append(" - ");
        List<String> dateList = GsonUtils.toBeanArray(repeatData, String.class);
        for (String date : dateList) {
            spanUtils.append(date).append(" ");
        }
        return spanUtils.create();
    }

    @Override
    public NextDateResult handlerNextDate(RemindTimeInfo remindTimeInfo, int currentCount) {
        int handlerCount = remindTimeInfo.getHandlerCount();//每日需要执行的次数
        long startTime = TimeUtil.getTimeByHM(remindTimeInfo.getStartTime()).getTimeInMillis();
        long currentTime = TimeUtil.getCurrentYMDHM();
        long endTime = 0;
        if (!TextUtils.isEmpty(remindTimeInfo.getEndTime())) {
            endTime = TimeUtil.getTimeByHM(remindTimeInfo.getEndTime()).getTimeInMillis();
        }
        return handlerNextDate(startTime, endTime, currentTime, handlerCount, currentCount, remindTimeInfo.getRepeatData());
    }

    private NextDateResult handlerNextDate(long startTime, long endTime, long currentTime, int handlerCount, int currentCount, String repeatData) {
        if (endTime == 0) {
            endTime = startTime;
        }
        List<String> selectDayList = GsonUtils.toBeanArray(repeatData, String.class);
        if (selectDayList.isEmpty()) return null;

        NextDateResult nextDateResult = new NextDateResult();

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        int currentYear = calendar.get(java.util.Calendar.YEAR);
        int currentMonth = calendar.get(java.util.Calendar.MONTH);
        int currentDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        String currentYMD = currentYear
                + "-" + StringUtil.getFillValue(currentMonth + 1)
                + "-" + StringUtil.getFillValue(currentDayOfMonth);

        long nextDateTime;
        float timeCell;

        if (selectDayList.contains(currentYMD) &&
                endTime >= currentTime && currentCount < handlerCount) {
            //如果是今日，且时间满足的情况下
            nextDateResult.setRestDayCount(false);

            timeCell = (endTime - startTime) * 1f / (handlerCount + 1);
            int diffCount = (int) Math.floor((currentTime - startTime) * 1f / timeCell);
            diffCount = Math.max(0, diffCount);
            nextDateTime = startTime + (int) (timeCell * (diffCount + 1));

        } else {
            nextDateResult.setRestDayCount(true);
            currentCount = 0;
            calendar.setTimeInMillis(startTime);

            //需要重新计算下次的
            int tmpIndex = 0;
            while (tmpIndex < 1000) {
                calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                currentYear = calendar.get(java.util.Calendar.YEAR);
                currentMonth = calendar.get(java.util.Calendar.MONTH);
                currentDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                currentYMD = currentYear
                        + "-" + StringUtil.getFillValue(currentMonth + 1)
                        + "-" + StringUtil.getFillValue(currentDayOfMonth);
                if (selectDayList.contains(currentYMD)) {
                    break;
                }
                tmpIndex++;
            }
            if (tmpIndex >= 1000) return nextDateResult;
            long diffTime = endTime - startTime;
            startTime = calendar.getTimeInMillis();
            endTime = startTime + diffTime;

            timeCell = (endTime - startTime) * 1f / (handlerCount + 1);
            nextDateTime = startTime + (int) (timeCell * (currentCount + 1));
        }
        nextDateResult.setNextTime(nextDateTime);
        return nextDateResult;
    }
}
