package com.zhangshuo.magicnotice.ui.activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.page.PageState;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityRuleHitListBinding;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.ui.adapter.NotificationAdapter;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_RULE_HIT_LIST)
public class RuleHitListActivity extends BaseActivity<ActivityRuleHitListBinding> {

    @Autowired
    int ruleId = -1;
    @Autowired
    int backFlag;
    private ConfigRuleModel configRuleModel;
    private NotificationAdapter notificationAdapter;
    private int page = 1;

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.rule_hit_title));
        binding.tvRuleName.getPaint().setFakeBoldText(true);
        binding.rvNoticeList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        notificationAdapter = new NotificationAdapter(this);
        notificationAdapter.setShowRuleInfo(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getCtx(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_drawable));
        binding.rvNoticeList.addItemDecoration(dividerItemDecoration);
        binding.rvNoticeList.setAdapter(notificationAdapter);
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                getRuleData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                getRuleData();
            }
        });
    }

    @Override
    protected void initData() {
        getRuleData();
    }

    private void getRuleData() {
        if (ruleId == 0) {
            binding.tvRuleName.setText(String.format(StringUtil.getString(R.string.rule_no_hit_tip_list)));
        } else {
            ConfigRuleDataService.findById(ruleId, new AsyncCallback<ConfigRuleModel>() {
                @Override
                public void onSuccess(ConfigRuleModel result) {
                    configRuleModel = result;
                    binding.tvRuleName.setText(String.format(StringUtil.getString(R.string.rule_hit_tip_list), result.getRuleName()));
                }
            });
        }
        NotificationDataService.findNotificationList(ruleId, page, 20, new AsyncCallback<List<NotificationData>>() {
            @Override
            public void onSuccess(List<NotificationData> result) {
                if (page == 1) {
                    notificationAdapter.addData(result, true);
                    binding.refreshLayout.finishRefresh();
                    binding.refreshLayout.setNoMoreData(false);
                } else {
                    notificationAdapter.addData(result);
                    binding.refreshLayout.finishLoadMore();
                    if (StringUtil.isEmpty(result)) {
                        binding.refreshLayout.setNoMoreData(true);
                    }
                }
                binding.pageViewResult.changePageState(notificationAdapter.getItemCount() == 0
                        ? PageState.empty() : PageState.content());
            }
        });
    }

    @Override
    public void onCloseClick() {
        if (backFlag == 1) {
            backToDesktop();
        } else {
            super.onCloseClick();
        }
    }

    @Override
    public void onBackPressed() {
        if (backFlag == 1) {
            backToDesktop();
        } else {
            super.onBackPressed();
        }
    }

    private void backToDesktop() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);//退到后台而不结束
            finishAct();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
