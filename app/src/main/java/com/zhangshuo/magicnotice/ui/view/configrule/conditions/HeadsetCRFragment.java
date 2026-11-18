package com.zhangshuo.magicnotice.ui.view.configrule.conditions;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.PermissionUtils;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.helper.HeadsetHelper;
import com.zhangshuo.magicnotice.databinding.FragmentCrHeadsetBinding;
import com.zhangshuo.magicnotice.ui.adapter.HeadsetSelectedAdapter;
import com.zhangshuo.magicnotice.ui.model.HeadsetSelectedInfo;
import com.zhangshuo.magicnotice.ui.model.conditions.HeadsetConditionsModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

import java.util.ArrayList;
import java.util.List;

public class HeadsetCRFragment extends BaseFragment<FragmentCrHeadsetBinding>
        implements IConfigRule {

    private HeadsetConditionsModel headsetConditionsModel;
    private HeadsetSelectedAdapter adapter;

    @Override
    protected void initView() {
        binding.rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HeadsetSelectedAdapter(getContext());
        binding.rvList.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                adapter.getDataList().get(position).setSelected(!adapter.getItem(position).isSelected());
                adapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    protected void initData() {
        binding.filterConfig.setConfig(headsetConditionsModel.isFilterType(),
                R.string.c_filter_tip_headset_y, R.string.c_filter_tip_headset_n);
        PermissionUtils.permission("android.permission.BLUETOOTH_CONNECT")
                .callback(new PermissionUtils.SingleCallback() {
                    @Override
                    public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        if (isAllGranted) {
                            getData();
                        }
                    }
                }).request();
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof HeadsetConditionsModel) {
            headsetConditionsModel = (HeadsetConditionsModel) object;
        }
        if (headsetConditionsModel == null) {
            headsetConditionsModel = new HeadsetConditionsModel();
        }
    }

    @Override
    public Object onResultData() {
        headsetConditionsModel.setFilterType(binding.filterConfig.isChecked());
        headsetConditionsModel.getDataList().clear();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            HeadsetSelectedInfo item = adapter.getItem(i);
            if (item.isSelected()) {
                headsetConditionsModel.getDataList().add(item);
            }
        }
        return headsetConditionsModel;
    }

    private void getData() {
        if (adapter == null) return;
        adapter.getDataList().clear();
        adapter.getDataList().add(new HeadsetSelectedInfo(StringUtil.getString(R.string.c_filter_head_set_wired), "WiredHeadSet"));
        adapter.notifyDataSetChanged();
        HeadsetHelper.getBluetoothList(new HeadsetHelper.OnCallback() {
            @Override
            public void onResult(List<BluetoothDevice> deviceList) {
                List<HeadsetSelectedInfo> headsetList = new ArrayList<>();
                for (int i = 0; i < deviceList.size(); i++) {
                    BluetoothDevice bluetoothDevice = deviceList.get(i);
                    HeadsetSelectedInfo headsetSelectedInfo = new HeadsetSelectedInfo(String.format(StringUtil.getString(R.string.c_filter_head_set_bluetooth), bluetoothDevice.getName()),
                            bluetoothDevice.getName());
                    headsetList.add(headsetSelectedInfo);
                }
                for (int i = 0; i < headsetConditionsModel.getDataList().size(); i++) {
                    String tmp = headsetConditionsModel.getDataList().get(i).getValue();
                    for (int j = 0; j < headsetList.size(); j++) {
                        if (tmp.equals(headsetList.get(j).getValue())) {
                            headsetList.get(j).setSelected(true);
                        }
                    }
                }
                adapter.getDataList().addAll(headsetList);
                adapter.notifyDataSetChanged();
            }
        });

    }

}
