package com.zhangshuo.magicnotice.ui.model;

import java.io.Serializable;

public class KeywordInfo implements Serializable {
    private String word;
    private String color = "#0081FF";

    public KeywordInfo() {
    }

    public KeywordInfo(String word, String color) {
        this.word = word;
        this.color = color;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
