package com.zhangshuo.magicnotice.ui.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivitySchedulerRecordBinding;

@Route(path = RoutePaths.APP_ACTIVITY_SCHEDULER_RECORD)
public class SchedulerRecordActivity extends BaseActivity<ActivitySchedulerRecordBinding> {

    @Autowired
    int schedulerId;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
