package com.zhangshuo.common.util;


import com.zhangshuo.common.model.EventMessage;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtil {

    /**
     * 注册EventBus
     */
    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    /**
     * 解注册EventBus
     */
    public static void unregister(Object subscriber) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    /**
     * 发事件
     */
    public static <T> void sendEvent(EventMessage<T> event) {
        EventBus.getDefault().post(event);
    }

    /**
     * 发粘性事件
     */
    public static <T> void sendStickyEvent(EventMessage<T> event) {
        EventBus.getDefault().postSticky(event);
    }
}
