package com.zhangshuo.magicnotice.ui.view.configrule.conditions;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCrScreenoutBinding;
import com.zhangshuo.magicnotice.ui.model.conditions.ScreenOutConditionsModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

public class ScreenOutCRFragment extends BaseFragment<FragmentCrScreenoutBinding>
        implements IConfigRule {

    private ScreenOutConditionsModel screenOutConditionsModel;


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        binding.filterConfig.setConfig(screenOutConditionsModel.isFilterType(),
                R.string.c_filter_tip_screen_y, R.string.c_filter_tip_screen_n);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof ScreenOutConditionsModel) {
            screenOutConditionsModel = (ScreenOutConditionsModel) object;
        }
        if (screenOutConditionsModel == null) {
            screenOutConditionsModel = new ScreenOutConditionsModel();
        }
    }

    @Override
    public Object onResultData() {
        screenOutConditionsModel.setFilterType(binding.filterConfig.isChecked());
        return screenOutConditionsModel;
    }
}
