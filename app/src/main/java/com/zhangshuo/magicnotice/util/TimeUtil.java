package com.zhangshuo.magicnotice.util;

import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    public static boolean isSuperCaiDan() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return month == 12;
    }


    public static Calendar getTimeByHM(String hm) {
        String[] hmArray = hm.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, StringUtil.stringToInt(hmArray[0]));
        calendar.set(Calendar.MINUTE, StringUtil.stringToInt(hmArray[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Long parseYMDHMDate(String ymdhm) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date parse = simpleDateFormat.parse(ymdhm);
            return parse.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static Long parseYMDTime(String ymd) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parse = simpleDateFormat.parse(ymd);
            return parse.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static Long parseHMTime(String hm) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            Date parse = simpleDateFormat.parse(hm);
            return parse.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String formatHHMMTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    public static String formatHMTime(long HMTime) {
        int hour = (int) (HMTime / 100);
        int minute = (int) (HMTime % 100);
        String hourStr = "";
        String minuteStr = "";
        if (hour < 10) {
            hourStr = "0" + hour;
        } else {
            hourStr = "" + hour;
        }
        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = "" + minute;
        }
        return hourStr + ":" + minuteStr;
    }

    public static int getHMTime(long time) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        return mCalendar.get(Calendar.HOUR_OF_DAY) * 100 + mCalendar.get(Calendar.MINUTE);
    }

    public static int getCurrentHMTime() {
        Calendar mCalendar = Calendar.getInstance();
        return mCalendar.get(Calendar.HOUR_OF_DAY) * 100 + mCalendar.get(Calendar.MINUTE);
    }

    public static long getHMDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, (int) (time / 100));
        calendar.set(Calendar.MINUTE, (int) (time % 100));
        return calendar.getTimeInMillis();
    }

    public static Long getCurrentYMDHM() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static Date getTodayYMD() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = simpleDateFormat.format(date);
        try {
            return simpleDateFormat.parse(formatDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static long parseYMDTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = simpleDateFormat.format(new Date(time));
        try {
            return simpleDateFormat.parse(formatDate).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String formatYMDTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    public static String formatYMDHMSDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        return simpleDateFormat.format(date);
    }

    public static String formatYMDHMDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date);
    }

    public static String formatYMDDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }


    /**
     * 日期格式化
     *
     * @return
     */
    public static String dateFormatTime(long time) {
        return dateFormatTime(time, false);
    }

    public static String formatHomeNotificationTime(long time) {
        return dateFormatTime(time, true);
    }

    private static String dateFormatTime(long time, boolean alwaysShowWeekday) {
        Date date = new Date(time);
        String weekdayPrefix = alwaysShowWeekday ? getWeekday(date).trim() + " " : "";
        if (isThisYear(date)) {//今年
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            if (isToday(date)) { //今天
//                int minute = minutesAgo(time);
//                if (minute < 60) {//1小时之内
//                    if (minute <= 1) {//一分钟之内，显示刚刚
//                        return StringUtil.getString(R.string.time_format_recent);
//                    } else {
//                        return minute + StringUtil.getString(R.string.time_format_minute);
//                    }
//                } else {
                return weekdayPrefix + StringUtil.getString(R.string.time_format_today) + simpleDateFormat.format(date);
//                }
            } else {
                if (isYestYesterday(date)) {//昨天，显示昨天
                    return weekdayPrefix + StringUtil.getString(R.string.time_format_yestoday) + simpleDateFormat.format(date);
                } else if (isThisWeek(date)) {//本周,显示周几
                    return (alwaysShowWeekday ? weekdayPrefix : getWeekday(date) + " ")
                            + simpleDateFormat.format(date);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                    return weekdayPrefix + sdf.format(date);
                }
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return weekdayPrefix + sdf.format(date);
        }
    }

    private static String getWeekday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return StringUtil.getString(R.string.time_format_monday);
            case Calendar.TUESDAY:
                return StringUtil.getString(R.string.time_format_tuesday);
            case Calendar.WEDNESDAY:
                return StringUtil.getString(R.string.time_format_wednesday);
            case Calendar.THURSDAY:
                return StringUtil.getString(R.string.time_format_thursday);
            case Calendar.FRIDAY:
                return StringUtil.getString(R.string.time_format_friday);
            case Calendar.SATURDAY:
                return StringUtil.getString(R.string.time_format_saturday);
            default:
                return StringUtil.getString(R.string.time_format_sunday);
        }
    }

    private static int minutesAgo(long time) {
        return (int) ((System.currentTimeMillis() - time) / (60000));
    }

    private static boolean isToday(Date date) {
        Date now = new Date();
        return (date.getYear() == now.getYear()) && (date.getMonth() == now.getMonth()) && (date.getDate() == now.getDate());
    }

    private static boolean isYestYesterday(Date date) {
        Date now = new Date();
        return (date.getYear() == now.getYear()) && (date.getMonth() == now.getMonth()) && (date.getDate() + 1 == now.getDate());
    }

    private static boolean isThisWeek(Date date) {
        Calendar todayCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();

        todayCal.setTime(new Date());
        dateCal.setTime(date);

        // 1.比较当前日期在年份中的周数是否相同
        if (todayCal.get(Calendar.WEEK_OF_YEAR) == dateCal.get(Calendar.WEEK_OF_YEAR)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isThisYear(Date date) {
        Date now = new Date();
        return date.getYear() == now.getYear();
    }
}
