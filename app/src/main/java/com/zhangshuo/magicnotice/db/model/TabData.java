package com.zhangshuo.magicnotice.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TabData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int relationDateOffset;
    private String relationIds;
    private String relationPkgs;
    private int openFlag;
    private int lastNoticeId;
    private int receiveFlag;
    private String showKeyWord;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationIds() {
        return relationIds;
    }

    public void setRelationIds(String relationIds) {
        this.relationIds = relationIds;
    }

    public int getOpenFlag() {
        return openFlag;
    }

    public void setOpenFlag(int openFlag) {
        this.openFlag = openFlag;
    }

    public int getLastNoticeId() {
        return lastNoticeId;
    }

    public void setLastNoticeId(int lastNoticeId) {
        this.lastNoticeId = lastNoticeId;
    }

    public int getRelationDateOffset() {
        return relationDateOffset;
    }

    public void setRelationDateOffset(int relationDateOffset) {
        this.relationDateOffset = relationDateOffset;
    }

    public String getRelationPkgs() {
        return relationPkgs;
    }

    public void setRelationPkgs(String relationPkgs) {
        this.relationPkgs = relationPkgs;
    }

    public int getReceiveFlag() {
        return receiveFlag;
    }

    public void setReceiveFlag(int receiveFlag) {
        this.receiveFlag = receiveFlag;
    }

    public String getShowKeyWord() {
        return showKeyWord;
    }

    public void setShowKeyWord(String showKeyWord) {
        this.showKeyWord = showKeyWord;
    }

}
