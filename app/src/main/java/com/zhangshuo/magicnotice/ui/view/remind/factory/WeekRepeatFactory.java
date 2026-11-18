package com.zhangshuo.magicnotice.ui.view.remind.factory;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SpanUtils;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.scheduler.NextDateResult;
import com.zhangshuo.magicnotice.ui.adapter.RepeatCycleAdapter;
import com.zhangshuo.magicnotice.ui.model.RemindTimeInfo;
import com.zhangshuo.magicnotice.ui.model.RepeatCycleModel;
import com.zhangshuo.magicnotice.ui.view.remind.IDataRemindCallback;
import com.zhangshuo.magicnotice.ui.view.remind.IRemindRepeatFactory;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * - 每周
 * - 周期设置 周一、周二、周三、周四、周五、周六、周日（多选）
 * [1,2,3,4,5,6]
 */
public class WeekRepeatFactory implements IRemindRepeatFactory {
    private View rootView;
    private RecyclerView rvWeek;
    private RepeatCycleAdapter adapter;
    private IDataRemindCallback dataRemindCallback;

    @Override
    public String getRepeatData() {
        //[1,2,3,4,5,6]
        List<Integer> selectValues = adapter.getSelectValues();
        if (selectValues.isEmpty()) {
            return "";
        }
        return GsonUtils.toString(selectValues);
    }

    @Override
    public void setRepeatData(String remindData) {
        try {
            List<Integer> selectValues = GsonUtils.toBeanArray(remindData, Integer.class);
            for (Integer selectValue : selectValues) {
                adapter.getDataList().get(selectValue - 1).setSelected(true);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
    }

    @Override
    public void setTimeData(String startTime, String endTime) {

    }

    @Override
    public View buildRepeatView(Context context) {
        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.view_repeat_week, null);
            rvWeek = rootView.findViewById(R.id.rv_week);
            rvWeek.setLayoutManager(new GridLayoutManager(context, 7));
            adapter = new RepeatCycleAdapter(context);
            adapter.setOnCallback(new RepeatCycleAdapter.OnCallback() {
                @Override
                public void onSelected(List<Integer> dataList) {
                    if (dataRemindCallback != null) {
                        dataRemindCallback.onViewChanged(dataList);
                    }
                }
            });
            rvWeek.setAdapter(adapter);
            adapter.addData(Arrays.asList(
                    new RepeatCycleModel(StringUtil.getString(R.string.week_repeat_1), 1),
                    new RepeatCycleModel(StringUtil.getString(R.string.week_repeat_2), 2),
                    new RepeatCycleModel(StringUtil.getString(R.string.week_repeat_3), 3),
                    new RepeatCycleModel(StringUtil.getString(R.string.week_repeat_4), 4),
                    new RepeatCycleModel(StringUtil.getString(R.string.week_repeat_5), 5),
                    new RepeatCycleModel(StringUtil.getString(R.string.week_repeat_6), 6),
                    new RepeatCycleModel(StringUtil.getString(R.string.week_repeat_7), 7)
            ));

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
            spanUtils.append("  " + remindTimeInfo.getHandlerCount() + StringUtil.getString(R.string.week_repeat_num));
        }
        return spanUtils.create();
    }

    @Override
    public CharSequence handlerRepeatTip(String repeatData) {
        SpanUtils spanUtils = new SpanUtils();
        List<Integer> selectWeekList = GsonUtils.toBeanArray(repeatData, Integer.class);
        if (selectWeekList.isEmpty()) {
            spanUtils.append(StringUtil.getString(R.string.week_repeat_tip_no));
        } else if (selectWeekList.size() == 7) {
            spanUtils.append(StringUtil.getString(R.string.week_repeat_tip_every));
        } else {
            List<String> weekNameList = Arrays.asList(StringUtil.getString(R.string.week_repeat_1),StringUtil.getString(R.string.week_repeat_2),
                    StringUtil.getString(R.string.week_repeat_3),StringUtil.getString(R.string.week_repeat_4),
                    StringUtil.getString(R.string.week_repeat_5),StringUtil.getString(R.string.week_repeat_6),
                    StringUtil.getString(R.string.week_repeat_7));
            for (Integer integer : selectWeekList) {
                spanUtils.append(weekNameList.get(integer - 1)).append(" ");
            }
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
        List<Integer> selectWeekList = GsonUtils.toBeanArray(repeatData, Integer.class);
        if (selectWeekList.isEmpty()) return null;
        NextDateResult nextDateResult = new NextDateResult();
        //计算是否为当前周几,
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        long nextDateTime;
        float timeCell;

        if (selectWeekList.contains(week)
                && endTime >= currentTime && currentCount < handlerCount) {
            //如果今日是满足的情况下,且没有过期时间，且当前次数也没有过期
            nextDateResult.setRestDayCount(false);

            timeCell = (endTime - startTime) * 1f / (handlerCount + 1);
            int diffCount = (int) Math.floor((currentTime - startTime) * 1f / timeCell);
            diffCount = Math.max(0, diffCount);
            nextDateTime = startTime + (int) (timeCell * (diffCount + 1));

        } else {
            nextDateResult.setRestDayCount(true);
            currentCount = 0;
            //需要重新计算下次的
            int tmpIndex = 0;
            while (tmpIndex < 1000) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                week = calendar.get(Calendar.DAY_OF_WEEK);
                if (selectWeekList.contains(week)) {
                    break;
                }
                tmpIndex++;
            }
            if (tmpIndex >= 1000) return null;
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
