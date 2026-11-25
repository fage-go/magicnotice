package com.zhangshuo.magicnotice.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.notice.factory.RuleEvaluation;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestLogAdapter extends BaseRAdapter<RuleEvaluation.Row<ConfigRuleModel, ConditionsModel>> {
    private final Set<Integer> expanded = new HashSet<>();
    private int selectedRuleId;

    public TestLogAdapter(Context context) { super(context); }

    public void showResult(List<RuleEvaluation.Row<ConfigRuleModel, ConditionsModel>> rows, int selectedRuleId) {
        this.selectedRuleId = selectedRuleId;
        expanded.clear();
        getDataList().clear();
        getDataList().addAll(rows);
        notifyDataSetChanged();
    }

    @Override
    protected void onConvertView(ViewHolder vh, RuleEvaluation.Row<ConfigRuleModel, ConditionsModel> row, int position) {
        TextView name = (TextView) vh.$(R.id.tv_rule_name);
        TextView status = (TextView) vh.$(R.id.tv_rule_step);
        TextView reason = (TextView) vh.$(R.id.tv_rule_reason);
        TextView detail = (TextView) vh.$(R.id.tv_rule_detail);
        TextView toggle = (TextView) vh.$(R.id.tv_rule_toggle);
        boolean selected = row.rule.getId() == selectedRuleId;
        name.setText(getContext().getString(selected ? R.string.rule_test_row_selected : R.string.rule_test_row,
                position + 1, row.rule.getRuleName()));
        name.setTextColor(getContext().getColor(selected ? R.color.base_font_blue : R.color.base_font_title));
        status.setText(statusLabel(row.status));
        status.setTextColor(row.status == RuleEvaluation.Status.MATCH ? Color.parseColor("#21834B")
                : row.status == RuleEvaluation.Status.NO_MATCH ? Color.parseColor("#C5463B")
                : row.status == RuleEvaluation.Status.POSSIBLE || row.status == RuleEvaluation.Status.CONDITIONAL_MATCH
                ? Color.parseColor("#945710") : getContext().getColor(R.color.base_font_txt_sub));
        reason.setText(reason(row));
        boolean open = expanded.contains(position);
        detail.setVisibility(open ? View.VISIBLE : View.GONE);
        toggle.setVisibility(row.details.isEmpty() ? View.GONE : View.VISIBLE);
        toggle.setText(open ? R.string.rule_test_collapse : R.string.rule_test_expand);
        if (open) {
            StringBuilder lines = new StringBuilder();
            for (RuleEvaluation.Detail<ConditionsModel> item : row.details) {
                if (lines.length() != 0) lines.append('\n');
                String title = ConditionsModel.getFilterTip(item.condition.getType(), item.condition.isFilterType());
                String description = item.condition.getFilterTip(Integer.MAX_VALUE);
                lines.append(getContext().getString(R.string.rule_test_condition_line, title,
                        getContext().getString(item.value == RuleEvaluation.Truth.YES ? R.string.test_log_satisfy_tip
                                : item.value == RuleEvaluation.Truth.NO ? R.string.test_log_no_satisfy_tip : R.string.rule_test_unknown)));
                if (description != null && !description.isEmpty()) lines.append('\n').append(description);
            }
            detail.setText(lines);
        }
        toggle.setOnClickListener(v -> {
            if (!expanded.add(position)) expanded.remove(position);
            notifyItemChanged(position);
        });
    }

    private CharSequence reason(RuleEvaluation.Row<ConfigRuleModel, ConditionsModel> row) {
        if (row.status == RuleEvaluation.Status.NO_MATCH && !row.details.isEmpty()) {
            ConditionsModel failed = row.details.get(row.details.size() - 1).condition;
            return getContext().getString(R.string.rule_test_failed_reason,
                    ConditionsModel.getConditionsName(failed.getType()));
        }
        switch (row.status) {
            case DISABLED: return getContext().getString(R.string.rule_test_disabled_reason);
            case DND: return getContext().getString(R.string.rule_test_dnd_reason);
            case POSSIBLE: return getContext().getString(row.rule.isPolicy()
                    ? R.string.rule_test_possible_dnd : R.string.rule_test_possible_reason);
            case CONDITIONAL_MATCH: return getContext().getString(R.string.rule_test_conditional_reason);
            case MATCH: return getContext().getString(R.string.rule_test_match_reason);
            case NOT_CHECKED: return getContext().getString(R.string.rule_test_unchecked_reason);
            default: return getContext().getString(R.string.test_log_no_hit_tip);
        }
    }

    private int statusLabel(RuleEvaluation.Status status) {
        switch (status) {
            case DISABLED: return R.string.rule_test_disabled;
            case DND: return R.string.rule_test_dnd;
            case POSSIBLE: return R.string.rule_test_possible;
            case CONDITIONAL_MATCH: return R.string.rule_test_conditional;
            case MATCH: return R.string.test_log_hit_tip;
            case NO_MATCH: return R.string.test_log_no_hit_tip;
            default: return R.string.rule_test_unchecked;
        }
    }

    @Override protected int onSetItemViewType(RuleEvaluation.Row<ConfigRuleModel, ConditionsModel> row, int position) { return 0; }
    @Override protected int onBindView(int viewType) { return R.layout.item_test_log; }
}
