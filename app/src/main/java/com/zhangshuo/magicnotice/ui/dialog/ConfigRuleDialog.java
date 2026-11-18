package com.zhangshuo.magicnotice.ui.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.PermissionUtils;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogConfigRuleBinding;
import com.zhangshuo.magicnotice.ui.adapter.ConfigRuleSelectAdapter;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleSelectModel;
import com.zhangshuo.magicnotice.ui.model.behavior.OpenBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.magicnotice.util.OpenPowerCheckUtil;

import java.util.ArrayList;
import java.util.List;

public class ConfigRuleDialog extends BaseBottomDialog<DialogConfigRuleBinding> {
    private boolean isConditions = false;
    private Object oldData;
    private OnCallback onCallback;
    private ConfigRuleSelectAdapter tabAdapter;
    private int selectPosition = 0;
    private List<Integer> haveSettingTypeList = new ArrayList<>();
    private IConfigRule configRule;

    public void setBaseInfo(boolean isConditions, Object oldData, List<Integer> typeList) {
        this.isConditions = isConditions;
        this.oldData = oldData;
        this.haveSettingTypeList = typeList;
    }

    @Override
    public void initView() {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        tabAdapter = new ConfigRuleSelectAdapter(getContext());
        binding.rvSelect.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rvSelect.setAdapter(tabAdapter);
        tabAdapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                select(position);
            }
        });
        binding.btnConfigDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCallback != null) {
                    onCallback.onDelete(isConditions);
                }
            }
        });
        binding.btnConfigNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (configRule != null) {
                    Object result = configRule.onResultData();
                    if (result == null) {
                        return;
                    }
                    if (result instanceof ConditionsModel) {
                        ConditionsModel conditionsModel = (ConditionsModel) result;
                        if (conditionsModel.isEmpty()) {
                            ToastManager.get().noticeShortMessage(getString(R.string.config_rule_not_down));
                            return;
                        }
                    } else if (result instanceof BehaviorModel) {
                        BehaviorModel behaviorModel = (BehaviorModel) result;
                        if (behaviorModel.isEmpty()) {
                            ToastManager.get().noticeShortMessage(getString(R.string.config_rule_not_down));
                            return;
                        }
                        if (behaviorModel instanceof OpenBehaviorModel) {
                            int allowed = OpenPowerCheckUtil.isAllowed(getContext());
                            if (allowed == 1) {
                                ToastManager.get().noticeShortMessage(R.string.config_rule_tip);
                            } else if (allowed == 2) {
                                CommonDialog.newInstance()
                                        .setTitle(StringUtil.getString(R.string.config_rule_tip_title))
                                        .setContentGravity(Gravity.LEFT)
                                        .setContent(StringUtil.getString(R.string.config_rule_tip_content))
                                        .setCommonAction(StringUtil.getString(R.string.config_rule_tip_open))
                                        .setCommonActionListener(new CommonDialog.ActionListener() {
                                            @Override
                                            public void onClick(CommonDialog dialog) {
                                                PermissionUtils.launchAppDetailsSettings();
                                                dismiss();
                                            }
                                        })
                                        .show(getContext());
                            }
                        }
                    }
                    if (onCallback != null) {
                        onCallback.onResult(isConditions, result);
                    }
                }
            }
        });
        displayData();
    }


    private void displayData() {
        binding.btnConfigNext.setText(oldData == null ? getString(R.string.config_rule_tip_add) : getString(R.string.config_rule_tip_edit));
        binding.btnConfigDelete.setVisibility(oldData == null ? View.GONE : View.VISIBLE);

        List<ConfigRuleSelectModel> modelList = new ArrayList<>();
        if (oldData instanceof ConditionsModel) {
            ConditionsModel conditionsModel = (ConditionsModel) oldData;
            IConfigRule conditionsView = ConditionsModel.getConditionsView(conditionsModel.getType());
            if (conditionsView == null) return;
            modelList.add(new ConfigRuleSelectModel(conditionsModel.getType(),
                    ConditionsModel.getConditionsName(conditionsModel.getType()),
                    conditionsView));
        } else if (oldData instanceof BehaviorModel) {
            BehaviorModel behaviorModel = (BehaviorModel) oldData;
            IConfigRule behaviorView = BehaviorModel.getBehaviorView(behaviorModel.getType());
            if (behaviorView == null) return;
            modelList.add(new ConfigRuleSelectModel(behaviorModel.getType(),
                    BehaviorModel.getBehaviorName(behaviorModel.getType()),
                    behaviorView));
        } else {
            if (isConditions) {
                List<Integer> conditionsTypeList = ConditionsModel.getConditionsTypeList();
                for (Integer type : conditionsTypeList) {
                    if (haveSettingTypeList.contains(type)) continue;
                    modelList.add(new ConfigRuleSelectModel(type,
                            ConditionsModel.getConditionsName(type),
                            ConditionsModel.getConditionsView(type)));
                }
            } else {
                List<Integer> behaviorTypeList = BehaviorModel.getBehaviorTypeList();
                for (Integer type : behaviorTypeList) {
                    if (haveSettingTypeList.contains(type)) continue;
                    modelList.add(new ConfigRuleSelectModel(type,
                            BehaviorModel.getBehaviorName(type),
                            BehaviorModel.getBehaviorView(type)));
                }
            }
        }

        if (modelList.isEmpty()) {
            ToastManager.get().noticeShortMessage(isConditions ? getString(R.string.config_rule_have_set_c) : getString(R.string.config_rule_have_set_b));
            dismiss();
            return;
        }
        tabAdapter.addData(modelList, true);
        select(0);
    }


    public void select(int position) {
        selectPosition = position;
        for (int i = 0; i < tabAdapter.getDataList().size(); i++) {
            ConfigRuleSelectModel item = tabAdapter.getItem(i);
            if (item.isSelected()) {
                item.setSelected(false);
                tabAdapter.notifyItemChanged(i);
                break;
            }
        }
        ConfigRuleSelectModel item = tabAdapter.getItem(position);
        item.setSelected(true);
        tabAdapter.notifyItemChanged(position);
        //更改
        configRule = item.getConfigRule();
        configRule.onInitData(oldData);
        BaseFragment fragment = configRule.getFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fl_config_view, fragment)
                .commitNowAllowingStateLoss();
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    public interface OnCallback {
        void onResult(boolean isConditions, Object result);

        void onDelete(boolean isConditions);
    }
}
