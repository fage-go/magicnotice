package com.zhangshuo.magicnotice.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.page.PageState;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.CacheMemory;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivitySelectNoticeBinding;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.ui.adapter.NotificationAdapter;
import com.zhangshuo.magicnotice.ui.dialog.SelectAppDialog;
import com.zhangshuo.magicnotice.ui.dialog.SelectRuleDialog;

import java.util.ArrayList;
import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_SELECT_NOTICE)
public class SelectNoticeActivity extends BaseActivity<ActivitySelectNoticeBinding> {


    private String keyword;
    private String appName;
    private String packageName;
    private String ruleName;
    private int ruleId = -1;

    private SelectAppDialog selectAppDialog;
    private SelectRuleDialog selectRuleDialog;

    private NotificationAdapter notificationAdapter;
    private int page = 1;

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.no_hit_notice_select));
        buildFilter();
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                doSearch(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                doSearch(true);
            }
        });
        notificationAdapter = new NotificationAdapter(getCtx());
        notificationAdapter.setShowSelectOption(true);
        notificationAdapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent();
                intent.putExtra("noticeId", notificationAdapter.getItem(position).getId());
                setResult(RESULT_OK, intent);
                finishAct();
            }
        });
        binding.rvResult.setLayoutManager(new LinearLayoutManager(getCtx(), RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getCtx(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_drawable));
        binding.rvResult.addItemDecoration(dividerItemDecoration);
        binding.rvResult.setAdapter(notificationAdapter);
    }

    @Override
    protected void initData() {
        doSearch(true);
    }

    private void buildFilter() {
        binding.etFilterKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                doSearch(true);
            }
        });
        binding.tvFilterApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectAppDialog == null) {
                    selectAppDialog = new SelectAppDialog();
                    selectAppDialog.setOnCallback(new SelectAppDialog.OnCallback() {
                        @Override
                        public void onResult(List<String> packageNameList, List<String> nameList) {
                            if (packageNameList.isEmpty() || nameList.isEmpty()) {
                                packageName = "";
                                appName = getText(R.string.search_filter_all_app).toString();
                            } else {
                                packageName = packageNameList.get(0);
                                appName = nameList.get(0);
                            }
                            binding.tvFilterApp.setText(appName);
                            doSearch(true);
                        }
                    });
                }
                List<String> tmpPackageNameList = new ArrayList<>();
                if (!TextUtils.isEmpty(packageName)) {
                    tmpPackageNameList.add(packageName);
                }
                selectAppDialog.setSelectedPackageNameList(tmpPackageNameList);
                selectAppDialog.show(getCtx());
            }
        });

        binding.tvFilterRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectRuleDialog == null) {
                    selectRuleDialog = new SelectRuleDialog();
                    selectRuleDialog.setOnCallback(new SelectRuleDialog.OnCallback() {
                        @Override
                        public void onResult(List<Integer> ruleIdList, List<String> ruleNameList) {
                            if (ruleIdList.isEmpty() || ruleNameList.isEmpty()) {
                                ruleId = -1;
                                ruleName = getText(R.string.search_filter_all_rule).toString();
                            } else {
                                ruleId = ruleIdList.get(0);
                                ruleName = ruleNameList.get(0);
                            }
                            binding.tvFilterRule.setText(ruleName);
                            doSearch(true);
                        }
                    });
                }
                List<Integer> tmpRuleIdList = new ArrayList<>();
                tmpRuleIdList.add(ruleId);
                selectRuleDialog.setSelectedIdList(tmpRuleIdList);
                selectRuleDialog.show(getCtx());
            }
        });
    }

    private void doSearch(boolean refresh) {
        if (refresh) {
            page = 1;
        } else {
            page++;
        }

        NotificationDataService.findNotificationBySearch(keyword, packageName, ruleId, 0, 0, page, new AsyncCallback<List<NotificationData>>() {
            @Override
            public void onSuccess(List<NotificationData> result) {
                int size = result.size();
                LogUtils.i(">>>" + result.size());
                if (refresh) {
                    //如果是刷新
                    notificationAdapter.getDataList().clear();
                    if (size > 0) {
                        notificationAdapter.getDataList().addAll(result);
                    }
                    notificationAdapter.notifyDataSetChanged();
                    binding.refreshLayout.finishRefresh();
                    binding.refreshLayout.setNoMoreData(false);
                } else {
                    //如果是上拉加载更多
                    binding.refreshLayout.finishLoadMore();
                    if (size > 0) {
                        notificationAdapter.addData(result);
                        binding.refreshLayout.setNoMoreData(false);
                    } else {
                        binding.refreshLayout.setNoMoreData(true);
                    }
                }
                binding.pageViewResult.changePageState(notificationAdapter.getItemCount() == 0
                        ? PageState.empty() : PageState.content());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheMemory.cacheData.clear();
    }
}
