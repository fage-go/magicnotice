package com.zhangshuo.magicnotice.ui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.FragmentIndexBinding;
import com.zhangshuo.magicnotice.db.model.CountInfo;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.db.service.TabDataService;
import com.zhangshuo.magicnotice.ui.dialog.DeleteNoticeDialog;
import com.zhangshuo.magicnotice.ui.model.TabInfo;

import java.util.ArrayList;
import java.util.List;

@Route(path = RoutePaths.APP_FRAGMENT_INDEX)
public class IndexFragment extends BaseFragment<FragmentIndexBinding> {

    private List<TabInfo> tabInfoList = new ArrayList<>();
    private boolean needResetTab = false;
    private FragmentStateAdapter adapter;

    @Override
    protected void initView() {
        binding.tvTitle.getPaint().setFakeBoldText(true);
        binding.flCleanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteNoticeDialog().show(getCtx());
            }
        });
        binding.flSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_SEARCH);
            }
        });
        binding.llTabSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needResetTab = true;
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_TAB_CONFIG);
            }
        });
        adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return IndexItemFragment.buildIndexItemFragment(tabInfoList.get(position));
            }

            @Override
            public int getItemCount() {
                return tabInfoList.size();
            }

            @Override
            public long getItemId(int position) {
                return tabInfoList.get(position).getId();
            }

            @Override
            public boolean containsItem(long itemId) {
                return false;
            }
        };
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.tabLayout.setOnTabDoubleTapListener(this::scrollTabToTop);
        binding.tabLayout.bindViewPager2(binding.viewPager, tabInfoList);
    }

    private void scrollTabToTop(int position) {
        TabInfo tabInfo = getTabInfoByPosition(position);
        if (tabInfo == null) return;
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof IndexItemFragment) {
                IndexItemFragment itemFragment = (IndexItemFragment) fragment;
                if (itemFragment.isForTab(tabInfo.getId())) {
                    itemFragment.scrollToTop();
                    return;
                }
            }
        }
    }

    @Override
    protected void initData() {
        getTabList();
        refreshNoticeCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needResetTab) {
            getTabList();
            needResetTab = false;
        }
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.RUNNING_STATUS) {
            boolean runningStatus = (boolean) eventMessage.getData();
            changeRunningStatus(runningStatus);
        } else if (eventMessage.getCode() == EventCodes.INSERT_NEW_NOTIFICATION) {
            refreshNoticeCount();
            refreshNotReadCount();
        } else if (eventMessage.getCode() == EventCodes.REFRESH_NOT_READ) {
            refreshNotReadCount();
        } else if (eventMessage.getCode() == EventCodes.REFRESH_TAB_CONFIG) {
            getTabList();
            needResetTab = false;
        }
    }

    private void getTabList() {
        TabDataService.findActTab(new AsyncCallback<List<TabInfo>>() {
            @Override
            public void onSuccess(List<TabInfo> result) {
                setTabData(result);
            }
        });
    }

    public TabInfo getTabInfoByPosition(int position) {
        if (position < 0 || position >= tabInfoList.size()) return null;
        return tabInfoList.get(position);
    }

    private void setTabData(List<TabInfo> resultList) {
        tabInfoList.clear();
        tabInfoList.addAll(resultList);
        try {
            binding.viewPager.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager supportFragmentManager = getChildFragmentManager();
        List<Fragment> fragments = supportFragmentManager.getFragments();
        for (TabInfo tabInfo : tabInfoList) {
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment instanceof IndexItemFragment) {
                    ((IndexItemFragment) fragment).checkReset(tabInfo);
                }
            }
        }
    }


    private void refreshNoticeCount() {
        NotificationDataService.queryNotificationCount(new AsyncCallback<CountInfo>() {
            @Override
            public void onSuccess(CountInfo result) {
                if (binding != null) {
                    binding.tvNoticeCount.setText(String.format(getString(R.string.index_tip_count),
                            result.getNotificationCountStr(), String.valueOf(result.getAppCount())));
                }
            }
        });
    }

    private void refreshNotReadCount() {
        if (tabInfoList == null) return;
        NotificationDataService.queryNotReadCount(tabInfoList, new AsyncCallback<List<TabInfo>>() {
            @Override
            public void onSuccess(List<TabInfo> result) {
                if (binding != null) {
                    for (int i = 0; i < result.size(); i++) {
                        TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
                        if (tab == null || tab.getCustomView() == null) continue;
                        TabInfo tabInfo = result.get(i);
                        int notReadCount = tabInfo.getNotReadCount();
                        TextView tvPoint = tab.getCustomView().findViewById(R.id.tab_tv_point);
                        tvPoint.setVisibility(notReadCount > 0 ? View.VISIBLE : View.GONE);
                        tvPoint.setText(String.valueOf(notReadCount));
                    }
                }

            }
        });
    }

    private void changeRunningStatus(boolean runningStatus) {
        binding.ivRunStatus.setBackgroundResource(runningStatus ? R.drawable.bg_status_running : R.drawable.bg_status_stop);
    }

}
