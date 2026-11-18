package com.zhangshuo.common.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.R;
import com.zhangshuo.common.mvp.BaseViewBinder;

public abstract class BaseTipsDialog<D extends ViewBinding> extends BaseDialog {

    protected D binding;

    // 初始化view
    public abstract void initView();

    // ---------------------------- 私有方法 ----------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (D) BaseViewBinder.createViewBindingObject(this, inflater, container);
        initDefault();
        initView();
        return binding.getRoot();
    }

    private void initDefault() {
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(canDismissOutSide());
            setCancelable(canCancel());
            Window window = getDialog().getWindow();
            if (window != null && getAnimationStyle() > 0) {
                window.setWindowAnimations(getAnimationStyle());
            }
        }
    }

    // ------------------------- 子类复写方法 -------------------------

    /**
     * 点击外部是否可关闭
     */
    protected boolean canDismissOutSide() {
        return true;//默认true
    }

    /**
     * 点击返回是否可关闭
     */
    protected boolean canCancel() {
        return true;//默认true
    }

    /**
     * 弹窗显示动画
     */
    protected int getAnimationStyle() {
        return -1;//默认动画
    }

    /**
     * 是否背景变暗动画
     */
    protected boolean backgroundDimEnabled() {
        return true;//默认true
    }

    /**
     * 弹窗宽度
     */
    protected int getDialogWidth() {
        // 默认屏幕宽度的0.85
        return (int) (ScreenUtils.getScreenWidth() * 0.85);
    }

    /**
     * 弹窗高度
     */
    protected int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getDialogAnimation() {
        return R.style.base_dialogAnimStyle;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // 防止非UI线程设置layout导致崩溃
            if (getDialog() != null && getDialog().getWindow() != null) {
                Window window = getDialog().getWindow();
                window.setWindowAnimations(getDialogAnimation());
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = getDialogWidth();
                // 这里的弹窗高度务必是wrap_content，业务自己在布局中处理真实高度
                // 因为wrap_content模式下不会有状态栏变色、输入框键盘兼容等问题...
                params.height = getDialogHeight();
//                if (contains || !backgroundDimEnabled()) {
                if (!backgroundDimEnabled()) {
                    // 当前窗口已存在dialog且dialog可重叠，去掉背景变暗(避免窗口闪烁)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
                window.setAttributes(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show(Context context) {
        if (filter()) return;
        super.show(context);
    }
}
