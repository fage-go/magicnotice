package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.AppSelectedInfo;

public class AppSelectedAdapter extends BaseRAdapter<AppSelectedInfo> {
    public AppSelectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, AppSelectedInfo appSelectedInfo, int position) {
        ImageView ivLogo = (ImageView) viewHolder.$(R.id.app_selected_iv_logo);
        TextView tvName = (TextView) viewHolder.$(R.id.app_selected_tv_name);
        AppCompatCheckBox checkBox = (AppCompatCheckBox) viewHolder.$(R.id.app_selected_cv);
        if (appSelectedInfo.getIcon() != null) {
            ivLogo.setBackground(appSelectedInfo.getIcon());
            ivLogo.setVisibility(View.VISIBLE);
        } else {
            ivLogo.setVisibility(View.GONE);
        }
        tvName.setText(appSelectedInfo.getName());
        checkBox.setChecked(appSelectedInfo.isSelected());
    }

    @Override
    protected int onSetItemViewType(AppSelectedInfo appSelectedInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_app_selected;
    }
}
