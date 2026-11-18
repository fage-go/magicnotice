package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.RepeatCycleModel;

import java.util.ArrayList;
import java.util.List;

public class RepeatCycleTxtAdapter extends BaseRAdapter<RepeatCycleModel> {
    public RepeatCycleTxtAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, RepeatCycleModel repeatCycleModel, int position) {
        TextView tvTxt = (TextView) viewHolder.$(R.id.item_tv_content);
        tvTxt.setText(repeatCycleModel.getName());
        tvTxt.setTextColor(getContext().getColor(repeatCycleModel.isSelected() ? R.color.base_font_blue : R.color.base_font_txt));
        tvTxt.getPaint().setFakeBoldText(repeatCycleModel.isSelected());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatCycleModel.setSelected(!repeatCycleModel.isSelected());
                notifyItemChanged(position);
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
        return R.layout.item_time_week_cycle_txt;
    }
}
