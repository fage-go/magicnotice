package com.zhangshuo.common.page;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhangshuo.common.R;
import com.zhangshuo.common.view.loading.LoadingDrawable;


public class PageView extends FrameLayout {

    private PageState pageState = PageState.content();

    private View contentView;

    private View stateView;
    private ImageView stateIv;
    private TextView stateTv;
    private LinearLayout pageStateLl;

    /**
     * 显示页面的内容
     */
    public void setLayoutView(View contentView) {
        this.contentView = contentView;
        addView(contentView, 0, new LayoutParams(-1, -1));
    }

    /**
     * 切换页面的状态
     *
     * @param pageState
     */
    public void changePageState(PageState pageState) {
        if (this.pageState.getState() == pageState.getState()) return;
        this.pageState = pageState;
        if (pageState.getState() == PageState.LOADING) {
            if (contentView != null)
                contentView.setVisibility(GONE);
            if (stateView != null)
                stateView.setVisibility(VISIBLE);
            if (stateIv != null) {
                stateTv.setText("");
            }
            if (stateIv != null) {
                LoadingDrawable loadingDrawable = new LoadingDrawable(getContext());
                stateIv.setBackground(loadingDrawable);
                loadingDrawable.start();
            }
            pageStateLl.setGravity(Gravity.CENTER);
        } else if (pageState.getState() == PageState.CONTENT) {
            if (contentView != null)
                contentView.setVisibility(VISIBLE);
            if (stateView != null)
                stateView.setVisibility(GONE);
            if (stateIv != null) {
                stateIv.setBackground(null);
                stateTv.setText("");
            }
            if (pageStateLl != null)
                pageStateLl.removeAllViews();
        } else {
            if (contentView != null)
                contentView.setVisibility(GONE);
            if (stateView != null) {
                stateView.setVisibility(VISIBLE);
                if (pageState.getCustomEmptyView() != null) {
                    if (pageStateLl != null) {
                        pageStateLl.removeAllViews();
                        pageStateLl.addView(pageState.getCustomEmptyView());
                        pageStateLl.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                } else {
                    if (pageState.getImgResId() > 0)
                        stateIv.setBackgroundResource(pageState.getImgResId());
                    if (pageState.getTipResId() > 0)
                        stateTv.setText(pageState.getTipResId());
                    if (!TextUtils.isEmpty(pageState.getTipContent()))
                        stateTv.setText(pageState.getTipContent());
                }
            }
        }
    }

    private void init() {
        if (stateView != null) return;
        setBackgroundResource(R.color.base_bg_default);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        setLayoutParams(layoutParams);
        stateView = LayoutInflater.from(getContext()).inflate(R.layout.base_page_view_state, null);
        addView(stateView, new LayoutParams(-1, -1));
        stateView.setVisibility(GONE);
        stateIv = findViewById(R.id.page_state_iv);
        stateTv = findViewById(R.id.page_state_tv);
        pageStateLl = findViewById(R.id.page_state_ll);
    }

    public PageView(@NonNull Context context) {
        super(context);
        init();
    }

    public PageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
}
