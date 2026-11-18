package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TestLogItemInfo;

public class TestLogItemAdapter extends BaseRAdapter<TestLogItemInfo> {
    @Override
    protected void onConvertView(ViewHolder viewHolder, TestLogItemInfo testLogItemInfo, int position) {
        TextView tvConditionsName = (TextView) viewHolder.$(R.id.tv_conditions_name);
        TextView tvConditionsResult = (TextView) viewHolder.$(R.id.tv_conditions_result);
        TextView tvConditionsHint = (TextView) viewHolder.$(R.id.tv_conditions_hint);
        tvConditionsName.setText(testLogItemInfo.getName());
        tvConditionsResult.setText(testLogItemInfo.isResult() ? StringUtil.getString(R.string.test_log_satisfy_tip) : StringUtil.getString(R.string.test_log_no_satisfy_tip));
        tvConditionsResult.setTextColor(testLogItemInfo.isResult() ? Color.parseColor("#39B54A") : Color.parseColor("#E54D42"));
        tvConditionsHint.setText(testLogItemInfo.getMsg());
    }

    @Override
    protected int onSetItemViewType(TestLogItemInfo testLogItemInfo, int position) {
        return 0;
    }

    public TestLogItemAdapter(Context context) {
        super(context);
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_test_log_item;
    }

}
