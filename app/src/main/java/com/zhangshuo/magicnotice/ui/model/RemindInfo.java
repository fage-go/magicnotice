package com.zhangshuo.magicnotice.ui.model;

import com.zhangshuo.magicnotice.db.model.RemindData;
import com.zhangshuo.magicnotice.db.model.TxtData;

public class RemindInfo {
    public static final int TYPE_EXACT = 0;//固定文案
    public static final int TYPE_REMIND = 1;//随机文案
    private int id;
    private int remindType;
    private String title;
    private String content;
    private int classifyId;
    private String timeJson;
    private int notifyNumber;//当前几次
    private int notifyCount;//共计几次
    private String mockPkg;

    private TxtData randomTxtData;
    private TxtClassifyInfo txtClassifyInfo;
    private CharSequence dateStrTip;
    private CharSequence repeatTip;
    private RemindTimeInfo remindTimeInfo;

    public RemindInfo() {
    }

    public RemindInfo(RemindData remindData) {
        this.id = remindData.getId();
        this.remindType = remindData.getRemindType();
        this.title = remindData.getTitle();
        this.content = remindData.getContent();
        this.classifyId = remindData.getClassifyId();
        this.timeJson = remindData.getTimeJson();
        this.notifyNumber = remindData.getNotifyNumber();
        this.notifyCount = remindData.getNotifyCount();
        this.mockPkg = remindData.getMockPkg();
    }

    public RemindData convert() {
        RemindData remindData = new RemindData();
        remindData.setId(id);
        remindData.setRemindType(remindType);
        remindData.setTitle(title);
        remindData.setContent(content);
        remindData.setClassifyId(classifyId);
        remindData.setTimeJson(timeJson);
        remindData.setNotifyNumber(notifyNumber);
        remindData.setNotifyCount(notifyCount);
        remindData.setMockPkg(mockPkg);
        return remindData;
    }


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

    public TxtData getRandomTxtData() {
        return randomTxtData;
    }

    public void setRandomTxtData(TxtData randomTxtData) {
        this.randomTxtData = randomTxtData;
    }

    public TxtClassifyInfo getTxtClassifyInfo() {
        return txtClassifyInfo;
    }

    public void setTxtClassifyInfo(TxtClassifyInfo txtClassifyInfo) {
        this.txtClassifyInfo = txtClassifyInfo;
    }

    public CharSequence getDateStrTip() {
        return dateStrTip;
    }

    public void setDateStrTip(CharSequence dateStrTip) {
        this.dateStrTip = dateStrTip;
    }

    public RemindTimeInfo getRemindTimeInfo() {
        return remindTimeInfo;
    }

    public void setRemindTimeInfo(RemindTimeInfo remindTimeInfo) {
        this.remindTimeInfo = remindTimeInfo;
    }

    public CharSequence getRepeatTip() {
        return repeatTip;
    }

    public void setRepeatTip(CharSequence repeatTip) {
        this.repeatTip = repeatTip;
    }

    public String getMockPkg() {
        return mockPkg;
    }

    public void setMockPkg(String mockPkg) {
        this.mockPkg = mockPkg;
    }
}
