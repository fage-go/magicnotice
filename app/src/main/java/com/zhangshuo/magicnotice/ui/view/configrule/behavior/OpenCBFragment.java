package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.magicnotice.databinding.FragmentCbOpenBinding;
import com.zhangshuo.magicnotice.ui.model.behavior.OpenBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

public class OpenCBFragment extends BaseFragment<FragmentCbOpenBinding>
        implements IConfigRule {

    private OpenBehaviorModel openBehaviorModel;

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
        if (object instanceof OpenBehaviorModel) {
            openBehaviorModel = (OpenBehaviorModel) object;
        }
        if (openBehaviorModel == null) {
            openBehaviorModel = new OpenBehaviorModel();
        }
    }

    @Override
    public Object onResultData() {
        return openBehaviorModel;
    }
}
