package com.zhangshuo.magicnotice.ui.model;

public class TabConfigTipInfo {
    private String name;
    private String value;
    private String color;

    public TabConfigTipInfo() {
    }

    public TabConfigTipInfo(String name) {
        this.name = name;
    }

    public TabConfigTipInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public TabConfigTipInfo(String name, String value, String color) {
        this.name = name;
        this.value = value;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
