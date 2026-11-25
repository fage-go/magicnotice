package com.zhangshuo.magicnotice.notice.factory;

import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;

import java.util.List;

import static com.zhangshuo.magicnotice.notice.factory.RuleEvaluation.Truth.*;

/** Uses the same live condition implementations and current device state for delivery and tests. */
public final class NotificationRuleInputs implements RuleEvaluation.Input<ConfigRuleModel, ConditionsModel> {
    private final NotificationInfo notification;

    public NotificationRuleInputs(NotificationInfo notification) {
        this.notification = notification;
    }

    @Override public boolean enabled(ConfigRuleModel rule) { return rule.isOpen(); }
    @Override public boolean respectsDnd(ConfigRuleModel rule) { return rule.isPolicy(); }
    @Override public RuleEvaluation.Truth dnd() {
        return NotificationHelper.get().isZenModePolicy() ? YES : NO;
    }
    @Override public List<ConditionsModel> conditions(ConfigRuleModel rule) { return rule.getConditionList(); }
    @Override public RuleEvaluation.Truth test(ConditionsModel condition) {
        return condition.isFilter(notification) ? YES : NO;
    }
}
