package com.zhangshuo.magicnotice.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RemindData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int remindType;
    private String title;
    private String content;
    private int classifyId;
    private String timeJson;
    private int notifyNumber;
    private int notifyCount;
    private String mockPkg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRemindType() {
        return remindType;
    }

    public void setRemindType(int remindType) {
        this.remindType = remindType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    public String getTimeJson() {
        return timeJson;
    }

    public void setTimeJson(String timeJson) {
        this.timeJson = timeJson;
    }

    public int getNotifyNumber() {
        return notifyNumber;
    }

    public void setNotifyNumber(int notifyNumber) {
        this.notifyNumber = notifyNumber;
    }

    public int getNotifyCount() {
        return notifyCount;
    }

    public void setNotifyCount(int notifyCount) {
        this.notifyCount = notifyCount;
    }

    public String getMockPkg() {
        return mockPkg;
    }

    public void setMockPkg(String mockPkg) {
        this.mockPkg = mockPkg;
    }
}
