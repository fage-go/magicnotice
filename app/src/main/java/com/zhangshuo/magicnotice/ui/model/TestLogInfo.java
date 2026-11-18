package com.zhangshuo.magicnotice.ui.model;

import java.util.ArrayList;
import java.util.List;

public class TestLogInfo {
    private String name;
    private int step;
    private int maxStep;
    private boolean result;
    private List<TestLogItemInfo> logItemInfoList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getMaxStep() {
        return maxStep;
    }

    public void setMaxStep(int maxStep) {
        this.maxStep = maxStep;
    }

    public List<TestLogItemInfo> getLogItemInfoList() {
        return logItemInfoList;
    }

    public void setLogItemInfoList(List<TestLogItemInfo> logItemInfoList) {
        this.logItemInfoList = logItemInfoList;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
