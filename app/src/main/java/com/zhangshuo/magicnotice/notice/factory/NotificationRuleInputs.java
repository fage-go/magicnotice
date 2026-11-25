package com.zhangshuo.magicnotice.notice.factory;

import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.ConditionsModel;

import java.util.List;

import static com.zhangshuo.magicnotice.notice.factory.RuleEvaluation.Truth.*;
import static com.zhangshuo.magicnotice.ui.model.ConditionsModel.ConditionsType.*;

/** Uses the exact live condition implementations; a replay never substitutes live device state. */
public final class NotificationRuleInputs implements RuleEvaluation.Input<ConfigRuleModel, ConditionsModel> {
    private final NotificationInfo notification;
    private final boolean historical;

    public NotificationRuleInputs(NotificationInfo notification, boolean historical) {
        this.notification = notification;
        this.historical = historical;
    }

    @Override public boolean enabled(ConfigRuleModel rule) { return rule.isOpen(); }
    @Override public boolean respectsDnd(ConfigRuleModel rule) { return rule.isPolicy(); }
    @Override public RuleEvaluation.Truth dnd() {
        return historical ? UNKNOWN : NotificationHelper.get().isZenModePolicy() ? YES : NO;
    }
    @Override public List<ConditionsModel> conditions(ConfigRuleModel rule) { return rule.getConditionList(); }
    @Override public RuleEvaluation.Truth test(ConditionsModel condition) {
        if (historical && (condition.getType() == CONDITIONS_DIRECTION
                || condition.getType() == CONDITIONS_SCREEN_OUT || condition.getType() == CONDITIONS_HEAD_SET)) {
            return UNKNOWN;
        }
        return condition.isFilter(notification) ? YES : NO;
    }
}
