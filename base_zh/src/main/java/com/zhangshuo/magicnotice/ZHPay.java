package com.zhangshuo.magicnotice;

import android.app.Activity;

import com.zhangshuo.common.pay.IPay;
import com.zhangshuo.common.pay.IPayCallback;

public class ZHPay implements IPay {

    @Override
    public void init(Activity activity, IPayCallback payCallback) {
        if (payCallback != null) {
            payCallback.onInit(true);
        }
    }

    @Override
    public boolean isPay() {
        return true;
    }

    @Override
    public void hasPay(Activity activity, IPayCallback payCallback) {
        if (payCallback != null) {
            payCallback.onPayResult(true);
        }
    }


    @Override
    public void toPay(Activity activity, IPayCallback payCallback) {

    }
}
