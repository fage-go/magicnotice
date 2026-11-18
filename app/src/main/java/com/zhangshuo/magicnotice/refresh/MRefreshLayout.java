package com.zhangshuo.magicnotice.refresh;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnMultiListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

public class MRefreshLayout extends SmartRefreshLayout {
    private static final int PRE_LOAD_COUNT_DEFAULT = 10;//预加载默认数量

    private RefreshHeader refreshHeaderView;
    private RefreshFooter refreshFooterView;

    private OnPreLoadCallback onPreLoadCallback;
    private boolean moreLoading = false;//正在加载更多

    private int preloadCount = PRE_LOAD_COUNT_DEFAULT;//默认10条

    public MRefreshLayout(Context context) {
        super(context);
        init();
    }

    public MRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 在列表不满一页时候关闭上拉加载功能
        setEnableLoadMoreWhenContentNotFull(false);
        // 默认不支持下拉刷新
        setEnableRefresh(false);
        // 默认不支持加载更多
        setEnableLoadMore(false);
    }


    @NonNull
    @Override
    public RefreshLayout finishRefresh() {
        if (isRefreshing()) {
            return super.finishRefresh();
        }
        return this;
    }

    public void setRefreshHeaderView(RefreshHeader refreshHeaderView) {
        this.refreshHeaderView = refreshHeaderView;
        setRefreshHeader(refreshHeaderView);
    }

    public void setRefreshFooterView(RefreshFooter refreshFooterView) {
        this.refreshFooterView = refreshFooterView;
        setRefreshFooter(refreshFooterView);
    }

    public void setRefreshFooterView(RefreshFooter refreshFooterView, int width, int height) {
        this.refreshFooterView = refreshFooterView;
        setRefreshFooter(refreshFooterView, width, height);
    }

    public void hideFooterView() {
        notifyStateChanged(RefreshState.Loading);
    }


    @Override
    public RefreshLayout setOnRefreshListener(OnRefreshListener listener) {
        initRefresh();
        return super.setOnRefreshListener(listener);
    }

    @Override
    public RefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener) {
        initLoadMore();
        return super.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!moreLoading) {
                    //如果没有预加载的情况下
                    listener.onLoadMore(refreshLayout);
                }
            }
        });
    }

    @Override
    public RefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener) {
        initRefresh();
        initLoadMore();
        return super.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!moreLoading) {
                    listener.onLoadMore(refreshLayout);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                listener.onRefresh(refreshLayout);
            }
        });
    }

    @Override
    public RefreshLayout setOnMultiListener(OnMultiListener listener) {
        initRefresh();
        initLoadMore();
        return super.setOnMultiListener(listener);
    }

    private void initRefresh() {
        if (refreshHeaderView == null) {
            refreshHeaderView = new MRefreshView(getContext());
            setRefreshHeader(refreshHeaderView);
        }
        setEnableRefresh(true);
    }

    private void initLoadMore() {
        if (refreshFooterView == null) {
            refreshFooterView = new MFooterView(getContext());
            setRefreshFooter(refreshFooterView);
        }
        setEnableLoadMore(true);
    }

    @Override
    public RefreshLayout finishLoadMore(int delayed, boolean success, boolean noMoreData) {
        this.moreLoading = false;
        return super.finishLoadMore(delayed, success, noMoreData);
    }

    /**
     * 设置预加载数量 pageSize * 4/5
     */
    public void setPreloadCount(int pageSize) {
        try {
            this.preloadCount = (int) Math.max(Math.floor(pageSize * (4 / 5f)), PRE_LOAD_COUNT_DEFAULT);
        } catch (Exception ignored) {
        }
    }

    /**
     * 设置预加载功能
     */
    public void openPreLoadMore(@NonNull RecyclerView recyclerView, OnPreLoadCallback onPreLoadCallback) {
        this.moreLoading = false;
        this.onPreLoadCallback = onPreLoadCallback;
        addPreLoad(recyclerView);
    }

    private void addPreLoad(@NonNull RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                boolean isBoundary = false;//是否到达边界
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager sgl = (StaggeredGridLayoutManager) layoutManager;
                    int[] lastVisibleItemPositions = new int[sgl.getSpanCount()];
                    sgl.findLastVisibleItemPositions(lastVisibleItemPositions);
                    if (lastVisibleItemPositions[0] == -1) return;
                    if (lastVisibleItemPositions[0] >= sgl.getItemCount() - preloadCount) {
                        isBoundary = true;
                    }
                } else if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager ll = (LinearLayoutManager) layoutManager;
                    int lastVisibleItemPosition = ll.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition >= ll.getItemCount() - preloadCount) {
                        isBoundary = true;
                    }
                }
                if (isBoundary) {
                    if (!moreLoading && onPreLoadCallback != null && !mFooterNoMoreData) {
                        moreLoading = true;
                        onPreLoadCallback.onPreLoad();
                    }
                }
            }
        });
    }

    public interface OnPreLoadCallback {
        void onPreLoad();
    }
}
