package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.db.service.TabDataService;
import com.zhangshuo.magicnotice.notice.factory.ProcessFactory;
import com.zhangshuo.magicnotice.ui.dialog.TabConfigDialog;
import com.zhangshuo.magicnotice.ui.model.TabConfigTipInfo;
import com.zhangshuo.magicnotice.ui.model.TabInfo;

import java.util.ArrayList;
import java.util.List;

public class TabConfigAdapter extends BaseRAdapter<TabInfo> {
    public TabConfigAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, TabInfo tabInfo, int position) {
        TextView tvName = (TextView) viewHolder.$(R.id.item_tv_name);
        Switch switchOpen = (Switch) viewHolder.$(R.id.item_config_switch);
        RecyclerView rvList = (RecyclerView) viewHolder.$(R.id.item_rv_rule);
        rvList.setNestedScrollingEnabled(false);
        rvList.setLayoutManager(new FlexboxLayoutManager(getContext()));
        tvName.setText(tabInfo.getTabName());
        TabConfigTipAdapter tabConfigRuleAdapter = new TabConfigTipAdapter(getContext());
        List<TabConfigTipInfo> tabConfigTipInfoList = new ArrayList<>();
        if (tabInfo.getId() >= 0) {
            tabConfigTipInfoList.add(
                    new TabConfigTipInfo(StringUtil.getString(R.string.tab_config_time) + ": " + TabConfigDialog.getTipDate(tabInfo.getRelationDateOffset()))
            );
            String ruleTip = StringUtil.getString(R.string.tab_config_rule_tip);
            if (!TextUtils.isEmpty(tabInfo.getRelationIds())) {
                ruleTip = tabInfo.getRelationIds().split(",").length + StringUtil.getString(R.string.tab_config_rule_num_tip);
            }
            tabConfigTipInfoList.add(
                    new TabConfigTipInfo(StringUtil.getString(R.string.tab_config_rule) + ": " + ruleTip)
            );
            String appTip = StringUtil.getString(R.string.tab_config_rule_tip);
            if (!TextUtils.isEmpty(tabInfo.getRelationPkgs())) {
                appTip = tabInfo.getRelationPkgs().split(",").length + StringUtil.getString(R.string.tab_config_rule_num_tip);
            }
            tabConfigTipInfoList.add(
                    new TabConfigTipInfo(StringUtil.getString(R.string.tab_config_app) + ": " + appTip)
            );
        }

        if (tabInfo.getKeywordInfoList() != null && tabInfo.getKeywordInfoList().size() > 0) {
            int length = tabInfo.getKeywordInfoList().size();
            String keywordTip = length + StringUtil.getString(R.string.tab_config_rule_num_tip);
            tabConfigTipInfoList.add(
                    new TabConfigTipInfo(StringUtil.getString(R.string.tab_config_keyword) + ": " + keywordTip)
            );
        }
        if (tabConfigTipInfoList.size() > 0) {
            rvList.setVisibility(View.VISIBLE);
            tabConfigRuleAdapter.getDataList().addAll(tabConfigTipInfoList);
            rvList.setAdapter(tabConfigRuleAdapter);
        } else {
            rvList.setVisibility(View.GONE);
        }
        tabConfigRuleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int pt) {
                if (TabConfigAdapter.this.getOnItemClickListener() != null) {
                    (TabConfigAdapter.this).getOnItemClickListener().onClick(position);
                }
            }
        });

        switchOpen.setOnCheckedChangeListener(null);
        switchOpen.setVisibility(View.VISIBLE);
        switchOpen.setChecked(tabInfo.getOpenFlag() == 1);
        switchOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tabInfo.getOpenFlag() == 1) {
                    tabInfo.setOpenFlag(0);
                } else if (tabInfo.getOpenFlag() == 0) {
                    tabInfo.setOpenFlag(1);
                }
                if (tabInfo.getId() > 0) {
                    TabDataService.insertOrUpdate(tabInfo, null);
                } else {
                    saveLocalOpenConfig();
                }
                //状态变更的时候刷新
                ProcessFactory.get().getMagicProcess().refreshTabData();
            }
        });
    }

    private void saveLocalOpenConfig() {
        StringBuilder tabOpenFlag = new StringBuilder();
        for (TabInfo tabInfo : getDataList()) {
            if (tabInfo.getId() < 0 && tabInfo.getOpenFlag() == 1) {
                tabOpenFlag.append(tabInfo.getId()).append(";");
            }
        }
        LocalDataUtil.get().saveData(LocalDataKeys.SKEY_TAB_OPEN_FLAG, tabOpenFlag.toString());
    }

    @Override
    protected int onSetItemViewType(TabInfo tabInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_tab_config;
    }
}
