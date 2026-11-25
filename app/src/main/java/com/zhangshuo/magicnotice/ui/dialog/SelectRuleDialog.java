package com.zhangshuo.magicnotice.ui.dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.BaseBottomSheetDialog;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogSelectRuleBinding;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.ui.adapter.RuleSelectedAdapter;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.util.SelectedListHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectRuleDialog extends BaseBottomSheetDialog<DialogSelectRuleBinding> {

    private OnCallback onCallback;
    private RuleSelectedAdapter adapter;
    private List<Integer> selectedIdList = new ArrayList<>();
    private boolean isSingle = true;
    private boolean backupSelection;

    public void setBackupSelection() { backupSelection = true; }

    private SelectedListHelper<ConfigRuleModel> selectedListHelper;
    public SelectRuleDialog() {
        this.isSingle = true;
    }

    public SelectRuleDialog(boolean isSingle) {
        this.isSingle = isSingle;
    }

    public void setSelectedIdList(List<Integer> selectedIdList) {
        this.selectedIdList = selectedIdList;
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    public void initView() {
        adapter = new RuleSelectedAdapter(getContext());
        binding.rvList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_line_drawable));
        binding.rvList.addItemDecoration(dividerItemDecoration);
        binding.rvList.setAdapter(adapter);
        selectedListHelper = new SelectedListHelper<>(isSingle, new SelectedListHelper.OnJudgeCallback<ConfigRuleModel>() {
            @Override
            public boolean isSame(ConfigRuleModel data, ConfigRuleModel judgeData) {
                return data.getId() == judgeData.getId();
            }
        });
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    ConfigRuleModel configRuleModel = adapter.getItem(i);
                    if (i == position) {
                        configRuleModel.setSelected(!configRuleModel.isSelected());
                        adapter.notifyItemChanged(i);
                        selectedListHelper.onSelected(configRuleModel, configRuleModel.isSelected());
                    } else if (isSingle) {
                        if (configRuleModel.isSelected()) {
                            configRuleModel.setSelected(false);
                            adapter.notifyItemChanged(i);
                        }
                    }
                }

                selectedIdList.clear();
                for (int i = 0; i < selectedListHelper.getDataList().size(); i++) {
                    ConfigRuleModel configRuleModel = selectedListHelper.getDataList().get(i);
                    if (configRuleModel.isSelected()) {
                        selectedIdList.add(configRuleModel.getId());
                    }
                }
            }
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                getData(keyword);
            }
        });
        binding.tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    ConfigRuleModel item = adapter.getItem(i);
                    if (item.isSelected()) {
                        item.setSelected(false);
                        adapter.notifyItemChanged(i);
                    }
                }
                close();
                if (onCallback != null) {
                    onCallback.onResult(new ArrayList<>(), new ArrayList<>());
                }
            }
        });
        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (onCallback != null) {
                    List<Integer> idList = new ArrayList<>();
                    List<String> nameList = new ArrayList<>();
                    getNameList(idList, nameList);
                    onCallback.onResult(idList, nameList);
                }
            }
        });
        if (backupSelection) binding.tvAll.setVisibility(View.GONE);
        getData(null);
    }


    private void getNameList(List<Integer> idList, List<String> nameList) {
        List<ConfigRuleModel> dataList = selectedListHelper.getDataList();
        for (int j = 0; j < dataList.size(); j++) {
            ConfigRuleModel item = dataList.get(j);
            if (item.isSelected()) {
                idList.add(item.getId());
                nameList.add(item.getRuleName());
            }
        }
    }


    private void getData(String keyword) {
        ConfigRuleDataService.findConfigRuleList(keyword, new AsyncCallback<List<ConfigRuleModel>>() {
            @Override
            public void onSuccess(List<ConfigRuleModel> result) {
                List<ConfigRuleModel> initSelectedList = new ArrayList<>();
                ConfigRuleModel configRuleModel = new ConfigRuleModel();
                configRuleModel.setRuleName(getString(R.string.no_hit_name));
                configRuleModel.setId(0);
                if (!backupSelection) result.add(configRuleModel);
                for (int i = 0; i < result.size(); i++) {
                    for (int targetId : selectedIdList) {
                        if (targetId == result.get(i).getId()) {
                            result.get(i).setSelected(true);
                            initSelectedList.add(result.get(i));
                        }
                    }
                }
                adapter.addData(result, true);
                selectedListHelper.addSelectedList(initSelectedList);
            }
        });
    }


    public interface OnCallback {
        void onResult(List<Integer> ruleIdList, List<String> ruleNameList);
    }
}
