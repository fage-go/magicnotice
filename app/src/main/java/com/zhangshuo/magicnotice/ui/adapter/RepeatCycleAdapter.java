package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.RepeatCycleModel;

import java.util.ArrayList;
import java.util.List;

public class RepeatCycleAdapter extends BaseRAdapter<RepeatCycleModel> {
    private OnCallback onCallback;

    public RepeatCycleAdapter(Context context) {
        super(context);
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, RepeatCycleModel repeatCycleModel, int position) {
        TextView tvTxt = (TextView) viewHolder.$(R.id.tv_week_cycle);
        tvTxt.setText(repeatCycleModel.getName());
        tvTxt.setBackgroundResource(repeatCycleModel.isSelected() ? R.drawable.bg_time_week_y : R.drawable.bg_time_week_n);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatCycleModel.setSelected(!repeatCycleModel.isSelected());
                notifyItemChanged(position);
                if (onCallback != null) {
                    onCallback.onSelected(getSelectValues());
                }
            }
        });
    }


    public List<Integer> getSelectValues() {
        List<Integer> repeatCycleModelList = new ArrayList<>();
        for (RepeatCycleModel repeatCycleModel : getDataList()) {
            if (repeatCycleModel.isSelected()) {
                repeatCycleModelList.add(repeatCycleModel.getValue());
            }
        }
        return repeatCycleModelList;
    }

    @Override
    protected int onSetItemViewType(RepeatCycleModel repeatCycleModel, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_time_week_cycle;
    }

    public interface OnCallback {
        void onSelected(List<Integer> dataList);
    }
}
