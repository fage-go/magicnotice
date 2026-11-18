package com.zhangshuo.magicnotice.notice.factory;

import android.content.Context;

public interface IProcess<D, R> {

    R onProcess(Context context, D d);
}
