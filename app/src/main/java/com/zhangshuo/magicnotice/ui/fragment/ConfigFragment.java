package com.zhangshuo.magicnotice.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.pay.PayManager;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.FragmentConfigBinding;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.notice.factory.ProcessFactory;
import com.zhangshuo.magicnotice.ui.MainActivity;
import com.zhangshuo.magicnotice.ui.adapter.ConfigRuleAdapter;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.view.AnimationListenerImpl;
import com.zhangshuo.magicnotice.ui.view.ConfigTestTipView;
import com.zhangshuo.magicnotice.ui.view.DefaultItemTouchHelperCallback;
import com.zhangshuo.magicnotice.ui.view.NoHitRuleView;
import com.zhangshuo.magicnotice.widget.WidgetUtil;

import java.util.Collections;
import java.util.List;

@Route(path = RoutePaths.APP_FRAGMENT_CONFIG)
public class ConfigFragment extends BaseFragment<FragmentConfigBinding> {
    private ConfigRuleAdapter adapter;
    //    private SuggestConfigView suggestConfigView;
    private NoHitRuleView noHitRuleView;
    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                refreshConfigRule();
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void initView() {
        binding.tvTitle.getPaint().setFakeBoldText(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getCtx(), RecyclerView.VERTICAL, false));
        adapter = new ConfigRuleAdapter(getCtx());
        binding.recyclerView.setAdapter(adapter);
        //未命中规则
        noHitRuleView = new NoHitRuleView(getContext());
        adapter.addFooterView(noHitRuleView);
        //规则测试
        adapter.addFooterView(new ConfigTestTipView(getContext()));
        //配置意见
//        suggestConfigView = new SuggestConfigView(getContext());
//        adapter.addFooterView(suggestConfigView);

        binding.fabNew.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                if (checkGetProTip()) {
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_RULE);
                }
            }
        });
        binding.tvNewTip.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                if (checkGetProTip()) {
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_RULE);
                }
            }
        });
        adapter.setOnItemOptCallback(new ConfigRuleAdapter.OnItemOptCallback() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("configRuleId", adapter.getItem(position).getId());
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_RULE, bundle);
            }

            @Override
            public void onCheckChanged(int position, boolean isChecked) {
                ConfigRuleModel item = adapter.getItem(position);
                item.setOpen(isChecked);
                ConfigRuleDataService.insertOrUpdate(item, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        refreshConfigRule();
                    }
                });
            }
        });

        new ItemTouchHelper(new DefaultItemTouchHelperCallback(new DefaultItemTouchHelperCallback.OnItemTouchCallbackListener() {
            @Override
            public boolean currentPositionLongPressEnabled(int position) {
                return adapter.getItemViewType(position) != BaseRAdapter.TYPE_HEADER_VIEW &&
                        adapter.getItemViewType(position) != BaseRAdapter.TYPE_FOOTER_VIEW;
            }

            @Override
            public boolean onMove(int fromPosition, int targetPosition) {
                if (fromPosition < targetPosition) {
                    for (int i = fromPosition; i < targetPosition; i++) {
                        Collections.swap(adapter.getDataList(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > targetPosition; i--) {
                        Collections.swap(adapter.getDataList(), i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, targetPosition);
                mainHandler.removeMessages(1000);
                mainHandler.sendEmptyMessageDelayed(1000, 500);
                return true;
            }

        })).attachToRecyclerView(binding.recyclerView);
    }

    @Override
    protected void initData() {
        loadRuleData(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkConfigNewsTip();
//        if (suggestConfigView != null) {
//            suggestConfigView.getData();
//        }
        if (noHitRuleView != null) {
            noHitRuleView.refreshCount();
        }
    }


    private void checkConfigNewsTip() {
        boolean data = LocalDataUtil.get().getData(LocalDataKeys.BKEY_CONFIG_OPT_TIP, false);
        if (data) return;
        binding.tvNewTip.setVisibility(View.VISIBLE);
        binding.tvNewTip.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.config_tip_anim_in));
        binding.tvNewTip.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.config_tip_anim_out);
                animation.setAnimationListener(new AnimationListenerImpl() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        super.onAnimationEnd(animation);
                        binding.tvNewTip.setVisibility(View.GONE);
                    }
                });
                binding.tvNewTip.startAnimation(animation);
            }
        }, 2000);
        LocalDataUtil.get().saveData(LocalDataKeys.BKEY_CONFIG_OPT_TIP, true);
    }

    private void loadRuleData(boolean needRefresh) {
        ConfigRuleDataService.findConfigRuleInfoList(new AsyncCallback<List<ConfigRuleModel>>() {
            @Override
            public void onSuccess(List<ConfigRuleModel> result) {
                adapter.addData(result, true);
                if (needRefresh) {
                    refreshConfigRule();
                }
            }
        });
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.UPDATE_RULE) {
            loadRuleData(true);
        }
    }

    private void refreshConfigRule() {
        StringBuilder configIdBuilder = new StringBuilder();
        for (int i = 0; i < adapter.getDataList().size(); i++) {
            ConfigRuleModel configRuleModel = adapter.getDataList().get(i);
            configIdBuilder.append(configRuleModel.getId())
                    .append(";");
        }
        LocalDataUtil.get().saveData(LocalDataKeys.SKEY_SORT_IDS, configIdBuilder.toString());
        ProcessFactory.get().getMagicProcess().refreshConfigRule();

        WidgetUtil.get().refresh(adapter.getDataList());
    }


    private boolean checkGetProTip() {
        if (adapter.getDataList().size() < 3) return true;
        if (PayManager.get().isPay()) return true;
        CommonDialog.newInstance()
                .setTitle(getString(R.string.set_ui_pro))
                .setContent(getString(R.string.set_ui_pro_tip_rule))
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
