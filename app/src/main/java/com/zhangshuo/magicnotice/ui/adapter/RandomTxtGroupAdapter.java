package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TxtClassifyInfo;

public class RandomTxtGroupAdapter extends BaseRAdapter<TxtClassifyInfo> {
    private OnCallback onCallback;

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    public RandomTxtGroupAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, TxtClassifyInfo txtClassifyInfo, int position) {
        TextView tvTitle = (TextView) viewHolder.$(R.id.tv_group_name);
        TextView tvCount = (TextView) viewHolder.$(R.id.tv_count);
        TextView tvSelect = (TextView) viewHolder.$(R.id.tv_select);
        tvTitle.setText(txtClassifyInfo.getTitle());
        tvCount.setText(String.format(StringUtil.getString(R.string.random_txt_group_count), txtClassifyInfo.getCount()));
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCallback != null) {
                    onCallback.onGoEdit(position);
                }
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCallback != null) {
                    onCallback.onSelect(position);
                }
            }
        });
        tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCallback != null) {
                    onCallback.onOptTxt(position);
                }
            }
        });
    }

    @Override
    protected int onSetItemViewType(TxtClassifyInfo txtClassifyInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_view_random_txt_group;
    }

    public interface OnCallback {
        void onSelect(int position);

        void onGoEdit(int position);

        void onOptTxt(int position);
    }
}
