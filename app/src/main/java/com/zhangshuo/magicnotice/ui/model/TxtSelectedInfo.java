package com.zhangshuo.magicnotice.ui.model;

public class TxtSelectedInfo {
    private String name;
    private String value;
    private boolean selected = false;

    public TxtSelectedInfo(String name, String value, boolean isSelected) {
        this.name = name;
        this.value = value;
        this.selected = isSelected;
    }

    public static TxtSelectedInfo create(String name, String value, String selectValue) {
        boolean isSelected = value.equals(selectValue);
        return new TxtSelectedInfo(name, value, isSelected);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}