package com.zhangshuo.magicnotice.ui.model;

import com.zhangshuo.magicnotice.db.model.TxtClassifyData;

public class TxtClassifyInfo {
    private int id;
    private String title;
    private Long count;

    public TxtClassifyInfo() {

    }

    public TxtClassifyInfo(TxtClassifyData txtClassifyData) {
        this.id = txtClassifyData.getId();
        this.title = txtClassifyData.getTitle();
    }

    public TxtClassifyData convert() {
        TxtClassifyData txtClassifyData = new TxtClassifyData();
        txtClassifyData.setId(id);
        txtClassifyData.setTitle(title);
        return txtClassifyData;
    }

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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
