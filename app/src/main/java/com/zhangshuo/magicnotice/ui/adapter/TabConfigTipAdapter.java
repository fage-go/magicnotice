package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TabConfigTipInfo;

public class TabConfigTipAdapter extends BaseRAdapter<TabConfigTipInfo> {
    public TabConfigTipAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, TabConfigTipInfo tabConfigTipInfo, int position) {
        TextView tvRuleName = (TextView) viewHolder.$(R.id.tv_rule_name);
        tvRuleName.setText(tabConfigTipInfo.getName());
        if (TextUtils.isEmpty(tabConfigTipInfo.getColor())) {
            tvRuleName.setTextColor(Color.parseColor("#60646B"));
        } else {
            tvRuleName.setTextColor(Color.parseColor(tabConfigTipInfo.getColor()));
        }
    }

    @Override
    protected int onSetItemViewType(TabConfigTipInfo tabConfigTipInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_tab_config_rule;
    }
}
