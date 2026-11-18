package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.RandomTxtInfo;

public class RandomTxtAdapter extends BaseRAdapter<RandomTxtInfo> {

    private OnCallback onCallback;

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    public RandomTxtAdapter(Context context) {
        super(context);
    }


    @Override
    protected void onConvertView(ViewHolder viewHolder, RandomTxtInfo randomTxtInfo, int position) {
        TextView tvTitle = (TextView) viewHolder.$(R.id.item_tv_title);
        TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
        LinearLayout llDelete = (LinearLayout) viewHolder.$(R.id.item_ll_delete);
        tvTitle.setText(randomTxtInfo.getTitle());
        tvContent.setText(randomTxtInfo.getContent());
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCallback != null) {
                    onCallback.onDelete(randomTxtInfo.hashCode());
                }
            }
        });
    }

    @Override
    protected int onSetItemViewType(RandomTxtInfo randomTxtInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_random_txt;
    }

    public interface OnCallback {
        void onDelete(int hashCode);
    }
}
