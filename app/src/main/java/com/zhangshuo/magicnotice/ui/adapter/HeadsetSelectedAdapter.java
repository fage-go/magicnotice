package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.HeadsetSelectedInfo;

public class HeadsetSelectedAdapter extends BaseRAdapter<HeadsetSelectedInfo> {
    public HeadsetSelectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, HeadsetSelectedInfo headsetSelectedInfo, int position) {
        TextView tvName = (TextView) viewHolder.$(R.id.selected_tv_name);
        AppCompatCheckBox checkBox = (AppCompatCheckBox) viewHolder.$(R.id.selected_cv);
        tvName.setText(headsetSelectedInfo.getName());
        checkBox.setChecked(headsetSelectedInfo.isSelected());
    }

    @Override
    protected int onSetItemViewType(HeadsetSelectedInfo headsetSelectedInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_headset_selected;
    }
}
