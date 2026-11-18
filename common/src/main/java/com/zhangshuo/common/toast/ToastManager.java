package com.zhangshuo.common.toast;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.R;
import com.zhangshuo.common.util.StringUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ToastManager {
    public static final int MSG_WHAT_SHOW = 8372;
    private Application application;
    private int showActivityCount = 0;
    private Activity currentActivity;
    private WindowManager windowManager;
    private List<ToastBean> toastBeanList = new CopyOnWriteArrayList<>();
    private boolean isShow;//当前是否正在展示
    private Handler handler;
    private ToastView toastView;


    public void showShortToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }
    public void showShortToast(int resId){
        showShortToast(StringUtil.getString(resId));
    }

    public void showLongToast(String msg) {
        showToast(msg, Toast.LENGTH_LONG);
    }

    public void showToast(String msg, int flag) {
        Toast toast = Toast.makeText(AppUtil.get().getApplication(), null, flag);
        ToastView toastView = new ToastView(AppUtil.get().getApplication());
        toastView.setData(msg + "", 0);
        toast.setView(toastView);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            hook(toast);
        }
        try {
            toast.setGravity(Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        toast.show();
    }

    private static Field sField_TN;
    private static Field sField_TN_Handler;

    static {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            try {
                sField_TN = Toast.class.getDeclaredField("mTN");
                sField_TN.setAccessible(true);

                sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler.setAccessible(true);
            } catch (Exception ignored) {
            }
        }
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWrapper(preHandler));
        } catch (Exception ignored) {
        }
    }

    private static class SafelyHandlerWrapper extends Handler {

        private Handler impl;

        SafelyHandlerWrapper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                impl.handleMessage(msg);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 展示shortMessage
     *
     * @param msg
     */
    public void noticeShortMessage(String msg) {
        noticeMessage(msg, 2000, 0);
    }

    public void noticeShortMessage(int resId){
        noticeShortMessage(StringUtil.getString(resId));
    }

    /**
     * 展示longMessage
     *
     * @param msg
     */
    public void noticeLongMessage(String msg) {
        noticeMessage(msg, 5000, 0);
    }

    public void noticeLongMessage(int resId){
        noticeLongMessage(StringUtil.getString(resId));
    }

    public void noticeShortMessage(String msg, int icon) {
        noticeMessage(msg, 3000, icon);
    }

    public void noticeLongMessage(String msg, int icon) {
        noticeMessage(msg, 5000, icon);
    }


    private void noticeMessage(String msg, long time, int icon) {
        ToastBean toastBean = new ToastBean(msg, icon, time);
        toastBeanList.add(toastBean);
        checkStartShow();
    }

    /**
     * 检测是否可以开始展示
     */
    private void checkStartShow() {
        if (!isShow) {
            isShow = true;
            getHandler().sendEmptyMessage(MSG_WHAT_SHOW);
        }
    }

    /**
     * 检测是否需要展示
     */
    private void checkShow() {
        //判断当前的是否正在显示，如果正在显示
        try {
            if (currentActivity != null && toastView != null && toastView.isAttachedToWindow()) {
                toastBeanList.remove(0);
                windowManager.removeViewImmediate(toastView);
                toastView = null;
                windowManager = null;
            }
            if (currentActivity == null || !AppUtils.isAppForeground() || toastBeanList.isEmpty()) {
                //如果当前的currentActivity为null，或者不在前台
                isShow = false;
                return;
            }
            // 添加View到WindowManager中
            if (toastView == null) {
                toastView = new ToastView(currentActivity);
                windowManager = getWindowManager(currentActivity);
                windowManager.addView(toastView, getLayoutParams());
            }
            ToastBean firstToastBean = toastBeanList.get(0);
            firstToastBean.setStartTime(System.currentTimeMillis());
            toastView.setData(firstToastBean.getMsg(), firstToastBean.getIcon());
            //延迟隐藏或者去展示下一个
            getHandler().sendEmptyMessageDelayed(MSG_WHAT_SHOW, firstToastBean.getKeepTime());
        } catch (Exception e) {

        }
    }


    /**
     * 关闭当前的显示
     *
     * @param activity
     */
    private void destroyWindow(Activity activity) {
        if (toastView != null) {
            try {
                getHandler().removeMessages(MSG_WHAT_SHOW);
                ToastBean toastBean = toastBeanList.get(0);
                if (toastBean != null && toastBean.getStartTime() > 0) {
                    long diffTime = (System.currentTimeMillis() - toastBean.getStartTime());
                    if (diffTime > toastBean.getKeepTime()) {
                        toastBeanList.remove(0);
                    } else {
                        //剩余的时间
                        toastBeanList.get(0).setKeepTime(diffTime);
                    }
                }
                windowManager.removeViewImmediate(toastView);
            } catch (Exception e) {

            }
            toastView = null;
            windowManager = null;
            isShow = false;
        }
    }

    private WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }


    private WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 应用内窗口，设置type为Application，其他type需要权限
        // Window范围内的touch事件当前Window处理，window外的事件透传给外部处理
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = -2;
        params.height = -2;
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        params.windowAnimations = R.style.NoticeStyle;
        return params;
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MSG_WHAT_SHOW) {
                        checkShow();
                    }
                }
            };
        }
        return handler;
    }

    /**
     * 初始化
     *
     * @param application
     */
    public void init(Application application) {
        this.application = application;
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (currentActivity != null) {
                    if (!getActivityCode(currentActivity).equals(getActivityCode(activity))) {
                        //如果不是同一个的情况下
                        destroyWindow(currentActivity);
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
                checkStartShow();
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private String getActivityCode(Activity activity) {
        return activity.getClass().getSimpleName() + "@" + activity.hashCode();
    }

    //    单例模式
    private ToastManager() {
    }

    public static ToastManager get() {
        return Holder.instance;
    }

    private static final class Holder {
        public static final ToastManager instance = new ToastManager();
    }
}
