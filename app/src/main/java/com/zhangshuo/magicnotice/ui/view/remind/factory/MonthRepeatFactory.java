package com.zhangshuo.magicnotice.ui.view.remind.factory;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
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
 * 日期设置 每月
 * 8号 / 1号～31号（单选）
 */
public class MonthRepeatFactory implements IRemindRepeatFactory {
    private View rootView;
    private CalendarView mCalendarView;
    private List<String> dateList = new ArrayList<>();
    private IDataRemindCallback dataRemindCallback;

    @Override
    public String getRepeatData() {
        //["02"，"04","12"]
        if (dateList.isEmpty()) {
            return "";
        }
        return GsonUtils.toString(dateList);
    }

    @Override
    public void setRepeatData(String remindData) {
        if (TextUtils.isEmpty(remindData)) return;
        dateList = GsonUtils.toBeanArray(remindData, String.class);
        com.haibin.calendarview.Calendar[] calendarArray = new com.haibin.calendarview.Calendar[dateList.size()];
        for (int i = 0; i < dateList.size(); i++) {
            String date = dateList.get(i);
            //02
            calendarArray[i] = convertCalendar(date);
        }
        mCalendarView.putMultiSelect(calendarArray);
    }

    @Override
    public void setTimeData(String startTime, String endTime) {

    }

    private com.haibin.calendarview.Calendar convertCalendar(String dayTime) {
        com.haibin.calendarview.Calendar resultCalendar = new com.haibin.calendarview.Calendar();
        resultCalendar.setYear(mCalendarView.getCurYear());
        resultCalendar.setMonth(mCalendarView.getCurMonth());
        resultCalendar.setDay(StringUtil.stringToInt(dayTime));
        return resultCalendar;
    }


    @Override
    public View buildRepeatView(Context context) {
        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.view_repeat_month, null);
            mCalendarView = rootView.findViewById(R.id.calendarView);
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
                    String date = StringUtil.getFillValue(calendar.getDay());
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
        String startTime = remindTimeInfo.getStartTime();
        String endTime = remindTimeInfo.getEndTime();
        spanUtils.append(startTime);
        if (!TextUtils.isEmpty(endTime)) {
            spanUtils.append(" ~ ").append(endTime);
            spanUtils.append("  " + remindTimeInfo.getHandlerCount() + StringUtil.getString(R.string.month_repeat_num));
        }
        return spanUtils.create();
    }

    @Override
    public CharSequence handlerRepeatTip(String repeatData) {
        //["2022-09-02","2022-09-10","2022-03-02"]
        SpanUtils spanUtils = new SpanUtils();
        spanUtils.append(StringUtil.getString(R.string.month_repeat_every_month));
        spanUtils.append(" - ");
        List<String> selectValues = GsonUtils.toBeanArray(repeatData, String.class);
        for (String selectValue : selectValues) {
            spanUtils.append(selectValue).append(" ");
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
        int currentDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        String currentD = StringUtil.getFillValue(currentDayOfMonth);

        long nextDateTime;
        float timeCell;

        if (selectDayList.contains(currentD) &&
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
                currentDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                currentD = StringUtil.getFillValue(currentDayOfMonth);
                if (selectDayList.contains(currentD)) {
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
