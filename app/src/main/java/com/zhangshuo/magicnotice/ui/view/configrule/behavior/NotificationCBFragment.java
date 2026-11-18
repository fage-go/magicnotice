package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.magicnotice.databinding.FragmentCbNotificationBinding;
import com.zhangshuo.magicnotice.ui.model.behavior.NotificationBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

public class NotificationCBFragment extends BaseFragment<FragmentCbNotificationBinding>
        implements IConfigRule {

    private NotificationBehaviorModel behaviorModel;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof NotificationBehaviorModel) {
            behaviorModel = (NotificationBehaviorModel) object;
        }
        if (behaviorModel == null) {
            behaviorModel = NotificationBehaviorModel.getDefault();
        }
    }

    @Override
    public Object onResultData() {
        return behaviorModel;
    }
}
