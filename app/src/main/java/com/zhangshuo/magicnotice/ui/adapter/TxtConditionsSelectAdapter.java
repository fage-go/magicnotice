package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TxtConditionsSelectInfo;

public class TxtConditionsSelectAdapter extends BaseRAdapter<TxtConditionsSelectInfo> {
    public TxtConditionsSelectAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, TxtConditionsSelectInfo txtConditionsSelectInfo, int position) {
        TextView tvContent = (TextView) viewHolder.$(R.id.tv_content);
        tvContent.setText(txtConditionsSelectInfo.getName());
        tvContent.setTextColor(txtConditionsSelectInfo.isSelect() ? Color.parseColor("#0081FF") : Color.WHITE);
    }

    @Override
    protected int onSetItemViewType(TxtConditionsSelectInfo txtConditionsSelectInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_txt_condition_select;
    }
}
