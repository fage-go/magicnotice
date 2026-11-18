package com.zhangshuo.magicnotice.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.page.PageState;
import com.zhangshuo.common.pay.PayManager;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.FragmentSchedulerBinding;
import com.zhangshuo.magicnotice.db.service.RemindDataService;
import com.zhangshuo.magicnotice.ui.MainActivity;
import com.zhangshuo.magicnotice.ui.adapter.SchedulerAdapter;
import com.zhangshuo.magicnotice.ui.model.RemindInfo;

import java.util.List;

@Route(path = RoutePaths.APP_FRAGMENT_TIME)
public class SchedulerFragment extends BaseFragment<FragmentSchedulerBinding> {

    private SchedulerAdapter adapter;
    private int page = 1;

    @Override
    protected void initView() {
        binding.tvTitle.getPaint().setFakeBoldText(true);
        binding.rvList.setLayoutManager(new LinearLayoutManager(getCtx()));
        adapter = new SchedulerAdapter(getCtx());
        binding.rvList.setAdapter(adapter);
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                getData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                getData();
            }
        });
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("remindId", adapter.getItem(position).getId());
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_MAKE_SCHEDULER, bundle);
            }
        });
        binding.fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkGetProTip()) {
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_MAKE_SCHEDULER);
                }
            }
        });
    }

    @Override
    protected void initData() {
        getData();
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.REFRESH_SCHEDULER) {
            page = 1;
            getData();
        }
    }

    private void getData() {
        RemindDataService.findRemindList(page, new AsyncCallback<List<RemindInfo>>() {
            @Override
            public void onSuccess(List<RemindInfo> result) {
                if (page == 1) {
                    adapter.getDataList().clear();
                    binding.refreshLayout.finishRefresh();
                    binding.refreshLayout.setNoMoreData(false);
                } else {
                    binding.refreshLayout.finishLoadMore();
                    binding.refreshLayout.setNoMoreData(result.isEmpty());
                }
                adapter.getDataList().addAll(result);
                adapter.notifyDataSetChanged();

                binding.pageViewResult.changePageState(adapter.getItemCount() == 0 ? PageState.empty() : PageState.content());
            }
        });
    }

    private boolean checkGetProTip() {
        if (adapter.getDataList().size() < 1) return true;
        if (PayManager.get().isPay()) return true;
        CommonDialog.newInstance()
                .setTitle(getString(R.string.set_ui_pro))
                .setContent(getString(R.string.set_ui_pro_tip_config))
                .setCommonAction(getString(R.string.set_ui_pro_go))
                .setCommonActionListener(new CommonDialog.ActionListener() {
                    @Override
                    public void onClick(CommonDialog dialog) {
                        FragmentActivity activity = getActivity();
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).toPay();
                        }
                    }
                })
                .show(getContext());
        return false;
    }
}
