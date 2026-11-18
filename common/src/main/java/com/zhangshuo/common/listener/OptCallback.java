package com.zhangshuo.common.listener;


public interface OptCallback<T> {
    void onSuc(T t);

    void onFail(T t);
}
