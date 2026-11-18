package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;

public class ConditionsAdapter extends BaseRAdapter<ConditionsModel> {
    private OnCallback onCallback;

    public ConditionsAdapter(Context context) {
        super(context);
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, ConditionsModel conditionsModel, int position) {
        if (viewHolder.getItemViewType() == -1) {
            ((TextView) viewHolder.$(R.id.item_tv_add)).getPaint().setFakeBoldText(true);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCallback != null) {
                        onCallback.onAdd();
                    }
                }
            });
        } else {
            TextView tvTitle = (TextView) viewHolder.$(R.id.item_tv_title);
            TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
            tvTitle.setText(ConditionsModel.getFilterTip(conditionsModel.getType(), conditionsModel.isFilterType()));
            tvTitle.getPaint().setFakeBoldText(true);
            String filterTip = conditionsModel.getFilterTip(10);
            if (TextUtils.isEmpty(filterTip)) {
                tvContent.setVisibility(View.GONE);
            } else {
                tvContent.setText(filterTip);
                tvContent.setVisibility(View.VISIBLE);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCallback != null) {
                        onCallback.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    protected int onSetItemViewType(ConditionsModel conditionsModel, int position) {
        return conditionsModel.getType();
    }

    @Override
    protected int onBindView(int viewType) {
        switch (viewType) {
            case ConditionsModel.ConditionsType.CONDITIONS_TXT:
            case ConditionsModel.ConditionsType.CONDITIONS_APP:
            case ConditionsModel.ConditionsType.CONDITIONS_TIME:
            case ConditionsModel.ConditionsType.CONDITIONS_DIRECTION:
            case ConditionsModel.ConditionsType.CONDITIONS_SCREEN_OUT:
            case ConditionsModel.ConditionsType.CONDITIONS_HEAD_SET:
            case ConditionsModel.ConditionsType.CONDITIONS_PHONE_CALL:
                return R.layout.item_conditions_common;
        }
        if (viewType == -1)
            return R.layout.item_conditions_add;
        else
            return R.layout.item_line_empty;
    }
}
