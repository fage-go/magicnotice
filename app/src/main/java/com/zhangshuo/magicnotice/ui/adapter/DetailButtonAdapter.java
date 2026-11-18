package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.DetailButton;
import com.zhangshuo.common.util.UIUtil;

public class DetailButtonAdapter extends BaseRAdapter<DetailButton> {
    public DetailButtonAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, DetailButton detailButton, int position) {
        TextView tvButton = (TextView) viewHolder.$(R.id.tv_button);
        tvButton.setText(detailButton.getName());
        tvButton.setBackground(getDrawable(detailButton.getBgColor(), UIUtil.dp2Px(4)));
    }

    public Drawable getDrawable(int color, int cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(cornerRadius);//设置4个角的弧度
        drawable.setColor(color);// 设置颜色
        return drawable;
    }

    @Override
    protected int onSetItemViewType(DetailButton detailButton, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_detail_button;
    }
}
