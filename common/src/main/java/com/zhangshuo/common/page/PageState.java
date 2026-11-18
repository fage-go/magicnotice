package com.zhangshuo.common.page;

import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.zhangshuo.common.R;


public class PageState {

    static final int CONTENT = 1000;
    static final int LOADING = 1001;
    static final int EMPTY_DATA = 1002;
    static final int NET_ERROR = 1003;

    private int state;
    private int imgResId;
    private int tipResId;
    private View customEmptyView;
    private String tipContent;
    private View.OnClickListener onClickListener;

    public PageState(int state) {
        this.state = state;
    }

    public PageState setImgResId(@DrawableRes int imgResId) {
        this.imgResId = imgResId;
        return this;
    }

    public PageState setTipResId(@StringRes int tipResId) {
        this.tipResId = tipResId;
        return this;
    }

    public PageState setTipContent(String tipContent) {
        this.tipContent = tipContent;
        return this;
    }

    public PageState setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public PageState setCustomEmptyView(View customEmptyView) {
        this.customEmptyView = customEmptyView;
        return this;
    }

    public int getState() {
        return state;
    }

    public int getImgResId() {
        return imgResId;
    }

    public int getTipResId() {
        return tipResId;
    }

    public String getTipContent() {
        return tipContent;
    }

    public View getCustomEmptyView() {
        return customEmptyView;
    }

    public static PageState loading() {
        return new PageState(LOADING);
    }

    public static PageState content() {
        return new PageState(CONTENT);
    }

    public static PageState empty() {
        return new PageState(EMPTY_DATA).setImgResId(R.mipmap.icon_no_data);
    }

}
