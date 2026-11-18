package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.conditions.TxtConditionsModel;

public class TagAdapter extends BaseRAdapter<String> {
    private boolean needJudge = true;

    public TagAdapter(Context context) {
        super(context);
    }

    public void setNeedJudge(boolean needJudge) {
        this.needJudge = needJudge;
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, String s, int position) {
        TextView tvContent = (TextView) viewHolder.$(R.id.item_tag_tv);
        if (needJudge) {
            tvContent.setText(TxtConditionsModel.getJudgeTip(s));
        } else {
            tvContent.setText(s);
        }
    }

    @Override
    protected int onSetItemViewType(String s, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_tag;
    }
}
