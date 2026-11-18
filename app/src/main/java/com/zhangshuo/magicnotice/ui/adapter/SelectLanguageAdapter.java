package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TxtSelectedInfo;

public class SelectLanguageAdapter extends BaseRAdapter<TxtSelectedInfo> {

    public SelectLanguageAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, TxtSelectedInfo txtSelectedInfo, int position) {
        TextView tvLauncher = (TextView) viewHolder.$(R.id.tv_language);
        tvLauncher.setText(txtSelectedInfo.getName());
        tvLauncher.setTextColor(txtSelectedInfo.isSelected() ?
                getContext().getColor(R.color.base_font_blue) :
                getContext().getColor(R.color.base_font_txt));
    }

    @Override
    protected int onSetItemViewType(TxtSelectedInfo txtSelectedInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_view_select_language;
    }
}
