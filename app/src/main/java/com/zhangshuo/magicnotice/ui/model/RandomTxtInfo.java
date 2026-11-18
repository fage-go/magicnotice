package com.zhangshuo.magicnotice.ui.model;

import com.zhangshuo.magicnotice.db.model.TxtData;

import java.util.Objects;

public class RandomTxtInfo {
    private int id;
    private int classifyId;
    private String title;
    private String content;
    private boolean selected;

    public RandomTxtInfo() {
    }

    public RandomTxtInfo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public RandomTxtInfo(TxtData txtData) {
        id = txtData.getId();
        classifyId = txtData.getClassifyId();
        title = txtData.getTitle();
        content = txtData.getContent();
        selected = false;
    }

    public TxtData convert() {
        TxtData txtData = new TxtData();
        txtData.setClassifyId(classifyId);
        txtData.setId(id);
        txtData.setTitle(title);
        txtData.setContent(content);
        return txtData;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RandomTxtInfo txtInfo = (RandomTxtInfo) o;
        return id == txtInfo.id && classifyId == txtInfo.classifyId && selected == txtInfo.selected && Objects.equals(title, txtInfo.title) && Objects.equals(content, txtInfo.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classifyId, title, content, selected);
    }
}
