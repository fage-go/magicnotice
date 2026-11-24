package com.zhangshuo.magicnotice.ui.fragment;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.AppUtils;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.dialog.BottomOptionDialog;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.floatwindow.permission.FloatPermissionManager;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.pay.PayManager;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.MainHandlerUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.FragmentSetBinding;
import com.zhangshuo.magicnotice.db.model.CountInfo;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.notice.ListenerStatusDotMode;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.ui.MainActivity;
import com.zhangshuo.magicnotice.ui.dialog.DeleteNoticeDialog;
import com.zhangshuo.magicnotice.ui.dialog.ExposeDataDialog;
import com.zhangshuo.magicnotice.ui.dialog.FingerPrintDialog;
import com.zhangshuo.magicnotice.ui.dialog.SetLanguageDialog;
import com.zhangshuo.magicnotice.ui.dialog.ShowEmailDialog;
import com.zhangshuo.magicnotice.ui.dialog.ShowWXDialog;
import com.zhangshuo.magicnotice.util.ActivityFreeformUtil;
import com.zhangshuo.magicnotice.util.ConfigInit;

import java.util.Arrays;

@Route(path = RoutePaths.APP_FRAGMENT_SET)
public class SetFragment extends BaseFragment<FragmentSetBinding>
        implements BottomOptionDialog.OnOptionCallback {
    private boolean syncingListenerStatusDotSwitch;
    private static final String LISTENER_STATUS_DOT_CHOICES = "listener_status_dot_choices";


    @Override
    protected void initView() {
        binding.tvTitle.getPaint().setFakeBoldText(true);
        binding.clReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示信息
                showFastReplyDialog();
            }
        });
        binding.clLanguage.setOnClickListener(v -> {
            //语言设置
            new SetLanguageDialog().show(getCtx());
        });

        binding.switchFigure.setChecked(LocalDataUtil.get().getData(LocalDataKeys.BKEY_FINGURE_FLAG, false));
        binding.clFigureLock.setOnClickListener(v -> {
            //指纹锁
            if (supportFingerprint()) {
                showFingerPrintDialog();
            }
        });
        binding.clUserPrivacy.setOnClickListener(v -> {
            //用户协议
            Bundle bundle = new Bundle();
            if (ConfigInit.isBuildTypeZH()) {
                bundle.putString("url", RoutePaths.APP_USER_PRIVACY_PATH);
            } else {
                bundle.putString("url", RoutePaths.APP_PRIVACY_EN_PATH);
            }
            bundle.putString("title", getString(R.string.agreement_title_user_privacy));
            RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_H5, bundle);
        });
        if (ConfigInit.isBuildTypeZH()) {
            binding.viewUserPrivacy.setVisibility(View.VISIBLE);
            binding.clPrivacy.setVisibility(View.VISIBLE);
        } else {
            binding.viewUserPrivacy.setVisibility(View.GONE);
            binding.clPrivacy.setVisibility(View.GONE);
        }
        binding.clPrivacy.setOnClickListener(v -> {
            //隐私协议
            Bundle bundle = new Bundle();
            bundle.putString("url", RoutePaths.APP_PRIVACY_PATH);
            bundle.putString("title", getString(R.string.agreement_title_privacy));
            RouterUtil.get().navigation(RoutePaths.APP_ACTIVITY_H5, bundle);
        });
        binding.clCache.setOnClickListener(v -> {
            //清除缓存
            new DeleteNoticeDialog().show(getCtx());
        });
        binding.clSuggest.setOnClickListener(v -> {
            //意见反馈
            if (ConfigInit.isBuildTypeZH()) {
                new ShowWXDialog().show(getContext());
            } else {
                new ShowEmailDialog().show(getContext());
            }
        });
        binding.clVersion.setOnClickListener(v -> {
            //版本更新
//            UpdateUtil.check(getCtx(), true);
        });
//        if (AppUtil.isChineseLanguage()) {
//            binding.clShare.setVisibility(View.VISIBLE);
//            binding.clShare.setOnClickListener(v -> {
//                //分享 https://www.coolapk.com/apk/294294
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, "哇喔！我发现了好棒的应用，好东西要第一时间和您分享呢>>> https://www.coolapk.com/apk/294294");
//                intent.setType("text/plain");
//                startActivity(intent);
//            });
//        } else {
        binding.vLineShare.setVisibility(View.GONE);
        binding.clShare.setVisibility(View.GONE);
