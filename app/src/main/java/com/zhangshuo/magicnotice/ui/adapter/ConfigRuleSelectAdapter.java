package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleSelectModel;

public class ConfigRuleSelectAdapter extends BaseRAdapter<ConfigRuleSelectModel> {
    public ConfigRuleSelectAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, ConfigRuleSelectModel configRuleSelectModel, int position) {
        TextView textView = (TextView) viewHolder.$(R.id.item_select_tv);
        if (configRuleSelectModel.isSelected()) {
            textView.setTextColor(getContext().getColor(R.color.base_font_title));
            textView.setTextSize(18);
            textView.getPaint().setFakeBoldText(true);
        } else {
            textView.setTextColor(getContext().getColor(R.color.base_font_txt_sub));
            textView.setTextSize(16);
            textView.getPaint().setFakeBoldText(false);
        }
        textView.setText(configRuleSelectModel.getTitle());
    }

    @Override
    protected int onSetItemViewType(ConfigRuleSelectModel configRuleSelectModel, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_config_rule_select;
    }
}
