package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

public class RuleSelectedAdapter extends BaseRAdapter<ConfigRuleModel> {
    public RuleSelectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, ConfigRuleModel model, int position) {
        TextView tvName = (TextView) viewHolder.$(R.id.rule_selected_tv_name);
        AppCompatCheckBox checkBox = (AppCompatCheckBox) viewHolder.$(R.id.rule_selected_cv);
        tvName.setText(model.getRuleName());
        checkBox.setChecked(model.isSelected());
    }

    @Override
    protected int onSetItemViewType(ConfigRuleModel model, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_app_rules;
    }
}
