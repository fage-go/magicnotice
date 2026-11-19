package com.zhangshuo.magicnotice.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.base.BaseFragmentAdapter;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.floatwindow.permission.FloatPermissionManager;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.pay.IPayCallback;
import com.zhangshuo.common.pay.PayManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.MainHandlerUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.barrage.BarrageHelper;
import com.zhangshuo.magicnotice.config.AppStatusConfig;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.config.helper.ScreenStatusHelper;
import com.zhangshuo.magicnotice.databinding.ActivityMainBinding;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.guide.GuideDialog;
import com.zhangshuo.magicnotice.guide.GuideInfo;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.tts.TTSHelper;
import com.zhangshuo.magicnotice.ui.dialog.FirstGuideDialog;
import com.zhangshuo.magicnotice.ui.fragment.ConfigFragment;
import com.zhangshuo.magicnotice.ui.fragment.IndexFragment;
import com.zhangshuo.magicnotice.ui.fragment.SchedulerFragment;
import com.zhangshuo.magicnotice.ui.fragment.SetFragment;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.widget.ShortcutsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(path = RoutePaths.APP_ACTIVITY_MAIN)
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private List<Integer> bottomItemViewIDList = Arrays.asList(
            R.id.navigation_item_index,
            R.id.navigation_item_time,
            R.id.navigation_item_config,
            R.id.navigation_item_set
    );

    private boolean showFloatDialog = false;
    private FirstGuideDialog firstGuideDialog;
    private IPayCallback payCallback;


    @Override
    protected void initView() {
        showTitle(true, false);
        binding.viewPager.setOffscreenPageLimit(4);
        binding.viewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager()
                , Arrays.asList(
                new IndexFragment(),
                new SchedulerFragment(),
                new ConfigFragment(),
                new SetFragment()
        )));
        binding.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.bottomNavigation.setSelectedItemId(bottomItemViewIDList.get(position));
            }
        });
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                binding.viewPager.setCurrentItem(bottomItemViewIDList.indexOf(item.getItemId()));
                return true;
            }
        });
        binding.clWranTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationHelper.openNotification(MainActivity.this);
            }
        });
        //初始化
        BarrageHelper.get().init();
        ShortcutsUtil.get().showShortcuts();

    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationHelper.get().refreshListenerForeground(true);
        if (firstGuideDialog == null || !firstGuideDialog.isShowing()) {
            checkPermission();
        }
        ScreenStatusHelper.SCREEN_STATUS = true;
        hideLoading();
        AppStatusConfig.get().refreshHeadsetName();
        checkPayResult();
    }

    private void checkPayResult() {
        PayManager.get().hasPay(this, payCallback);
    }

    public void toPay() {
        showLoading(false);

        payCallback = new IPayCallback() {
            @Override
            public void onInit(boolean success) {
                if (success) {
                    PayManager.get().toPay(MainActivity.this, payCallback);
                }
            }

            @Override
            public void onPayResult(boolean success) {
                hideLoading();
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.PAY_RESULT, success));
            }
        };
        PayManager.get().init(this, payCallback);
    }

    @Override
    protected void initData() {
        //部分功能需要提前初始化
        TTSHelper.getInstance().initTextToSpeech(null);
        MainHandlerUtil.get().post(new Runnable() {
            @Override
            public void run() {
                checkFirstInit();
            }
        }, 200);
    }


    @Override
    public void onBackPressed() {
        try {
            gotoDesktop();
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private void showGuideDialog() {
        List<GuideInfo> guideInfoList = new ArrayList<>();
        guideInfoList.add(new GuideInfo(R.layout.layout_guide_step_1));
        guideInfoList.add(new GuideInfo(R.layout.layout_guide_step_2));
        guideInfoList.add(new GuideInfo(R.layout.layout_guide_step_3));
        GuideDialog guideDialog = new GuideDialog();
        guideDialog.setGuideInfoList(guideInfoList);
        guideDialog.setOnCallback(new GuideDialog.OnCallback() {
            @Override
            public void onNext(int position) {
                binding.viewPager.setCurrentItem(position);
            }
        });
        guideDialog.show(getCtx());
    }

    private void gotoDesktop() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);//退到后台而不结束
    }


    private void checkFirstInit() {
        boolean haveFirstInit = LocalDataUtil.get().getData(LocalDataKeys.BKEY_FIRST_INIT, false);
        if (haveFirstInit) {
            checkSysSetGuide();
            return;
        }
        ConfigRuleDataService.findConfigRuleInfoList(new AsyncCallback<List<ConfigRuleModel>>() {
            @Override
            public void onSuccess(List<ConfigRuleModel> result) {
                if (result.isEmpty()) {
                    firstGuideDialog = new FirstGuideDialog();
                    firstGuideDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showGuideDialog();
                            firstGuideDialog = null;
                        }
                    });
                    firstGuideDialog.show(getCtx());
                } else {
                    LocalDataUtil.get().saveData(LocalDataKeys.BKEY_FIRST_INIT, true);
                }
            }
        });
    }

    private void checkSysSetGuide() {
        boolean haveSysInit = LocalDataUtil.get().getData(LocalDataKeys.BKEY_SYS_INIT, false);
        if (haveSysInit) {
            return;
        }
        CommonDialog.newInstance()
                .setTitle(getString(R.string.txt_sys_init_title))
                .setContent(getString(R.string.txt_sys_init_content))
                .setAutoClose(true)
                .setCommonAction(getString(R.string.txt_sys_init_auction))
                .setCommonActionListener(new CommonDialog.ActionListener() {
                    @Override
                    public void onClick(CommonDialog dialog) {
                        binding.viewPager.setCurrentItem(bottomItemViewIDList.indexOf(R.id.navigation_item_set), true);
                    }
                }).show(getCtx());
        LocalDataUtil.get().saveData(LocalDataKeys.BKEY_SYS_INIT, true);
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.PERMISSION_FLOAT_CLOSED) {
            if (!showFloatDialog) {
                showFloatDialog = true;
                FloatPermissionManager.getInstance().showConfirmDialog(getCtx(), new FloatPermissionManager.OnConfirmResult() {
                    @Override
                    public void confirmResult(boolean confirm) {
                        if (confirm) {
                            FloatPermissionManager.getInstance().doApplyPermission(getCtx());
                        }
                        showFloatDialog = false;
                    }
                });
            }
        } else if (eventMessage.getCode() == EventCodes.PERMISSION_NOTIFY_CLOSED) {
            checkPermission();
        }
    }

    private void checkPermission() {
        MainHandlerUtil.get().post(new Runnable() {
            @Override
            public void run() {
                boolean notificationListenerEnabled = NotificationHelper.isNotificationListenerEnabled(getCtx());
                onPermissionResult(notificationListenerEnabled);
            }
        }, 500);
    }

    public void onPermissionResult(boolean haveNotification) {
        EventBusUtil.sendEvent(new EventMessage<>(EventCodes.RUNNING_STATUS, haveNotification));
        if (haveNotification) {
            if (binding.clWranTip.getVisibility() == View.VISIBLE) {
                binding.clWranTip.animate()
                        .scaleYBy(1f)
                        .scaleY(0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                binding.clWranTip.setVisibility(View.GONE);
                            }
                        }).start();
            }
        } else {
            binding.tvWarnTip.setText(R.string.main_permission_tip_notify);
            if (binding.clWranTip.getVisibility() == View.GONE) {
                binding.clWranTip.setScaleY(0f);
                binding.clWranTip.setVisibility(View.VISIBLE);
                binding.clWranTip.animate()
                        .scaleYBy(0f)
                        .scaleY(1f)
                        .start();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.FILE_SELECT, uri));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}