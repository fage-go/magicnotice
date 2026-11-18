package com.zhangshuo.magicnotice.ui.model;

public class TxtConditionsSelectInfo {
    private String name;
    private String value;
    private boolean select;

    public TxtConditionsSelectInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
