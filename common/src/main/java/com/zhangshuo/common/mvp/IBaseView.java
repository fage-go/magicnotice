package com.zhangshuo.common.mvp;


import android.content.Context;

import com.zhangshuo.common.page.PageState;

public interface IBaseView {

    /**
     * 获取context
     */
    Context getCtx();

    /**
     * 切换页面状态
     */
    void showPageState(PageState pageState);

    /**
     * 显示toast
     */
    void showToast(String msg);

    /**
     * 显示loading
     */
    void showLoading();

    void showLoading(boolean cancelable);

    void showLoading(String loadText);

    /**
     * 隐藏loading
     */
    void hideLoading();

    /**
     * 关闭页面
     */
    void finishAct();
}
