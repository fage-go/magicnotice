package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.KeywordInfo;

public class KeyWordInfoAdapter extends BaseRAdapter<KeywordInfo> {

    public KeyWordInfoAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, KeywordInfo keywordInfo, int position) {
        TextView tvContent = (TextView) viewHolder.$(R.id.item_keyword_tv);
        tvContent.setText(keywordInfo.getWord());
        if (TextUtils.isEmpty(keywordInfo.getColor())) {
            tvContent.setTextColor(Color.parseColor("#0081FF"));
        } else {
            tvContent.setTextColor(Color.parseColor(keywordInfo.getColor()));
        }
    }

    @Override
    protected int onSetItemViewType(KeywordInfo keywordInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_keyword_info;
    }
}
