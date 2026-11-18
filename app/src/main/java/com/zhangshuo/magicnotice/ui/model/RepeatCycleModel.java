package com.zhangshuo.magicnotice.ui.model;

public class RepeatCycleModel {
    private CharSequence name;
    private int value;
    private boolean selected;

    public RepeatCycleModel(CharSequence name, int value) {
        this.name = name;
        this.value = value;
    }

    public CharSequence getName() {
        return name;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
