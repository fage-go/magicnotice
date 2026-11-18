package com.zhangshuo.magicnotice.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Deprecated
@Entity
public class SchedulerRecordData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int schedulerId;
    private long postTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(int schedulerId) {
        this.schedulerId = schedulerId;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }
}
