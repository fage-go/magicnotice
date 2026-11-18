package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.PhoneInfo;

public class PhoneSelectAdapter extends BaseRAdapter<PhoneInfo> {
    public PhoneSelectAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, PhoneInfo phoneInfo, int position) {
        TextView tvInfo = (TextView) viewHolder.$(R.id.item_phone_tv);
        tvInfo.setText(phoneInfo.getName() + ":" + phoneInfo.getPhone());
    }

    @Override
    protected int onSetItemViewType(PhoneInfo phoneInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_phone_select;
    }
}
