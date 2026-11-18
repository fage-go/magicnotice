package com.zhangshuo.magicnotice.util;

import java.util.ArrayList;
import java.util.List;

public class SelectedListHelper<T> {
    private boolean isSingle = false;
    private List<T> dataList = new ArrayList<>();
    private OnJudgeCallback<T> onJudgeCallback;

    public void addSelectedList(List<T> initDataList) {
        if (isSingle) {
            dataList.clear();
        }
        for (int i = 0; i < initDataList.size(); i++) {
            T data = initDataList.get(i);
            for (int j = 0; j < dataList.size(); j++) {
                T oldData = dataList.get(j);
                if (onJudgeCallback.isSame(oldData, data)) {
                    dataList.remove(j);
                    j--;
                }
            }
        }
        this.dataList.addAll(initDataList);
    }

    public SelectedListHelper(boolean isSingle, OnJudgeCallback<T> onJudgeCallback) {
        this.isSingle = isSingle;
        this.onJudgeCallback = onJudgeCallback;
    }

    public void onSelected(T data, boolean selected) {
        if (isSingle) {
            dataList.clear();
        }
        for (int i = 0; i < dataList.size(); i++) {
            if (onJudgeCallback.isSame(dataList.get(i), data)) {
                dataList.remove(i);
                i--;
            }
        }
        if (selected) {
            dataList.add(data);
        }
    }

    public List<T> getDataList() {
        return dataList;
    }

    public interface OnJudgeCallback<T> {
        boolean isSame(T data, T judgeData);
    }
}
