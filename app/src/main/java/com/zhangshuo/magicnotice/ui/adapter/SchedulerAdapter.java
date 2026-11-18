package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.scheduler.NextDateResult;
import com.zhangshuo.magicnotice.scheduler.SchedulerUtil;
import com.zhangshuo.magicnotice.ui.model.RemindInfo;
import com.zhangshuo.magicnotice.util.TimeUtil;

public class SchedulerAdapter extends BaseRAdapter<RemindInfo> {
    public SchedulerAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, RemindInfo remindInfo, int position) {
        TextView tvNextTime = (TextView) viewHolder.$(R.id.item_tv_next_date);
        TextView tvTitle = (TextView) viewHolder.$(R.id.item_tv_title);
        TextView tvContent = (TextView) viewHolder.$(R.id.item_tv_content);
        TextView tvTime = (TextView) viewHolder.$(R.id.item_tv_date);
        TextView tvRepeat = (TextView) viewHolder.$(R.id.item_tv_repeat);
        LinearLayout llCount = (LinearLayout) viewHolder.$(R.id.item_ll_count_bg);
        ImageView ivType = (ImageView) viewHolder.$(R.id.item_iv_type);
        TextView tvCount = (TextView) viewHolder.$(R.id.item_tv_count);

        boolean isRandom = remindInfo.getRemindType() == RemindInfo.TYPE_REMIND;
        if (isRandom) {
            tvTitle.setText(remindInfo.getTxtClassifyInfo().getTitle());
            tvContent.setText(String.format(StringUtil.getString(R.string.scheduler_txt_count), remindInfo.getTxtClassifyInfo().getCount()));
        } else {
            tvTitle.setText(remindInfo.getTitle());
            tvContent.setText(remindInfo.getContent());
        }
        llCount.setBackgroundResource(isRandom ? R.drawable.bg_scheduler_random_count : R.drawable.bg_scheduler_count);
        ivType.setBackgroundResource(isRandom ? R.mipmap.icon_scheduler_random : R.mipmap.icon_scheduler);
        int currentCount = remindInfo.getNotifyCount();
        tvCount.getPaint().setFakeBoldText(true);
        tvCount.setTextColor(isRandom ? getContext().getColor(R.color.base_color_blue) : getContext().getColor(R.color.base_color_yellow));
        tvTime.setText(remindInfo.getDateStrTip());
        tvRepeat.setText(remindInfo.getRepeatTip());

        NextDateResult nextRemindTime = SchedulerUtil.getNextRemindTime(remindInfo.getRemindTimeInfo(), remindInfo.getNotifyNumber());
        if (nextRemindTime == null || nextRemindTime.getNextTime() == 0) {
            tvNextTime.setVisibility(View.GONE);
            tvCount.setText("DONE");
        } else {
            tvCount.setText(String.format(StringUtil.getString(R.string.scheduler_txt_count_tip), currentCount));
            tvNextTime.setVisibility(View.VISIBLE);
            tvNextTime.setText(TimeUtil.dateFormatTime(nextRemindTime.getNextTime()));
        }

    }


    @Override
    protected int onSetItemViewType(RemindInfo remindInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_scheduler;
    }
}
