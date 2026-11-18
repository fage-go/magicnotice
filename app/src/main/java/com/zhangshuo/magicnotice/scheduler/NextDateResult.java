package com.zhangshuo.magicnotice.scheduler;

public class NextDateResult {

    private boolean restDayCount;
    private long nextTime;

    public NextDateResult() {
    }

    public NextDateResult(boolean restDayCount, long nextTime) {
        this.restDayCount = restDayCount;
        this.nextTime = nextTime;
    }

    public boolean isRestDayCount() {
        return restDayCount;
    }

    public void setRestDayCount(boolean restDayCount) {
        this.restDayCount = restDayCount;
    }

    public long getNextTime() {
        return nextTime;
    }

    public void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    @Override
    public String toString() {
        return "{" +
                "restDayCount=" + restDayCount +
                ", nextTime=" + nextTime +
                '}';
    }
}
