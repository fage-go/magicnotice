package com.zhangshuo.common.util;

public interface AsyncCallback<T> {

    /**
     * 异步结果成功
     */
    void onSuccess(T result);

    /**
     * 异步结果失败
     */
    default void onError(Throwable t) {
        if (t != null) {
            t.printStackTrace();
        }
    }
}