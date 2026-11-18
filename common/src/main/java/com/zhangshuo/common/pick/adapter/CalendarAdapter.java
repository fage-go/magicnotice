package com.zhangshuo.common.pick.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangshuo.common.R;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.pick.model.CalendarDate;

public class CalendarAdapter extends BaseRAdapter<CalendarDate> {
    public CalendarAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, CalendarDate calendarDate, int position) {
        TextView tvDay = (TextView) viewHolder.$(R.id.tv_day);
        TextView tvLunarDay = (TextView) viewHolder.$(R.id.tv_lunar_day);
        ImageView ivPoint = (ImageView) viewHolder.$(R.id.iv_point);
        if (calendarDate.isSelect()) {
            ivPoint.setBackgroundResource(R.drawable.bg_icalendar_item_y);
        } else {
            ivPoint.setBackgroundResource(R.drawable.bg_icalendar_item_n);
        }
        tvDay.setText(calendarDate.getSolar().solarDay + "");
        tvLunarDay.setText(calendarDate.getLunar().lunarDay + "");
        if (calendarDate.isInThisMonth()) {
            tvDay.setTextColor(Color.parseColor("#2E333B"));
            tvLunarDay.setTextColor(Color.parseColor("#aeb1bf"));
        } else {
            tvDay.setTextColor(Color.parseColor("#aeb1bf"));
            tvLunarDay.setTextColor(Color.parseColor("#CBCDD3"));
        }
    }

    @Override
    protected int onSetItemViewType(CalendarDate calendarDate, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_calendar;
    }
}
