package com.zhangshuo.magicnotice.ui.view.remind.factory;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SpanUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.common.view.wheel.ScrollPickerAdapter;
import com.zhangshuo.common.view.wheel.ScrollPickerView;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.scheduler.NextDateResult;
import com.zhangshuo.magicnotice.ui.model.RemindTimeInfo;
import com.zhangshuo.magicnotice.ui.view.remind.IDataRemindCallback;
import com.zhangshuo.magicnotice.ui.view.remind.IRemindRepeatFactory;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 日期设置 每日
 */
public class DayRepeatFactory implements IRemindRepeatFactory {
    private View rootView;
    private ScrollPickerView rvDay;
    private TextView tvTip;
    private IDataRemindCallback dataRemindCallback;
    private String date = StringUtil.getString(R.string.day_repeat_every);
    private ScrollPickerAdapter scrollPickerAdapter;

    @Override
    public String getRepeatData() {
        return date;
    }


    @Override
    public void setRepeatData(String remindData) {
        date = remindData;
        int index = 0;
        if (!StringUtil.getString(R.string.day_repeat_every).equals(date)) {
            index = StringUtil.stringToInt(date);
        }
        RecyclerView.LayoutManager layoutManager = rvDay.getLayoutManager();
        layoutManager.scrollToPosition(index);
    }

    @Override
    public void setTimeData(String startTime, String endTime) {

    }

    @Override
    public View buildRepeatView(Context context) {
        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.view_repeat_day, null);
            rvDay = rootView.findViewById(R.id.rv_day);
            tvTip = rootView.findViewById(R.id.tv_day_tip);

            List<String> dataList = new ArrayList<>();
            dataList.add(StringUtil.getString(R.string.day_repeat_every));
            for (int i = 1; i < 31; i++) {
                dataList.add(String.valueOf(i));
            }
            scrollPickerAdapter = new ScrollPickerAdapter.ScrollPickerAdapterBuilder<String>(context)
                    .setDataList(dataList)
                    .selectedItemOffset(1)
                    .visibleItemNumber(3)
                    .setDivideLineColor("#E5E5E5")
                    .setItemViewProvider(null)
                    .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                        @Override
                        public void onScrolled(View currentItemView) {
                            date = (String) currentItemView.getTag();
                            int position = dataList.indexOf(date);
                            tvTip.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                            if (dataRemindCallback != null) {
                                dataRemindCallback.onViewChanged(date);
                            }
                        }
                    }).build();
            rvDay.setAdapter(scrollPickerAdapter);

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
            spanUtils.append("  " + remindTimeInfo.getHandlerCount() + StringUtil.getString(R.string.day_repeat_num));
        }
        return spanUtils.create();
    }

    @Override
    public CharSequence handlerRepeatTip(String repeatData) {
        SpanUtils spanUtils = new SpanUtils();
        if (StringUtil.getString(R.string.day_repeat_every).equals(repeatData)) {
            spanUtils.append(StringUtil.getString(R.string.day_repeat_every_day));
        } else {
            spanUtils.append(String.format(StringUtil.getString(R.string.day_repeat_step_day), repeatData));
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
        NextDateResult nextDateResult = new NextDateResult();
        long nextDateTime;
        float timeCell;
        if (endTime < currentTime || currentCount >= handlerCount) {
            nextDateResult.setRestDayCount(true);
            currentCount = 0;
            //如果今日时间已经不满足，或者今日次数已经超限了，则使用明天
            int diffTime = 1;
            if (!StringUtil.getString(R.string.day_repeat_every).equals(repeatData)) {
                diffTime = StringUtil.stringToInt(repeatData) + 1;
            }
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(startTime);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);
            startCalendar.add(Calendar.DAY_OF_MONTH, diffTime);
            startTime = startCalendar.getTimeInMillis();
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(endTime);
            endCalendar.set(Calendar.SECOND, 0);
            endCalendar.set(Calendar.MILLISECOND, 0);
            endCalendar.add(Calendar.DAY_OF_MONTH, diffTime);
            endTime = endCalendar.getTimeInMillis();
            timeCell = (endTime - startTime) * 1f / (handlerCount + 1);
            nextDateTime = startTime + (int) (timeCell * (currentCount + 1));
        } else {
            //如果在今日时间内
            nextDateResult.setRestDayCount(false);
            timeCell = (endTime - startTime) * 1f / (handlerCount + 1);
            int diffCount = (int) Math.floor((currentTime - startTime) * 1f / timeCell);
            diffCount = Math.max(0, diffCount);
            nextDateTime = startTime + (int) (timeCell * (diffCount + 1));
        }
        nextDateResult.setNextTime(nextDateTime);
        return nextDateResult;
    }
}
