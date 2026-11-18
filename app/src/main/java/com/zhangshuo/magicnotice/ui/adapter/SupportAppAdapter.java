package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;

public class SupportAppAdapter extends BaseRAdapter<String> {
    public SupportAppAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, String s, int position) {
        TextView textView = (TextView) viewHolder.$(R.id.tv_support_app);
        textView.setText(s);
    }

    @Override
    protected int onSetItemViewType(String s, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_support_app;
    }
}
