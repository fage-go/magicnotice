package com.zhangshuo.common.pay;

import android.app.Activity;

public class PayManager {

    private IPay pay;

    public void register(IPay pay) {
        this.pay = pay;
    }

    public void init(Activity activity, IPayCallback payCallback) {
        if (pay == null) return;
        pay.init(activity, payCallback);
    }

    public boolean isPay() {
        return true;
//        if (pay == null) return false;
//        return pay.isPay();
    }

    public void hasPay(Activity activity, IPayCallback payCallback) {
        if (pay == null) {
            if (payCallback != null) {
                payCallback.onPayResult(false);
            }
        } else {
            pay.hasPay(activity, payCallback);
        }
    }

    public void toPay(Activity activity, IPayCallback payCallback) {
        if (pay == null) return;
        pay.toPay(activity, payCallback);
    }

    private PayManager() {
    }

    public static PayManager get() {
        return Holder.instance;
    }

    private static final class Holder {
        private static final PayManager instance = new PayManager();
    }
}
