package com.zhangshuo.magicnotice.ui.dialog;

import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.DialogSelectMulitOptionsBinding;
import com.zhangshuo.magicnotice.ui.adapter.MultiOptionsSelectedAdapter;
import com.zhangshuo.magicnotice.ui.model.OptionModel;
import com.zhangshuo.magicnotice.util.SelectedListHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectMultiOptionsDialog extends BaseBottomDialog<DialogSelectMulitOptionsBinding> {

    private OnCallback onCallback;
    private MultiOptionsSelectedAdapter adapter;
    private List<String> selectedValueList = new ArrayList<>();
    private boolean isSingle = true;
    private List<OptionModel> optionModelList = new ArrayList<>();

    private SelectedListHelper<OptionModel> selectedListHelper;

    public SelectMultiOptionsDialog(List<OptionModel> optionModelList) {
        this.isSingle = true;
        this.optionModelList = optionModelList;
    }

    public SelectMultiOptionsDialog(List<OptionModel> optionModelList, boolean isSingle) {
        this.isSingle = isSingle;
        this.optionModelList = optionModelList;
    }

    public void setSelectedIdList(List<String> selectedIdList) {
        this.selectedValueList = selectedIdList;
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    public void initView() {
        adapter = new MultiOptionsSelectedAdapter(getContext());
        binding.rvList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.item_line_drawable));
        binding.rvList.addItemDecoration(dividerItemDecoration);
        binding.rvList.setAdapter(adapter);
        selectedListHelper = new SelectedListHelper<OptionModel>(isSingle, new SelectedListHelper.OnJudgeCallback<OptionModel>() {
            @Override
            public boolean isSame(OptionModel data, OptionModel judgeData) {
                return data.getValue().equals(judgeData.getValue());
            }
        });
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    OptionModel optionModel = adapter.getItem(i);
                    if (i == position) {
                        optionModel.setSelected(!optionModel.isSelected());
                        adapter.notifyItemChanged(i);
                        selectedListHelper.onSelected(optionModel, optionModel.isSelected());
                    } else if (isSingle) {
                        if (optionModel.isSelected()) {
                            optionModel.setSelected(false);
                            adapter.notifyItemChanged(i);
                        }
                    }
                }

                selectedValueList.clear();
                for (int i = 0; i < selectedListHelper.getDataList().size(); i++) {
                    OptionModel optionModel = selectedListHelper.getDataList().get(i);
                    if (optionModel.isSelected()) {
                        selectedValueList.add(optionModel.getValue());
                    }
                }
            }
        });
        binding.tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    OptionModel item = adapter.getItem(i);
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
                    List<String> valueList = new ArrayList<>();
                    List<String> nameList = new ArrayList<>();
                    getSelectedList(valueList, nameList);
                    onCallback.onResult(valueList, nameList);
                }
            }
        });
        getData();
    }


    private void getSelectedList(List<String> valueList, List<String> nameList) {
        List<OptionModel> dataList = selectedListHelper.getDataList();
        for (int j = 0; j < dataList.size(); j++) {
            OptionModel item = dataList.get(j);
            if (item.isSelected()) {
                valueList.add(item.getValue());
                nameList.add(item.getCharSequence().toString());
            }
        }
    }


    private void getData() {
        List<OptionModel> initSelectedList = new ArrayList<>();
        for (OptionModel optionModel : optionModelList) {
            if (selectedValueList.contains(optionModel.getValue())) {
                optionModel.setSelected(true);
                initSelectedList.add(optionModel);
            }
        }
        adapter.addData(optionModelList, true);
        selectedListHelper.addSelectedList(initSelectedList);
    }


    public interface OnCallback {
        void onResult(List<String> selectedValueList, List<String> selectedNameList);
    }
}
