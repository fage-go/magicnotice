package com.zhangshuo.common.model;

/**
 * @author: zhangshuo
 * Date:2020/12/27
 * @doc: EventBus通用事件
 */
public class EventMessage<T> {

    private int code;
    private T data;

    public EventMessage(int code) {
        this.code = code;
    }

    public EventMessage(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
