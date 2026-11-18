package com.zhangshuo.magicnotice.ui.model;

public class RemindTimeInfo {
    public static final int TYPE_REPEAT_NONE = 0;//不重复
    public static final int TYPE_REPEAT_DAY = 1;//每天
    public static final int TYPE_REPEAT_WEEK = 2;//每周
    public static final int TYPE_REPEAT_MONTH = 3;//每月
    public static final int TYPE_REPEAT_YEAR = 4;//每年

    private String startTime;
    private String endTime;
    private int handlerCount = 1;//提醒次数

    private int repeatType = TYPE_REPEAT_NONE;//类型

    private String repeatData;


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getHandlerCount() {
        return handlerCount;
    }

    public void setHandlerCount(int handlerCount) {
        this.handlerCount = handlerCount;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public String getRepeatData() {
        return repeatData;
    }

    public void setRepeatData(String repeatData) {
        this.repeatData = repeatData;
    }

    @Override
    public String toString() {
        return "{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", handlerCount=" + handlerCount +
                ", repeatType=" + repeatType +
                ", repeatData='" + repeatData + '\'' +
                '}';
    }
}
