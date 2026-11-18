package com.zhangshuo.common.pay;

public interface IPayCallback {

    void onInit(boolean success);

    void onPayResult(boolean success);
}
