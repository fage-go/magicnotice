package com.zhangshuo.magicnotice.ui.dialog;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.LinearLayout;

import com.zhangshuo.common.dialog.BaseTipsDialog;
import com.zhangshuo.common.floatwindow.permission.FloatPermissionManager;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.MainHandlerUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.barrage.BarrageView;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.databinding.DialogFirstGuideBinding;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;
import com.zhangshuo.magicnotice.util.ConfigInit;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class FirstGuideDialog extends BaseTipsDialog<DialogFirstGuideBinding> {
    private boolean isEnableNotice, isEnableFloat;
    private boolean inited = false;

    @Override
    public void initView() {
        initBarrage();
        binding.tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableNotice && isEnableFloat) {
                    createDefaultConfig();
                } else {
                    ToastManager.get().showShortToast(StringUtil.getString(R.string.first_guide_no_power));
                }
            }
        });
        binding.clPermissionNotify.setOnClickListener(v -> {
            NotificationHelper.openNotification(getContext());
        });
        binding.clPermissionFloat.setOnClickListener(v -> {
            FloatPermissionManager.getInstance().applyFloatWindow(getContext(), true);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainHandlerUtil.get().post(new Runnable() {
            @Override
            public void run() {
                checkPower();
            }
        }, 500);
    }

    private void checkPower() {
        try {
            isEnableNotice = NotificationHelper.isNotificationListenerEnabled(getContext());
            isEnableFloat = FloatPermissionManager.getInstance().checkPermission(getContext());
            binding.switchNotify.setChecked(isEnableNotice);
            binding.switchFloat.setChecked(isEnableFloat);
            EventBusUtil.sendEvent(new EventMessage<>(EventCodes.PERMISSION_NOTIFY_CLOSED));
        } catch (Exception e) {

        }

    }

    private void createDefaultConfig() {
        ConfigInit.checkAndFirstSchedulerConfig(new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ToastManager.get().noticeShortMessage(getString(R.string.first_guide_config_suc));
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.UPDATE_RULE));
                dismiss();
            }
        });
    }

    private void initBarrage() {
        BarrageView b1 = new BarrageView(getContext(), "FistDemo");
        b1.setBarrageLogo(R.mipmap.ic_launcher);
        b1.setBarrageText(getString(R.string.first_guide_tip_config_1));
        b1.setBarrageModel(BarrageBehaviorModel.getDefault());
        configBarrageViewAnim(b1, 0);
        binding.llBarrage.addView(b1);

        BarrageView b2 = new BarrageView(getContext(), "FistDemo");
        b2.setBarrageLogo(R.mipmap.ic_launcher);
        b2.setBarrageText(getString(R.string.first_guide_tip_cofing_2));
        BarrageBehaviorModel aDefault = BarrageBehaviorModel.getDefault();
        aDefault.setBgColorHex("#343434");
        aDefault.setTxtColorHex("#EFEFEF");
        b2.setBarrageModel(aDefault);
        configBarrageViewAnim(b2, 1000);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = AutoSizeUtils.dp2px(getContext(), 20);
        binding.llBarrage.addView(b2, layoutParams);
    }

    private void configBarrageViewAnim(BarrageView barrageView, long startDelay) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(barrageView, "moveX", getDialogWidth(), 0);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setDuration(5000);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.start();
    }

}
