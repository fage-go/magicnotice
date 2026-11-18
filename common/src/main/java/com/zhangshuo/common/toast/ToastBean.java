package com.zhangshuo.common.toast;


public class ToastBean {
    private String msg;//消息内容
    private int icon;
    private long startTime;//开始展示时间
    private long keepTime;//mill

    public ToastBean(String msg, int icon, long keepTime) {
        this.msg = msg;
        this.icon = icon;
        this.keepTime = keepTime;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public long getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(long keepTime) {
        this.keepTime = keepTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