//        }
        binding.clReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartApp();
            }
        });

        binding.switchKeepAliveNotify.setChecked(LocalDataUtil.get().getData(LocalDataKeys.BKEY_KEEP_ALIVE_NOTIFICATION, false));
        binding.clKeepAliveNotify.setOnClickListener(v -> {
            boolean enable = !LocalDataUtil.get().getData(LocalDataKeys.BKEY_KEEP_ALIVE_NOTIFICATION, false);
            LocalDataUtil.get().saveData(LocalDataKeys.BKEY_KEEP_ALIVE_NOTIFICATION, enable);
            binding.switchKeepAliveNotify.setChecked(enable);
            NotificationHelper.get().refreshKeepAliveNotification(getCtx());
        });

        syncListenerStatusDotSwitch();
        binding.clListenerStatusDot.setOnClickListener(v -> toggleListenerStatusDot());
        binding.switchListenerStatusDot.setOnCheckedChangeListener((button, checked) -> {
            // Switch also changes via drag and keyboard, not only clicks.
            if (!syncingListenerStatusDotSwitch) toggleListenerStatusDot();
        });

        binding.clNotify.setOnClickListener(v -> {
            if (NotificationHelper.isNotificationListenerEnabled(getCtx())) {
                ToastManager.get().noticeShortMessage(getString(R.string.set_power_haved));
            } else {
                NotificationHelper.openNotification(getContext());
            }
        });
        binding.clFloat.setOnClickListener(v -> {
            if (FloatPermissionManager.getInstance().checkPermission(getCtx())) {
                ToastManager.get().noticeShortMessage(getString(R.string.set_power_haved));
            } else {
                FloatPermissionManager.getInstance().applyFloatWindow(getCtx(), false);
            }
        });
        binding.clBackground.setOnClickListener(v -> {
            //后台运行
            showBackgroundDialog();
        });
        binding.clBattery.setOnClickListener(v -> {
            //电池优化
            showIgnoreBatteryDialog();
        });
        binding.switchHide.setChecked(LocalDataUtil.get().getData(LocalDataKeys.BKEY_HIDE_RECENT, false));
        binding.switchHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocalDataUtil.get().saveData(LocalDataKeys.BKEY_HIDE_RECENT, isChecked);
                ToastManager.get().noticeShortMessage(getString(R.string.set_ui_hide_recent_tip));
            }
        });

        binding.clFreeform.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                if (ConfigInit.isBuildTypeZH()) {
                    new ShowWXDialog().setExtTip(getString(R.string.set_show_float_tip)).show(getCtx());
                } else {
                    new ShowEmailDialog().setExtTip(getString(R.string.set_show_float_tip)).show(getCtx());
                }
            }
        });
        binding.clData.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                showExposeDataDialog();
            }
        });
        binding.clPro.setVisibility(PayManager.get().isPay() ? View.GONE : View.VISIBLE);
        binding.clPro.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                FragmentActivity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).toPay();
                }
            }
        });
    }

    @Override
    public void onReceiveEvent(EventMessage eventMessage) {
        super.onReceiveEvent(eventMessage);
        if (eventMessage.getCode() == EventCodes.PAY_RESULT) {
            boolean result = (boolean) eventMessage.getData();
            binding.clPro.setVisibility(result ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        //设置条数
        NotificationDataService.queryNotificationCount(new AsyncCallback<CountInfo>() {
            @Override
            public void onSuccess(CountInfo result) {
                setCacheInfo(result);
            }
        });
        //设置版本
        binding.tvAboutName.setText(getString(R.string.app_name) + " " + AppUtils.getAppVersionName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MainHandlerUtil.get().post(new Runnable() {
            @Override
            public void run() {
                boolean notifyEnable = NotificationHelper.isNotificationListenerEnabled(getCtx());
                binding.switchNotify.setChecked(notifyEnable);
                binding.switchKeepAliveNotify.setChecked(LocalDataUtil.get().getData(LocalDataKeys.BKEY_KEEP_ALIVE_NOTIFICATION, false));
                NotificationHelper.get().refreshKeepAliveNotification(getCtx());
                boolean floatEnable = FloatPermissionManager.getInstance().checkPermission(getCtx());
                binding.switchFloat.setChecked(floatEnable);
                ActivityFreeformUtil.SupportInfo freeformSupport = ActivityFreeformUtil.isFreeformSupport(getCtx());
                binding.tvFreeformValue.setText(freeformSupport.getSpecName() + " " + freeformSupport.getSpecVersion());

            }
        }, 500);
    }

    private int listenerStatusDotMode() {
        return LocalDataUtil.get().getData(
                LocalDataKeys.IKEY_LISTENER_STATUS_DOT, ListenerStatusDotMode.OFF);
    }

    private void syncListenerStatusDotSwitch() {
        syncingListenerStatusDotSwitch = true;
        try {
            binding.switchListenerStatusDot.setChecked(
                    ListenerStatusDotMode.isEnabled(listenerStatusDotMode()));
        } finally {
            syncingListenerStatusDotSwitch = false;
        }
    }

    private void toggleListenerStatusDot() {
        // A switch tap may toggle the widget before the click reaches this handler.
        // Restore the saved state until a user choice has been made.
        syncListenerStatusDotSwitch();
        if (ListenerStatusDotMode.isEnabled(listenerStatusDotMode())) {
            saveListenerStatusDotMode(ListenerStatusDotMode.OFF);
            return;
        }
        if (!Settings.canDrawOverlays(requireContext())) {
            showListenerStatusDotPermissionTip();
            return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null || fragmentManager.isStateSaved()
                || fragmentManager.findFragmentByTag(LISTENER_STATUS_DOT_CHOICES) != null) {
            return;
        }
        BottomOptionDialog dialog = BottomOptionDialog.withMessage(
                getString(R.string.set_ui_listener_status_dot_recommendation),
                Arrays.asList(
                        getString(R.string.set_ui_listener_status_dot_transparent),
                        getString(R.string.set_ui_listener_status_dot_visible)));
        dialog.setTargetFragment(this, 0);
        dialog.showNow(fragmentManager, LISTENER_STATUS_DOT_CHOICES);
    }

    @Override
    public void onClick(int position) {
        if (position == 0) {
            selectListenerStatusDotMode(ListenerStatusDotMode.TRANSPARENT);
        } else if (position == 1) {
            selectListenerStatusDotMode(ListenerStatusDotMode.DOT);
        }
    }

    @Override
    public void onCancel() {
        // Only selecting a mode enables the switch.
    }

    private void selectListenerStatusDotMode(int choice) {
        Context context = getContext();
        if (context == null || getView() == null) return;
        int mode = ListenerStatusDotMode.selected(choice, Settings.canDrawOverlays(context));
        if (mode == ListenerStatusDotMode.OFF) {
            showListenerStatusDotPermissionTip();
            return;
        }
        saveListenerStatusDotMode(mode);
    }

    private void saveListenerStatusDotMode(int mode) {
        LocalDataUtil.get().saveData(LocalDataKeys.IKEY_LISTENER_STATUS_DOT, mode);
        syncListenerStatusDotSwitch();
        NotificationHelper.get().refreshListenerStatusDot();
    }

    private void showListenerStatusDotPermissionTip() {
        ToastManager.get().noticeShortMessage(
                getString(R.string.set_ui_listener_status_dot_permission_tip));
    }

    public boolean supportFingerprint() {
        KeyguardManager keyguardManager = getContext().getSystemService(KeyguardManager.class);
        FingerprintManager fingerprintManager = getContext().getSystemService(FingerprintManager.class);
        if (!fingerprintManager.isHardwareDetected()) {
            ToastManager.get().showShortToast(getString(R.string.set_fingerprint_no_support));
            return false;
        } else if (!keyguardManager.isKeyguardSecure()) {
            ToastManager.get().showShortToast(getString(R.string.set_fingerprint_no_set));
            return false;
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            ToastManager.get().showShortToast(getString(R.string.set_fingerprint_no_one));
            return false;
        }
        return true;
    }

    private void showFastReplyDialog() {

    }

    private void showBackgroundDialog() {
        String ignoreOptTip = getBackgroundOptTip();
        String content = getString(R.string.set_background_tip) + "\n" + ignoreOptTip;
        CommonDialog.newInstance().setTitle(getString(R.string.set_background_tip_title)).setContentGravity(Gravity.LEFT).setContent(content).setCommonAction(getString(R.string.set_background_tip_know)).setCommonActionColor(getContext().getColor(R.color.base_color_blue)).show(getCtx());
    }


    private void showFingerPrintDialog() {
        FingerPrintDialog fingerPrintDialog = new FingerPrintDialog();
        fingerPrintDialog.setInfo(new FingerPrintDialog.OnFingerAuthCallback() {
            @Override
            public void onSuccess() {
                boolean data = !LocalDataUtil.get().getData(LocalDataKeys.BKEY_FINGURE_FLAG, false);
                LocalDataUtil.get().saveData(LocalDataKeys.BKEY_FINGURE_FLAG, data);
                binding.switchFigure.setChecked(data);
            }

            @Override
            public void onFailed() {

            }
        });
        fingerPrintDialog.show(getContext());
    }

    private void showIgnoreBatteryDialog() {
        final boolean ignoringBatteryOptimizations = isIgnoringBatteryOptimizations();
        String ignoreOptTip = getIgnoreBatteryOptimizationsOptTip();
        String content = getString(R.string.set_barrtery_tip) + "\n" + ignoreOptTip;
        CommonDialog.newInstance().setTitle(getString(R.string.set_barrtery_tip_title)).setContent(content).setContentGravity(Gravity.LEFT).setAutoClose(false).setCommonAction(ignoringBatteryOptimizations ? getString(R.string.set_barrtery_tip_goset) : getString(R.string.set_barrtery_tip_apply)).setCommonActionColor(getContext().getColor(R.color.base_color_blue)).setCommonActionListener(new CommonDialog.ActionListener() {
            @Override
            public void onClick(CommonDialog dialog) {
                if (ignoringBatteryOptimizations) {
                    goIgnoreBatteryOptimizationsSetting();
                } else {
                    requestIgnoreBatteryOptimizations();
                }
            }
        }).show(getCtx());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goIgnoreBatteryOptimizationsSetting() {
        try {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(i);
        } catch (Exception e) {

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName());
        }
        return isIgnoring;
    }


    private String getIgnoreBatteryOptimizationsOptTip() {
        String tip = "";
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            tip = getString(R.string.set_battery_tip_xiaomi);
        } else if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {
            tip = getString(R.string.set_battery_tip_vivo);
        } else if (Build.MANUFACTURER.toLowerCase().contains("OPPO") || Build.BRAND.equals("oppo")) {
            tip = getString(R.string.set_battery_tip_oppo);
        } else if ((Build.MANUFACTURER.toUpperCase().contains("HUAWEI") || Build.BRAND.equals("Huawei") || Build.BRAND.equals("HONOR"))) {
            tip = getString(R.string.set_battery_tip_huawei);
        } else {
            tip = getString(R.string.set_battery_tip_other);
        }
        return tip;
    }

    private String getBackgroundOptTip() {
        String tip = "";
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            tip = getString(R.string.set_background_tip_xiaomi);
        } else if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {
            tip = getString(R.string.set_background_tip_vivo);
        } else if (Build.MANUFACTURER.toLowerCase().contains("OPPO") || Build.BRAND.equals("oppo")) {
            tip = getString(R.string.set_background_tip_oppo);
        } else if ((Build.MANUFACTURER.toUpperCase().contains("HUAWEI") || Build.BRAND.equals("Huawei") || Build.BRAND.equals("HONOR"))) {
            tip = getString(R.string.set_background_tip_huawei);
        } else {
            tip = getString(R.string.set_background_tip_other);
        }
        return tip;
    }


    private void setCacheInfo(CountInfo cacheInfo) {
        if (cacheInfo.getNotificationCount() > 0) {
            binding.tvCacheValue.setText(String.format(getString(R.string.set_clean_tip_count), cacheInfo.getNotificationCountStr()));
        } else {
            binding.tvCacheValue.setText(R.string.set_clean_tip_none);
        }
    }

    public void reStartApp() {
        Intent intent = getCtx().getPackageManager().getLaunchIntentForPackage(getCtx().getPackageName());
        AlarmManager alarmManager = (AlarmManager) getCtx().getSystemService(Context.ALARM_SERVICE);
        PendingIntent restartPendIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartPendIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void showExposeDataDialog() {
        new ExposeDataDialog().show(getCtx());
    }
}
