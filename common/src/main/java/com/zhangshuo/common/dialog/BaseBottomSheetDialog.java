package com.zhangshuo.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zhangshuo.common.R;
import com.zhangshuo.common.mvp.BaseViewBinder;

public abstract class BaseBottomSheetDialog<D extends ViewBinding> extends BaseDialog {


    private BottomSheetBehavior<FrameLayout> mBottomSheetBehavior;
    private boolean waitingForDismissAllowingStateLoss;

    protected D binding;

    public abstract void initView();

    // ---------------------------- 私有方法 ----------------------------


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.base_DialogBottomSheetAnim);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), getTheme());
        binding = (D) BaseViewBinder.createViewBindingObject(this, bottomSheetDialog.getLayoutInflater(), null);
        bottomSheetDialog.setContentView(binding.getRoot());
        mBottomSheetBehavior = bottomSheetDialog.getBehavior();
        // 以下步骤主要是为了去除收缩这个中间步骤
        if (isSkipCollapsed()) {
            mBottomSheetBehavior.setHideable(true);
            mBottomSheetBehavior.setSkipCollapsed(true);
            mBottomSheetBehavior.setPeekHeight(0);
            mBottomSheetBehavior.addBottomSheetCallback(new CollapsedAlwaysHiddenBottomSheetCallback());
        }
        bottomSheetDialog.setCanceledOnTouchOutside(true);//设置点击外部可消失
        return bottomSheetDialog;
    }


    // ------------------------- 子类复写方法 -------------------------
    @Override
    public void show(Context context) {
        if (filter()) return;
        super.show(context);
    }


    private class CollapsedAlwaysHiddenBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            // 完全取消折叠状态，把折叠状态设置成相当于隐藏状态
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                dismissAllowingStateLoss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // 启动就扩展
        try {
            if (isSkipCollapsed() && mBottomSheetBehavior != null) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 这里才将布局的view加入到bottomSheetDialog的包裹布局中
        initView();
    }

    @Override
    public void dismiss() {
        try {
            if (!tryDismissWithAnimation(false)) {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        try {
            if (!tryDismissWithAnimation(true)) {
                super.dismissAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to dismiss the dialog fragment with the bottom sheet animation. Returns true if possible,
     * false otherwise.
     */
    private boolean tryDismissWithAnimation(boolean allowingStateLoss) {
        Dialog baseDialog = getDialog();
        if (baseDialog instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) baseDialog;
            BottomSheetBehavior<?> behavior = dialog.getBehavior();
            if (behavior.isHideable() && dialog.getDismissWithAnimation()) {
                dismissWithAnimation(behavior, allowingStateLoss);
                return true;
            }
        }

        return false;
    }

    private void dismissWithAnimation(
            @NonNull BottomSheetBehavior<?> behavior, boolean allowingStateLoss) {
        waitingForDismissAllowingStateLoss = allowingStateLoss;

        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            dismissAfterAnimation();
        } else {
            if (getDialog() instanceof BottomSheetDialog) {
                ((BottomSheetDialog) getDialog()).removeDefaultCallback();
            }
            behavior.addBottomSheetCallback(new BottomSheetDismissCallback());
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void dismissAfterAnimation() {
        if (waitingForDismissAllowingStateLoss) {
            super.dismissAllowingStateLoss();
        } else {
            super.dismiss();
        }
    }

    private class BottomSheetDismissCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismissAfterAnimation();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    }

    /**
     * 是否直接跳过折叠模式，折叠时默认会隐藏
     * 默认会跳过折叠模式，只有扩展和隐藏状态。
     */
    public boolean isSkipCollapsed() {
        return true;
    }
}
