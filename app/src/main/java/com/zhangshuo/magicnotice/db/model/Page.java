package com.zhangshuo.magicnotice.db.model;

import java.util.List;

public class Page<T> {
    private int page;
    private int pageSize;
    private int count;
    private long totalCount;
    private List<T> dataList;

    public Page() {
    }

    public Page(int page, int pageSize, long totalCount, List<T> dataList) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.dataList = dataList;
        this.count = dataList.size();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
