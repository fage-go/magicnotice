package com.zhangshuo.common.dialog;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.R;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.router.RouterUtil;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.LogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class BaseDialog extends DialogFragment {

    public static final String TAG = "DADialog";

    protected static final long INTERVAL = 600L;//防抖
    protected static long lastClickTime;//上次点击时间
    protected DialogInterface.OnDismissListener onDismissListener;

    protected Context mContext;//context


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CurrentPage", "current is " + getClass().getSimpleName());
        //这里设置style为STYLE_NORMAL，仅自定义样式去掉原边框而已
        setStyle(DialogFragment.STYLE_NORMAL, R.style.base_CommonDialogStyle);
        this.mContext = getContext() == null ? AppUtil.get().getApplication() : getContext();
        RouterUtil.get().inject(this);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    /**
     * dialog防抖过滤
     */
    protected boolean filter() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < INTERVAL) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    /**
     * fragment是否已被销毁
     */
    protected boolean isFragmentEnable() {
        return getActivity() != null
                && !getActivity().isDestroyed()
                && !getActivity().isFinishing()
                && isAdded();
    }

    /**
     * 获取Activity的context
     */
    private FragmentActivity getActivityContext(Context context) {
        try {
            while (context instanceof ContextWrapper) {
                if (context instanceof FragmentActivity) {
                    return (FragmentActivity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ------------------------- 对外提供API -------------------------

    /**
     * 显示对话框
     */
    public void show(Context context) {
        try {
            if (getActivityContext(context) != null) {
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                if (fm.isDestroyed()) return;//Activity销毁return
//                contains = checkContains(fm);//判断屏幕是否已存在dialog
                FragmentTransaction ft = fm.beginTransaction();
                if (fm.findFragmentByTag(TAG + "#" + hashCode()) != null) {
                    // 在每个add事务前增加一个remove事务，防止连续的add
                    ft.remove(this).commitNowAllowingStateLoss();
                }
                // 避免mStateSaved导致的崩溃
                ft.add(this, TAG + "#" + hashCode()).commitAllowingStateLoss();
            } else {
                Log.e(TAG, "context非FragmentActivity，无法弹出对话框！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBusUtil.register(this);
    }

    /**
     * 关闭对话框
     */
    public void close() {
        try {
            // 避免mStateSaved导致的崩溃
            dismissAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        EventBusUtil.unregister(this);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    /**
     * EventBus事件接收
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage eventMessage) {

    }

    /**
     * 判断dialog是否显示
     */
    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception ignored) {
        }
    }

    private LoadingDialog loadingDialog;

    /**
     * 显示loading
     */
    public void showLoading() {
        try {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog.show(getContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(
                    TAG + ";showLoading:" + e
            );
        }
    }

    /**
     * 隐藏loading
     */
    public void hideLoading() {
        if (loadingDialog != null) {
            try {
                loadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(
                        TAG + ";hideLoading:" + e
                );
            }
            loadingDialog = null;
        }
    }
}
