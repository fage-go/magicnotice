package com.zhangshuo.common.pick.model;

import java.util.Calendar;

/**
 * Created by joybar on 2/24/16.
 */
public class CalendarDate {

    private Lunar lunar = new Lunar();//农历
    private Solar solar = new Solar();//公历
    private boolean isInThisMonth; //是否在当月
    private boolean isSelect;//是否被选中

    public CalendarDate(int year, int month, int day, boolean isInThisMonth, boolean isSelect, Lunar lunar) {
        this.isInThisMonth = isInThisMonth;
        this.isSelect = isSelect;
        this.lunar = lunar;
    }


    public CalendarDate(boolean isInThisMonth, boolean isSelect, Solar solar, Lunar lunar) {
        this.isInThisMonth = isInThisMonth;
        this.isSelect = isSelect;
        this.solar = solar;
        this.lunar = lunar;
    }

    public boolean isSameDay(CalendarDate calendarDate) {
        return getSolar().solarYear == calendarDate.getSolar().solarYear &&
                getSolar().solarMonth == calendarDate.getSolar().solarMonth &&
                getSolar().solarDay == calendarDate.getSolar().solarDay;
    }

    public boolean isInThisMonth() {
        return isInThisMonth;
    }

    public void setIsInThisMonth(boolean isInThisMonth) {
        this.isInThisMonth = isInThisMonth;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public Solar getSolar() {
        return solar;
    }

    public void setSolar(Solar solar) {
        this.solar = solar;
    }

    public void setInThisMonth(boolean inThisMonth) {
        isInThisMonth = inThisMonth;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public Lunar getLunar() {
        return lunar;
    }

    public void setLunar(Lunar lunar) {
        this.lunar = lunar;
    }

    public String getSolarInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSolar().solarYear).append("-");
        if (getSolar().solarMonth < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(getSolar().solarMonth).append("-");
        if (getSolar().solarDay < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(getSolar().solarDay);
        return stringBuilder.toString();
    }

    public String getLunarInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getLunar().lunarYear).append("-");
        if (getLunar().lunarMonth < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(getLunar().lunarMonth).append("-");
        if (getLunar().lunarDay < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(getLunar().lunarDay);
        return stringBuilder.toString();
    }


    public long getSolarInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getSolar().solarYear);
        calendar.set(Calendar.MONTH, getSolar().solarMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, getSolar().solarDay);
        return calendar.getTimeInMillis();
    }

    public long getLunarInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getLunar().lunarYear);
        calendar.set(Calendar.MONTH, getLunar().lunarMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, getLunar().lunarDay);
        return calendar.getTimeInMillis();
    }
}
