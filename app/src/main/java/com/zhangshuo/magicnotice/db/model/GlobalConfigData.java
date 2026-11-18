package com.zhangshuo.magicnotice.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GlobalConfigData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String code;
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
