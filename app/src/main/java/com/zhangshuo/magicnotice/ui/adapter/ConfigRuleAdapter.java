package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.dialog.ConfigMorePopup;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

public class ConfigRuleAdapter extends BaseRAdapter<ConfigRuleModel> {
    private OnItemOptCallback onItemOptCallback;

    public ConfigRuleAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, ConfigRuleModel configRuleModel, int position) {
        View vTag = (View) viewHolder.$(R.id.v_tag_color);
        TextView tvTitle = (TextView) viewHolder.$(R.id.item_config_tv_title);
        Switch switchOpen = (Switch) viewHolder.$(R.id.item_config_switch);
        TextView tvTip = (TextView) viewHolder.$(R.id.item_tv_rule);
        LinearLayout llMore = (LinearLayout) viewHolder.$(R.id.item_ll_more);
        ImageView ivMore = (ImageView) viewHolder.$(R.id.item_iv_more);

        if (TextUtils.isEmpty(configRuleModel.getTagColor())) {
            vTag.setVisibility(View.GONE);
        } else {
            try {
                vTag.setVisibility(View.VISIBLE);
                ColorStateList colors = ColorStateList.valueOf(Color.parseColor(configRuleModel.getTagColor()));
                Drawable wrappedDrawable = DrawableCompat.wrap(vTag.getBackground());
                DrawableCompat.setTintList(wrappedDrawable, colors);
                vTag.setBackground(wrappedDrawable);
            } catch (Exception e) {
                vTag.setVisibility(View.GONE);
            }

        }

        tvTitle.setText(configRuleModel.getRuleName());
        switchOpen.setOnCheckedChangeListener(null);
        switchOpen.setChecked(configRuleModel.isOpen());
        switchOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onItemOptCallback != null) {
                    onItemOptCallback.onCheckChanged(position, isChecked);
                }
            }
        });
        if (configRuleModel.getFilterCount() > 0) {
            tvTip.setText(String.format(StringUtil.getString(R.string.config_rule_filter_have), configRuleModel.getFilterCount()));
        } else {
            tvTip.setText(R.string.config_rule_filter_no);
        }
        llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMorePopup configMorePopup = new ConfigMorePopup(getContext());
                configMorePopup.setPopupGravity(Gravity.LEFT | Gravity.BOTTOM);
                configMorePopup.showByConfigRuleModel(ivMore, getItem(position));
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemOptCallback != null) {
                    onItemOptCallback.onClick(position);
                }
            }
        });
    }

    @Override
    protected int onSetItemViewType(ConfigRuleModel configRuleModel, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_view_config_rule;
    }

    public void setOnItemOptCallback(OnItemOptCallback onItemOptCallback) {
        this.onItemOptCallback = onItemOptCallback;
    }

    public interface OnItemOptCallback {
        void onClick(int position);

        void onCheckChanged(int position, boolean isChecked);
    }
}
