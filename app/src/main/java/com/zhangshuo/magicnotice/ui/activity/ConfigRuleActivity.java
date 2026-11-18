package com.zhangshuo.magicnotice.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.pick.dataset.OptionDataSet;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.CacheMemory;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityConfigRuleBinding;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.ui.adapter.BehaviorAdapter;
import com.zhangshuo.magicnotice.ui.adapter.ConditionsAdapter;
import com.zhangshuo.magicnotice.ui.adapter.OnCallback;
import com.zhangshuo.magicnotice.ui.dialog.ConfigRuleDialog;
import com.zhangshuo.magicnotice.ui.dialog.SelectColorDialog;
import com.zhangshuo.magicnotice.ui.dialog.SelectOptionsDialog;
import com.zhangshuo.magicnotice.ui.model.BehaviorModel;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.OptionModel;
import com.zhangshuo.magicnotice.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_CONFIG_RULE)
public class ConfigRuleActivity extends BaseActivity<ActivityConfigRuleBinding>
        implements ConfigRuleDialog.OnCallback {
    public static ConfigRuleModel tmpConfigRuleMode;

    @Autowired
    int configRuleId;
    @Autowired
    int copyRuleId;
    @Autowired
    String suggestRuleName;
    @Autowired
    String fastRuleName;//快捷创建的

    private ConfigRuleModel configRuleModel;

    private ConditionsAdapter conditionsAdapter;
    private BehaviorAdapter behaviorAdapter;

    private ConfigRuleDialog configRuleDialog;
    private SelectOptionsDialog cleanOptionsDialog;

    private int selectedPosition = -1;
    private String selectTagColor = "";

    @Override
    protected void initView() {
        setTitleBar(configRuleId == 0 ? getString(R.string.config_title_new) : getString(R.string.config_title_edit));
        if (configRuleId > 0)
            setTitleBarOpt(R.mipmap.icon_history, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ruleId", configRuleId);
                    RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_RULE_HIT_LIST, bundle);
                }
            });
        binding.etTitle.getPaint().setFakeBoldText(true);
        conditionsAdapter = new ConditionsAdapter(this);
        binding.rvConditions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        conditionsAdapter.getDataList().add(new ConditionsModel(-1));
        binding.rvConditions.setAdapter(conditionsAdapter);
        conditionsAdapter.setOnCallback(new OnCallback() {
            @Override
            public void onAdd() {
                selectedPosition = -1;
                showConfigRuleDialog(true, null);
            }

            @Override
            public void onItemClick(int position) {
                if (position < conditionsAdapter.getItemCount()) {
                    selectedPosition = position;
                    showConfigRuleDialog(true, conditionsAdapter.getItem(selectedPosition));
                }
            }

        });
        behaviorAdapter = new BehaviorAdapter(this);
        binding.rvBehavior.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        behaviorAdapter.getDataList().add(new BehaviorModel(-1));
        binding.rvBehavior.setAdapter(behaviorAdapter);
        behaviorAdapter.setOnCallback(new OnCallback() {
            @Override
            public void onAdd() {
                selectedPosition = -1;
                showConfigRuleDialog(false, null);
            }

            @Override
            public void onItemClick(int position) {
                if (position < behaviorAdapter.getItemCount()) {
                    selectedPosition = position;
                    showConfigRuleDialog(false, behaviorAdapter.getItem(selectedPosition));
                }
            }
        });
        binding.btnDelete.setVisibility(configRuleId == 0 ? View.GONE : View.VISIBLE);
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
                String ruleName = binding.etTitle.getText().toString();
                if (TextUtils.isEmpty(ruleName) && configRuleModel != null) {
                    ruleName = configRuleModel.getRuleName();
                }
                SpannableString spannableString = new SpannableString(String.format(getString(R.string.delete_search_tip), ruleName));
                int startIndex = spannableString.toString().indexOf(ruleName);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                        startIndex, startIndex + ruleName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                CommonDialog.newInstance()
                        .setContent(spannableString)
                        .setLeftAction(getString(R.string.delete_dialog_cancel))
                        .setCommonAction(getString(R.string.delete_dialog_confirm))
                        .setAutoClose(true)
                        .setCommonActionColor(getColor(R.color.base_font_red))
                        .setCommonActionListener(new CommonDialog.ActionListener() {
                            @Override
                            public void onClick(CommonDialog dialog) {
                                ConfigRuleDataService.deleteConfig(configRuleId, new AsyncCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        EventBusUtil.sendEvent(new EventMessage<>(EventCodes.UPDATE_RULE));
                                        finishAct();
                                    }
                                });
                            }
                        }).show(getCtx());

            }
        });
        binding.clTagColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择颜色
                List<String> colorList = ColorUtil.getColorList();
                colorList.add(0, "");
                SelectColorDialog.create()
                        .setColorList(colorList)
                        .setSelectColor(selectTagColor)
                        .setOnSelectCallback(new SelectColorDialog.OnSelectCallback() {
                            @Override
                            public void onSelect(String color) {
                                selectTagColor = color;
                                binding.ccvTagColor.setColor(color);
                            }
                        })
                        .show(getCtx());
            }
        });
        binding.clClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择清理时间
                showCleanOptions();
            }
        });
        binding.mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ruleName = binding.etTitle.getText().toString();
                if (TextUtils.isEmpty(ruleName)) {
                    ToastManager.get().showShortToast(getString(R.string.config_next_tip_name));
                    binding.etTitle.requestFocus();
                    return;
                }
                int conditionsCount = conditionsAdapter.getItemCount();
                int behaviorCount = behaviorAdapter.getItemCount();
                String confirmMsg = "";
                if (conditionsCount <= 1) {
                    confirmMsg = getString(R.string.config_next_tip_intercept) + "\n";
                }
                if (behaviorCount > 0 && !binding.switchRetain.isChecked()) {
                    boolean haveEffect = false;
                    for (int i = 0; i < behaviorAdapter.getItemCount(); i++) {
                        BehaviorModel item = behaviorAdapter.getItem(i);
                        if (item == null) return;
                        if (item.getType() == BehaviorModel.BehaviorType.BEHAVIOR_BARRAGE
                                || item.getType() == BehaviorModel.BehaviorType.BEHAVIOR_BROADCAST
                                || item.getType() == BehaviorModel.BehaviorType.BEHAVIOR_COPY
                                || item.getType() == BehaviorModel.BehaviorType.BEHAVIOR_FLOAT_BALL) {
                            haveEffect = true;
                        }
                    }
                    if (!haveEffect) {
                        confirmMsg = confirmMsg + getString(R.string.config_next_tip_no_effect);
                    }
                }
                if (TextUtils.isEmpty(confirmMsg)) {
                    doSave(ruleName);
                } else {
                    CommonDialog.newInstance()
                            .setContent(confirmMsg)
                            .setAutoClose(true)
                            .setContentGravity(Gravity.LEFT)
                            .setLeftAction(getString(R.string.config_next_tip_cancel))
                            .setCommonAction(getString(R.string.config_next_tip_confirm))
                            .setCommonActionListener(new CommonDialog.ActionListener() {
                                @Override
                                public void onClick(CommonDialog dialog) {
                                    doSave(ruleName);
                                }
                            }).show(getCtx());
                }
            }
        });
    }

    private void doSave(String ruleName) {
        if (configRuleModel == null) {
            configRuleModel = new ConfigRuleModel();
        }
        configRuleModel.setOpen(true);
        configRuleModel.setTagColor(selectTagColor);
        configRuleModel.setRetain(binding.switchRetain.isChecked());
        configRuleModel.setPolicy(binding.switchPolicy.isChecked());
        configRuleModel.setCollect(binding.switchCollect.isChecked());
        configRuleModel.setRuleName(ruleName);
        configRuleModel.setConditions(conditionsAdapter.getDataList().subList(0, conditionsAdapter.getItemCount() - 1));
        configRuleModel.setBehaviorModel(behaviorAdapter.getDataList().subList(0, behaviorAdapter.getItemCount() - 1));
        ConfigRuleDataService.insertOrUpdate(configRuleModel, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
//                ToastManager.get().noticeShortMessage(getString(R.string.config_next_tip_save_suc));
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.UPDATE_RULE));
                finishAct();
            }
        });
    }

    private void showCleanOptions() {
        if (cleanOptionsDialog == null) {
            cleanOptionsDialog = new SelectOptionsDialog(OptionModel.getModelList());
            cleanOptionsDialog.setOnCallback(new SelectOptionsDialog.OnCallback() {
                @Override
                public void onResult(int index, OptionDataSet optionDataSet) {
                    if (configRuleModel != null) {
                        configRuleModel.setCleanTime(optionDataSet.getValue());
                    }
                    binding.tvCleanTime.setText(optionDataSet.getCharSequence());
                }
            });
        }
        if (configRuleModel != null)
            cleanOptionsDialog.setSelectValue(configRuleModel.getCleanTime());
        cleanOptionsDialog.show(getCtx());

    }

    @Override
    protected void initData() {
        if (configRuleId > 0) {
            ConfigRuleDataService.findById(configRuleId, new AsyncCallback<ConfigRuleModel>() {
                @Override
                public void onSuccess(ConfigRuleModel result) {
                    initConfigData(result);
                }
            });
        } else if (copyRuleId > 0) {
            ConfigRuleDataService.findById(copyRuleId, new AsyncCallback<ConfigRuleModel>() {
                @Override
                public void onSuccess(ConfigRuleModel result) {
                    result.setId(0);
                    result.setRuleName("Copy " + result.getRuleName());
                    initConfigData(result);
                }
            });
        } else if (!TextUtils.isEmpty(fastRuleName)) {
            initConfigData(tmpConfigRuleMode.copyModel());
            tmpConfigRuleMode = null;
        } else {
            this.configRuleModel = new ConfigRuleModel();
        }
    }

    private void initConfigData(ConfigRuleModel model) {
        if (model == null) {
            return;
        }
        this.configRuleModel = model;
        binding.etTitle.setText(configRuleModel.getRuleName());
        List<ConditionsModel> conditionList = configRuleModel.getConditionList();
        conditionsAdapter.getDataList().addAll(0, conditionList);
        conditionsAdapter.notifyDataSetChanged();
        List<BehaviorModel> behaviorList = configRuleModel.getBehaviorList();
        behaviorAdapter.getDataList().addAll(0, behaviorList);
        behaviorAdapter.notifyDataSetChanged();
        binding.switchRetain.setChecked(configRuleModel.isRetain());
        binding.switchPolicy.setChecked(configRuleModel.isPolicy());
        binding.switchCollect.setChecked(configRuleModel.isCollect());
        this.selectTagColor = configRuleModel.getTagColor();
        binding.ccvTagColor.setColor(configRuleModel.getTagColor());
        binding.tvCleanTime.setText(OptionModel.getNameByValue(configRuleModel.getCleanTime()));
    }

    private void showConfigRuleDialog(boolean isConditions, Object result) {
        configRuleDialog = new ConfigRuleDialog();
        configRuleDialog.setOnCallback(this);
        List<Integer> selectTypeList = new ArrayList<>();
        if (isConditions) {
            for (int i = 0; i < conditionsAdapter.getItemCount(); i++) {
                ConditionsModel item = conditionsAdapter.getItem(i);
                selectTypeList.add(item.getType());
            }
        } else {
            for (int i = 0; i < behaviorAdapter.getItemCount(); i++) {
                BehaviorModel item = behaviorAdapter.getItem(i);
                selectTypeList.add(item.getType());
            }
        }
        configRuleDialog.setBaseInfo(isConditions, result, selectTypeList);
        configRuleDialog.show(this);
    }

    @Override
    public void onResult(boolean isConditions, Object result) {
        configRuleDialog.dismiss();
        if (isConditions) {
            if (result instanceof ConditionsModel) {
                ConditionsModel conditionsModel = (ConditionsModel) result;
                if (selectedPosition > -1) {
                    conditionsAdapter.getDataList().set(selectedPosition, conditionsModel);
                    conditionsAdapter.notifyItemChanged(selectedPosition);
                } else {
                    conditionsAdapter.getDataList().add(
                            conditionsAdapter.getItemCount() - 1,
                            conditionsModel);
                    conditionsAdapter.notifyDataSetChanged();
                    binding.scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        } else {
            if (result instanceof BehaviorModel) {
                BehaviorModel behaviorModel = (BehaviorModel) result;
                if (selectedPosition > -1) {
                    behaviorAdapter.getDataList().set(selectedPosition, behaviorModel);
                    behaviorAdapter.notifyItemChanged(selectedPosition);
                } else {
                    behaviorAdapter.getDataList().add(
                            behaviorAdapter.getItemCount() - 1,
                            behaviorModel);
                    behaviorAdapter.notifyDataSetChanged();
                    binding.scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        }
        selectedPosition = -1;
    }

    @Override
    public void onDelete(boolean isConditions) {
        configRuleDialog.dismiss();
        if (isConditions) {
            if (selectedPosition > -1 && selectedPosition < conditionsAdapter.getItemCount()) {
                conditionsAdapter.removeItem(selectedPosition);
            }
        } else {
            if (selectedPosition > -1 && selectedPosition < behaviorAdapter.getItemCount()) {
                behaviorAdapter.removeItem(selectedPosition);
            }
        }
        selectedPosition = -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6678 && resultCode == RESULT_OK) {
            //选择通信录后返回
            Uri contactData = data.getData();
            EventBusUtil.sendEvent(new EventMessage<Uri>(EventCodes.PHONE_SELECTED_RESULT, contactData));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheMemory.cacheData.clear();
    }
}
