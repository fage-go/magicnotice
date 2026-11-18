package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TimeRecord;

public class TimeRecordAdapter extends BaseRAdapter<TimeRecord> {

    public TimeRecordAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, TimeRecord timeRecord, int position) {

    }

    @Override
    protected int onSetItemViewType(TimeRecord timeRecord, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_time_record;
    }
}
