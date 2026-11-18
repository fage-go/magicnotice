package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.OptionModel;

public class MultiOptionsSelectedAdapter extends BaseRAdapter<OptionModel> {
    public MultiOptionsSelectedAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, OptionModel optionModel, int position) {
        TextView tvName = (TextView) viewHolder.$(R.id.options_selected_tv_name);
        AppCompatCheckBox checkBox = (AppCompatCheckBox) viewHolder.$(R.id.options_selected_cv);
        tvName.setText(optionModel.getCharSequence());
        checkBox.setChecked(optionModel.isSelected());
    }

    @Override
    protected int onSetItemViewType(OptionModel optionModel, int position) {
        return 0;
    }


    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_options_multi;
    }

}
