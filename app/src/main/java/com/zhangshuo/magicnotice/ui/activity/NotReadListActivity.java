package com.zhangshuo.magicnotice.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.page.PageState;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ball.FloatBallHelper;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityNotReadListBinding;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.ui.adapter.NotificationAdapter;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.window.NotReadView;
import com.zhangshuo.magicnotice.window.NotReadWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_NOT_READ_LIST)
public class NotReadListActivity extends BaseActivity<ActivityNotReadListBinding> {

    /**
     * 小窗模式打开
     *
     * @param context
     * @param ruleId
     * @param unReadNum
     */
    public static void openNotReadWindow(Context context, int ruleId, int unReadNum, boolean trySmall) {
        LogUtils.i("openNotReadWindow>>" + ruleId + " > " + unReadNum);
        if (trySmall) {
            String floatRuleId = "RuleView_" + ruleId;
            NotReadView notReadView = new NotReadView(context);
            NotReadWindow notReadWindow = new NotReadWindow.Builder().setView(notReadView).build();
            notReadView.setInfo(ruleId, unReadNum, new NotReadView.OnCallback() {

                @Override
                public void needShowFloatBall(String ruleId, boolean show) {
                    FloatBallHelper.get().showFloatBall(floatRuleId, -1);
                }

                @Override
                public void onClose() {
                    notReadWindow.dismiss();
                    FloatBallHelper.get().dismissFloatBall(floatRuleId);
                }
            });
            notReadWindow.show();
            notReadView.queryDataListByRule();
        } else {
            Intent intent = new Intent(context, NotReadListActivity.class);
            intent.putExtra("ruleId", ruleId);
            intent.putExtra("unReadNum", unReadNum);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    int ruleId;
    int unReadNum;

    private NotificationAdapter notificationAdapter;
    private boolean timeSort = true;//true倒序，false正序

    private List<NotificationData> unReadDataList = new ArrayList<>();

    @Override
    protected void initView() {
        ruleId = getIntent().getIntExtra("ruleId", 0);
        unReadNum = getIntent().getIntExtra("unReadNum", 0);
        initTitle();
        binding.tvRuleName.getPaint().setFakeBoldText(true);
        binding.tvRuleName.setTextSize(16);
        binding.rvNoticeList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        notificationAdapter = new NotificationAdapter(this);
        notificationAdapter.setShowSmallMode(false);
        notificationAdapter.setShowRuleInfo(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getCtx(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_drawable));
        binding.rvNoticeList.addItemDecoration(dividerItemDecoration);
        binding.rvNoticeList.setAdapter(notificationAdapter);
        binding.tvSortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSort = !timeSort;
                binding.ivSortTime.setBackgroundResource(timeSort ? R.mipmap.icon_sort_down : R.mipmap.icon_sort_up);
                showAdapterData();
            }
        });
        LogUtils.i("initView>>>" + ruleId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ruleId = intent.getIntExtra("ruleId", 0);
        unReadNum = intent.getIntExtra("unReadNum", 0);
        getUnReadData();
        LogUtils.i("onNewIntent>>>" + ruleId);
    }


    private void initTitle() {
        showTitle(true);
        setTitleBar(getString(R.string.rule_hit_title_not_read));
        setTitleBarOpt(R.mipmap.icon_history, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("ruleId", ruleId);
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_RULE_HIT_LIST, bundle);
            }
        });
        binding.tvSortTime.setVisibility(View.VISIBLE);
        binding.ivSortTime.setVisibility(View.VISIBLE);
        binding.tvRuleName.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initData() {
        getUnReadData();
        LogUtils.i("initData>>>" + ruleId);
    }


    private void getUnReadData() {
        if (unReadNum > 100) {
            unReadNum = 100;
        }
        if (ruleId != 0) {
            getUnReadDataByRule();
        } else {
            finishAct();
        }
    }

    private void showAdapterData() {
        List<NotificationData> notificationDataList = new ArrayList<>(unReadDataList);
        if (!timeSort) {
            Collections.reverse(notificationDataList);
        }
        notificationAdapter.addData(notificationDataList, true);
        binding.pageViewResult.changePageState(notificationAdapter.getItemCount() == 0
                ? PageState.empty() : PageState.content());
    }

    private void getUnReadDataByRule() {
        ConfigRuleDataService.findById(ruleId, new AsyncCallback<ConfigRuleModel>() {
            @Override
            public void onSuccess(ConfigRuleModel result) {
                binding.tvRuleName.setText(String.format(StringUtil.getString(R.string.rule_hit_tip_list_not_read_small), result.getRuleName()));
                binding.tvRuleName.setTextSize(16);
            }
        });
        queryDataListByRule();
    }

    private void queryDataListByRule() {
        NotificationDataService.findNotificationList(ruleId, 1, unReadNum, new AsyncCallback<List<NotificationData>>() {
            @Override
            public void onSuccess(List<NotificationData> result) {
                unReadDataList.clear();
                unReadDataList.addAll(result);
                showAdapterData();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isFinishing())
            finishAct();
    }

}
