package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ui.model.TestLogInfo;

public class TestLogAdapter extends BaseRAdapter<TestLogInfo> {
    public TestLogAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onConvertView(ViewHolder viewHolder, TestLogInfo testLogInfo, int position) {
        TextView tvRuleName = (TextView) viewHolder.$(R.id.tv_rule_name);
        TextView tvRuleStep = (TextView) viewHolder.$(R.id.tv_rule_step);
        RecyclerView rvItem = (RecyclerView) viewHolder.$(R.id.rv_log_item);
        tvRuleName.setText(testLogInfo.getName());
        if (testLogInfo.getStep() == testLogInfo.getMaxStep()) {
            tvRuleStep.setText(testLogInfo.isResult() ? StringUtil.getString(R.string.test_log_hit_tip) : StringUtil.getString(R.string.test_log_no_hit_tip));
            tvRuleStep.setTextColor(testLogInfo.isResult() ? Color.parseColor("#39B54A") : Color.parseColor("#E54D42"));
        } else {
            tvRuleStep.setText(String.format("(%d/%d)", testLogInfo.getStep(), testLogInfo.getMaxStep()));
            tvRuleStep.setTextColor(Color.parseColor("#aeb1bf"));
        }
        TestLogItemAdapter testLogItemAdapter = new TestLogItemAdapter(getContext());
        testLogItemAdapter.getDataList().addAll(testLogInfo.getLogItemInfoList());
        rvItem.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItem.setAdapter(testLogItemAdapter);
    }

    @Override
    protected int onSetItemViewType(TestLogInfo testLogInfo, int position) {
        return 0;
    }

    @Override
    protected int onBindView(int viewType) {
        return R.layout.item_test_log;
    }
}
