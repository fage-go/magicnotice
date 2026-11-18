package com.zhangshuo.magicnotice.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Deprecated
@Entity
public class SchedulerData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String dateStr;
    private String timeStr;
    private boolean retain;
    private int classifyId;
    private int notifyNumber;
    private String behaviorList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public String getBehaviorList() {
        return behaviorList;
    }

    public void setBehaviorList(String behaviorList) {
        this.behaviorList = behaviorList;
    }

    public int getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    public int getNotifyNumber() {
        return notifyNumber;
    }

    public void setNotifyNumber(int notifyNumber) {
        this.notifyNumber = notifyNumber;
    }
}
