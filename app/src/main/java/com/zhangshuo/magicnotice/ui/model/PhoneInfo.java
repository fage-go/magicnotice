package com.zhangshuo.magicnotice.ui.model;

public class PhoneInfo {
    private String name;
    private String phone;

    public PhoneInfo() {
    }

    public PhoneInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
