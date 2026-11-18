package com.zhangshuo.magicnotice.ui.model;

public class DetailInfo {
    private String name;
    private String value;
    private boolean canClick;

    public DetailInfo() {
    }

    public DetailInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public DetailInfo(String name, String value, boolean canClick) {
        this.name = name;
        this.value = value;
        this.canClick = canClick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isCanClick() {
        return canClick;
    }

    public void setCanClick(boolean canClick) {
        this.canClick = canClick;
    }
}
