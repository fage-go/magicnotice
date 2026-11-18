package com.zhangshuo.magicnotice.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.MainHandlerUtil;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivitySplashBinding;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.ui.dialog.PrivacyDialog;
import com.zhangshuo.magicnotice.util.ConfigInit;

@Route(path = RoutePaths.APP_ACTIVITY_SPLASH)
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationHelper.reEnableSetting(this);
    }

    @Override
    protected void initView() {
        boolean isPrivacy = LocalDataUtil.get().getData(LocalDataUtil.BKEY_PRIVATE, false);

        if (isPrivacy || !ConfigInit.isBuildTypeZH()) {
            //不是新用户
            afterAllowPrivacy();
        } else {
            //如果是新用户
            PrivacyDialog privacyDialog = new PrivacyDialog();
            privacyDialog.setOnPrivacyCallback(new PrivacyDialog.OnPrivacyCallback() {
                @Override
                public void onOpt(boolean agree) {
                    if (agree) {
                        LocalDataUtil.get().saveData(LocalDataUtil.BKEY_PRIVATE, true);
                        afterAllowPrivacy();
                    } else {
                        finish();
                    }
                }
            });
            privacyDialog.show(getCtx());
        }
        onChangeHideRecent(!isPrivacy);
    }

    @Override
    protected void initData() {

    }

    private void afterAllowPrivacy() {
        AppUtil.get().afterAllowPrivacy();
        MainHandlerUtil.get().post(new Runnable() {
            @Override
            public void run() {
                gotoMain();
            }
        }, 500);
    }

    private void onChangeHideRecent(boolean newApp) {
        boolean defaultData = LocalDataUtil.get().getData(LocalDataKeys.BKEY_HIDE_RECENT, true);
        if (newApp) {
            LocalDataUtil.get().saveData(LocalDataKeys.BKEY_HIDE_RECENT, false);
        } else {
            if (defaultData) {
                LocalDataUtil.get().saveData(LocalDataKeys.BKEY_HIDE_RECENT, true);
            }
        }
    }

    private void gotoMain() {
        //默认为关闭状态
        boolean data = LocalDataUtil.get().getData(LocalDataKeys.BKEY_HIDE_RECENT, false);
//        Intent intent = new Intent(getCtx(), DebugActivity.class);
        Intent intent = new Intent(getCtx(), MainActivity.class);
        if (data) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }
        finishAct();
        startActivity(intent);
    }
}
