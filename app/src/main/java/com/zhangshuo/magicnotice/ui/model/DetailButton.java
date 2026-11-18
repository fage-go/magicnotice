package com.zhangshuo.magicnotice.ui.model;

public class DetailButton {
    private String name;
    private int bgColor;

    public DetailButton(String name, int bgColor) {
        this.name = name;
        this.bgColor = bgColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
