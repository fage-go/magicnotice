package com.zhangshuo.magicnotice.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.RoutePaths;

public class ConfigTestTipView extends FrameLayout {

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_config_test_tip, this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_CONFIG_TEST);
            }
        });
    }

    public ConfigTestTipView(@NonNull Context context) {
        super(context);
        init();
    }
}
