package com.zhangshuo.magicnotice.ui.view.configrule.conditions;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCrDirectionBinding;
import com.zhangshuo.magicnotice.ui.model.conditions.DirectionConditionsModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

public class DirectionCRFragment extends BaseFragment<FragmentCrDirectionBinding>
        implements IConfigRule {
    private DirectionConditionsModel directionConditionsModel;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        binding.filterConfig.setConfig(directionConditionsModel.isFilterType(),
                R.string.c_filter_tip_direction_y, R.string.c_filter_tip_direction_n);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof DirectionConditionsModel) {
            directionConditionsModel = (DirectionConditionsModel) object;
        }
        if (directionConditionsModel == null) {
            directionConditionsModel = new DirectionConditionsModel();
        }
    }

    @Override
    public Object onResultData() {
        directionConditionsModel.setFilterType(binding.filterConfig.isChecked());
        return directionConditionsModel;
    }
}
