package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TxtSelectedInfo;
import com.zhangshuo.common.util.UIUtil;

public class TxtSelectBehaviorAdapter extends BaseRAdapter<TxtSelectedInfo> {
    private OnItemSelectCallback onItemSelectCallback;

    public void setOnItemSelectCallback(OnItemSelectCallback onItemSelectCallback) {
        this.onItemSelectCallback = onItemSelectCallback;
    }

    public TxtSelectBehaviorAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, TxtSelectedInfo txtSelectedInfo, int position) {
        MaterialCardView materialCardView = (MaterialCardView) viewHolder.$(R.id.shock_card_view);
        TextView tvTitle = (TextView) viewHolder.$(R.id.shock_tv_title);
        tvTitle.setText(txtSelectedInfo.getName());
        tvTitle.getPaint().setFakeBoldText(txtSelectedInfo.isSelected());
        materialCardView.setCardElevation(txtSelectedInfo.isSelected() ? UIUtil.dp2Px(4) : 0);
        materialCardView.setStrokeWidth(UIUtil.dp2Px(1));
        materialCardView.setStrokeColor(txtSelectedInfo.isSelected() ? Color.parseColor("#FFFFFF") : Color.parseColor("#F0F0F0"));
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                boolean isSame = false;
                for (int i = 0; i < getItemCount(); i++) {
                    if (getItem(i).isSelected()) {
                        if (i == position) {
                            isSame = true;
                        } else {
                            getItem(i).setSelected(false);
                            notifyItemChanged(i);
                        }
                        break;
                    }
                }
                if (!isSame) {
                    getItem(position).setSelected(true);
                    notifyItemChanged(position);
                }
                if (onItemSelectCallback != null) {
                    onItemSelectCallback.onSelected(position);
                }
            }
        });
    }

    @Override
    protected int onSetItemViewType(TxtSelectedInfo txtSelectedInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_shock_behavior;
    }

    public interface OnItemSelectCallback {
        void onSelected(int position);
    }
}
