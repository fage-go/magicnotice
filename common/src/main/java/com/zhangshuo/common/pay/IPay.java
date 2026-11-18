package com.zhangshuo.common.pay;

import android.app.Activity;

public interface IPay {

    void init(Activity activity,IPayCallback payCallback);

    boolean isPay();

    void hasPay(Activity activity,IPayCallback payCallback);

    void toPay(Activity activity,IPayCallback payCallback);
}
