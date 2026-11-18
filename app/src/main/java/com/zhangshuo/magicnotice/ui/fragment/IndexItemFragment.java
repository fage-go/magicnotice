package com.zhangshuo.magicnotice.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.page.PageState;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.databinding.FragmentIndexItemBinding;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.model.Page;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.refresh.MRefreshView;
import com.zhangshuo.magicnotice.ui.adapter.NotificationAdapter;
import com.zhangshuo.magicnotice.ui.model.TabInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class IndexItemFragment extends BaseFragment<FragmentIndexItemBinding> {
    private TabInfo tabInfo;
    private boolean haveInit = false;
    private LinearLayoutManager linearLayoutManager;
    private boolean scrollToTopPending;

    public IndexItemFragment() {

    }

    public static IndexItemFragment buildIndexItemFragment(TabInfo tabInfo) {
        IndexItemFragment indexItemFragment = new IndexItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tabInfo", tabInfo);
        indexItemFragment.setArguments(bundle);
        return indexItemFragment;
    }

    private NotificationAdapter notificationAdapter;

    @Override
    protected void reSavedInstanceState(Bundle bundle) {
        super.reSavedInstanceState(bundle);
        if (bundle != null) {
            Serializable tabInfo = bundle.getSerializable("tabInfo");
            if (tabInfo instanceof TabInfo) {
                this.tabInfo = (TabInfo) tabInfo;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tabInfo", tabInfo);
    }

    @Override
    protected void initView() {
        Bundle arguments = getArguments();
        Serializable tabInfo = arguments.getSerializable("tabInfo");
        if (tabInfo instanceof TabInfo) {
            this.tabInfo = (TabInfo) tabInfo;
        }
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                queryDataList(false, false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                RefreshHeader refreshHeader = refreshLayout.getRefreshHeader();
                boolean isAllData = false;
                if (refreshHeader instanceof MRefreshView) {
                    isAllData = ((MRefreshView) refreshHeader).isNeedAll();
                }
                queryDataList(true, isAllData);
            }
        });
        RefreshHeader refreshHeader = binding.refreshLayout.getRefreshHeader();
        if (refreshHeader instanceof MRefreshView) {
            ((MRefreshView) refreshHeader).setTips(R.string.refresh_news_pull, R.string.refresh_news, R.string.refresh_news_all);
        }
        notificationAdapter = new NotificationAdapter(getCtx(), true);
        setAdapterHighLight();
        linearLayoutManager = new LinearLayoutManager(getCtx(), RecyclerView.VERTICAL, false);
        binding.rvList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getCtx(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_drawable));
        binding.rvList.addItemDecoration(dividerItemDecoration);
        binding.rvList.setAdapter(notificationAdapter);
        binding.rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager != null) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition > 5) {
                        if (binding.fabTop.getVisibility() == View.GONE) {
                            binding.fabTop.setVisibility(View.VISIBLE);
                            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                            scaleAnimation.setDuration(200);
                            binding.fabTop.startAnimation(scaleAnimation);
                        }
                    } else {
                        binding.fabTop.clearAnimation();
                        binding.fabTop.setVisibility(View.GONE);
                    }
                }
            }
        });
        binding.fabTop.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                scrollToTop();
            }
        });
    }

    public boolean isForTab(int tabId) {
        TabInfo currentTab = tabInfo;
        if (currentTab == null && getArguments() != null) {
            Serializable argument = getArguments().getSerializable("tabInfo");
            if (argument instanceof TabInfo) {
                currentTab = (TabInfo) argument;
            }
        }
        return currentTab != null && currentTab.getId() == tabId;
    }

    public void scrollToTop() {
        if (getView() == null || binding == null || linearLayoutManager == null) {
            scrollToTopPending = true;
            return;
        }
        scrollToTopPending = false;
        binding.rvList.stopScroll();
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
        binding.fabTop.clearAnimation();
        binding.fabTop.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!haveInit) {
            queryDataList(true, true);
            haveInit = true;
        }
        if (scrollToTopPending) {
            scrollToTop();
        }
    }

    public void checkReset(TabInfo tab) {
        if (tabInfo == null || tab == null) return;
        if (tabInfo.getId() == tab.getId()) {
            if (!tabInfo.isSameRelation(tab)) {
                this.tabInfo = tab;
                setAdapterHighLight();
                if (haveInit) {
                    queryDataList(true, true);
                }
            }
        }
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.UPDATE_INDEX_NOTIFICATION) {
            queryDataList(true, true);
        } else if (eventMessage.getCode() == EventCodes.REFRESH_NOTIFICATION_LOCK) {
            //如果不是-1
            if (tabInfo == null) return;
            NotificationData notificationData = (NotificationData) eventMessage.getData();
            if (notificationData == null) return;
            if (-1 == tabInfo.getId()) {
                //如果是收藏
                if (notificationData.getLock() == 1) {
                    notificationAdapter.addItemData(0, notificationData);
                    binding.rvList.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.rvList.smoothScrollToPosition(0);
                        }
                    });
                } else {
                    int position = notificationAdapter.getPositionById(notificationData.getId());
                    if (position >= 0) {
                        notificationAdapter.removeItem(position);
                    }
                }
            } else {
                //更新
                int position = notificationAdapter.getPositionById(notificationData.getId());
                if (position >= 0) {
                    NotificationData item = notificationAdapter.getItem(position);
                    item.setLock(notificationData.getLock());
                    notificationAdapter.notifyItemChanged(position);
                }
            }
        } else if (eventMessage.getCode() == EventCodes.REFRESH_NOTIFICATION_DELETE) {
            NotificationData notificationData = (NotificationData) eventMessage.getData();
            if (notificationData == null) return;
            int position = notificationAdapter.getPositionById(notificationData.getId());
            if (position >= 0) {
                notificationAdapter.removeItem(position);
                NotificationDataService.deleteById(notificationData.getId(), null);
                ToastManager.get().noticeShortMessage(R.string.index_item_del_tip);
            }
        }
    }

    private void setAdapterHighLight() {
        if (tabInfo == null || notificationAdapter == null) return;
        if (!TextUtils.isEmpty(tabInfo.getShowKeyWord())) {
            notificationAdapter.setHighlightKeyWordList(tabInfo.getKeywordInfoList());
        } else {
            notificationAdapter.setHighlightKeyWordList(new ArrayList<>());
        }
    }


    private void queryDataList(boolean refresh, boolean allData) {
        AsyncCallback<Page<NotificationData>> callback = new AsyncCallback<Page<NotificationData>>() {
            @Override
            public void onSuccess(Page<NotificationData> result) {
                int size = result.getCount();
                if (refresh) {
                    //如果是刷新
                    if (allData) {
                        notificationAdapter.getDataList().clear();
                        notificationAdapter.addData(result.getDataList(), true);
                        EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_NOT_READ));
                    } else {
                        if (size > 0) {
                            notificationAdapter.getDataList().addAll(0, result.getDataList());
                            notificationAdapter.notifyItemRangeInserted(0, size);
                            if (notificationAdapter.getDataList().size() > 50) {
                                int itemCount = 0;
                                for (int i = 50; i < notificationAdapter.getItemCount(); i++) {
                                    notificationAdapter.getDataList().remove(i);
                                    i--;
                                    itemCount++;
                                }
                                notificationAdapter.notifyItemRangeRemoved(50, itemCount);
                            }
                            EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_NOT_READ));
                        }
                    }
                    binding.refreshLayout.finishRefresh();
                    binding.refreshLayout.setNoMoreData(false);
                } else {
                    //如果是上拉加载更多
                    binding.refreshLayout.finishLoadMore();
                    if (size > 0) {
                        notificationAdapter.addData(result.getDataList());
                        binding.refreshLayout.setNoMoreData(false);
                    } else {
                        binding.refreshLayout.setNoMoreData(true);
                    }
                }

                binding.pageViewResult.changePageState(notificationAdapter.getItemCount() == 0
                        ? PageState.empty() : PageState.content());
            }
        };
        //如果是刷新
        if (refresh && !allData) {
            //下拉刷新还是全部刷新
            int firstId = 0;
            int pageSize = 20;
            if (notificationAdapter.getItemCount() > 0) {
                firstId = notificationAdapter.getItem(0).getId();
            }
            if (-2 == tabInfo.getId()) {
                //如果是全部
                NotificationDataService.findNewAllNotificationList(firstId, pageSize, callback);
            } else if (-1 == tabInfo.getId()) {
                //如果是收藏
                NotificationDataService.findNewLockNotificationList(firstId, pageSize, callback);
            } else {
                //如果是其他的
                NotificationDataService.findNewRuleNotificationList(firstId, pageSize, tabInfo, callback);
            }

        } else {
            //上拉加载更多
            int lastId = Integer.MAX_VALUE;
            int pageSize = 20;
            if (!allData && notificationAdapter.getItemCount() > 0) {
                lastId = notificationAdapter.getItem(notificationAdapter.getItemCount() - 1).getId();
            }
            if (-2 == tabInfo.getId()) {
                //如果是全部
                NotificationDataService.findOldAllNotificationList(lastId, pageSize, callback);
            } else if (-1 == tabInfo.getId()) {
                //如果是收藏
                NotificationDataService.findOldLockNotificationList(lastId, pageSize, callback);
            } else {
                //如果是其他的
                NotificationDataService.findOldRuleNotificationList(lastId, pageSize, tabInfo, callback);
            }
        }
    }
}
