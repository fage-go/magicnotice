package com.zhangshuo.magicnotice.ui.view.configrule;

import com.zhangshuo.common.base.BaseFragment;

public interface IConfigRule {

    BaseFragment getFragment();

    void onInitData(Object object);

    Object onResultData();
}
