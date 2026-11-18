package com.zhangshuo.magicnotice.ui.activity;

import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.magicnotice.databinding.ActivityDebugBinding;
import com.zhangshuo.magicnotice.ui.model.behavior.LightModel;

public class DebugActivity extends BaseActivity<ActivityDebugBinding> {

    @Override
    protected void initView() {
        binding.lightView.setMock(true);
        binding.lightView.setAutoAnim(true);
        LightModel lightModel = LightModel.getDefault();
        lightModel.setViewType(LightModel.TYPE_SLIDE);
        lightModel.setAnimType(LightModel.ANIM_FILL);
        lightModel.setSpeed(1);
        lightModel.setColorShadow("#FF0000");
        binding.lightView.bindData(lightModel);
    }

    @Override
    protected void initData() {
    }


}
