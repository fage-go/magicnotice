package com.zhangshuo.magicnotice.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.db.service.RemindDataService;
import com.zhangshuo.magicnotice.ui.model.RemindInfo;
import com.zhangshuo.magicnotice.ui.model.RemindTimeInfo;
import com.zhangshuo.magicnotice.ui.view.remind.IRemindRepeatFactory;
import com.zhangshuo.magicnotice.ui.view.remind.factory.DayRepeatFactory;
import com.zhangshuo.magicnotice.ui.view.remind.factory.MonthRepeatFactory;
import com.zhangshuo.magicnotice.ui.view.remind.factory.NoneRepeatFactory;
import com.zhangshuo.magicnotice.ui.view.remind.factory.WeekRepeatFactory;
import com.zhangshuo.magicnotice.ui.view.remind.factory.YearRepeatFactory;

import java.util.Calendar;

public class SchedulerUtil {
    private AlarmManager alarmManager;

    public AlarmManager getAlarmManager() {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) AppUtil.get().getApplication().getSystemService(Context.ALARM_SERVICE);
        }
        return alarmManager;
    }

    public void configAlarm(RemindInfo remindInfo) {
        //先删除
        deleteAlarm(remindInfo);
        //新增
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long currentTime = calendar.getTimeInMillis();

        PendingIntent pendingIntent = getPendingIntent(remindInfo.getId());
        NextDateResult nextDateResult = getNextRemindTime(remindInfo.getRemindTimeInfo(), remindInfo.getNotifyNumber());
        if (nextDateResult == null || nextDateResult.getNextTime() <= 100 || remindInfo.getId() <= 0)
            return;
        long nextDate = nextDateResult.getNextTime();
        if (nextDateResult.isRestDayCount()) {
            //需要进行更新当前次数
            remindInfo.setNotifyNumber(0);
            RemindDataService.insertOrUpdate(remindInfo, new AsyncCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {

                }
            });
        }
        if (nextDate > currentTime) {
            //设置闹钟
            getAlarmManager().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextDate, pendingIntent);
        }
    }

    public void deleteAlarm(RemindInfo remindInfo) {
        if (remindInfo == null) return;
        PendingIntent pendingIntent = getPendingIntent(remindInfo.getId());
        getAlarmManager().cancel(pendingIntent);
    }


    private PendingIntent getPendingIntent(int remindId) {
        Intent intent = new Intent(AppUtil.get().getApplication(), SchedulerBroadcast.class);
        intent.putExtra("remindId", remindId);
        return PendingIntent.getBroadcast(AppUtil.get().getApplication(), remindId, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
    }


    public static CharSequence getRemindDateTip(RemindTimeInfo remindTimeInfo) {
        IRemindRepeatFactory remindRepeatFactory = getRemindRepeatFactory(remindTimeInfo.getRepeatType());
        return remindRepeatFactory.handlerDateTip(remindTimeInfo);
    }

    public static CharSequence getRepeatTip(RemindTimeInfo remindTimeInfo) {
        IRemindRepeatFactory remindRepeatFactory = getRemindRepeatFactory(remindTimeInfo.getRepeatType());
        return remindRepeatFactory.handlerRepeatTip(remindTimeInfo.getRepeatData());
    }

    public static NextDateResult getNextRemindTime(RemindTimeInfo remindTimeInfo, int notifyNumber) {
        IRemindRepeatFactory remindRepeatFactory = getRemindRepeatFactory(remindTimeInfo.getRepeatType());
        return remindRepeatFactory.handlerNextDate(remindTimeInfo, notifyNumber);
    }

    public static IRemindRepeatFactory getRemindRepeatFactory(int type) {
        IRemindRepeatFactory remindRepeatFactory;
        if (type == RemindTimeInfo.TYPE_REPEAT_DAY) {
            remindRepeatFactory = new DayRepeatFactory();
        } else if (type == RemindTimeInfo.TYPE_REPEAT_WEEK) {
            remindRepeatFactory = new WeekRepeatFactory();
        } else if (type == RemindTimeInfo.TYPE_REPEAT_MONTH) {
            remindRepeatFactory = new MonthRepeatFactory();
        } else if (type == RemindTimeInfo.TYPE_REPEAT_YEAR) {
            remindRepeatFactory = new YearRepeatFactory();
        } else {
            remindRepeatFactory = new NoneRepeatFactory();
        }
        return remindRepeatFactory;
    }


    public static SchedulerUtil get() {
        return Holder.instance;
    }

    private SchedulerUtil() {

    }

    public static final class Holder {
        private static final SchedulerUtil instance = new SchedulerUtil();
    }
}
