package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.DetailInfo;

public class DetailInfoAdapter extends BaseRAdapter<DetailInfo> {
    private OnCallback onCallback;
    public DetailInfoAdapter(Context context) {
        super(context);
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, DetailInfo detailInfo, int position) {
        TextView tvTitle = (TextView) viewHolder.$(R.id.info_tv_title);
        TextView tvValue = (TextView) viewHolder.$(R.id.info_tv_value);
        tvTitle.setText(detailInfo.getName());
        tvValue.setText(detailInfo.getValue());
        if (TextUtils.isEmpty(detailInfo.getValue())) {
            tvValue.setText(R.string.detail_info_value_none);
            tvValue.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            tvValue.setOnClickListener(null);
        } else {
            tvValue.setText(detailInfo.getValue());
            if (detailInfo.isCanClick()) {
                tvValue.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG | Paint.UNDERLINE_TEXT_FLAG);
                tvValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onCallback!=null){
                            onCallback.onClick(detailInfo);
                        }
                    }
                });
            } else {
                tvValue.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
                tvValue.setOnClickListener(null);
            }
        }

    }

    @Override
    protected int onSetItemViewType(DetailInfo detailInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_detail_info;
    }

    public interface OnCallback {
        void onClick(DetailInfo detailInfo);
    }
}
