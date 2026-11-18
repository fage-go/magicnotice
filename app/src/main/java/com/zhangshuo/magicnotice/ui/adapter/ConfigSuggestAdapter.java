package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.db.model.ConfigRuleData;

public class ConfigSuggestAdapter extends BaseRAdapter<ConfigRuleData> {
    public ConfigSuggestAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, ConfigRuleData configRuleData, int position) {
        TextView tvItem = (TextView) viewHolder.$(R.id.tv_item);
        tvItem.setText(configRuleData.getName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("suggestRuleName",configRuleData.getName());
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_RULE, bundle);
            }
        });
    }

    @Override
    protected int onSetItemViewType(ConfigRuleData configRuleData, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_view_config_suggest;
    }
}
