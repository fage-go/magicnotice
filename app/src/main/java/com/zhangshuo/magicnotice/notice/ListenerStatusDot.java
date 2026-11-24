package com.zhangshuo.magicnotice.notice;

import android.app.AppOpsManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.config.LocalDataKeys;

/** A display-only connection indicator. Main-thread only; it never starts a service. */
final class ListenerStatusDot {
    private final Context context;
    private final WindowManager windows;
    private final AppOpsManager appOps;
    private final AppOpsManager.OnOpChangedListener permissionWatcher;
    private boolean watching;
    private View dot;
    private int sizePx;
    private int marginPx;
    private int attachedMode;

    ListenerStatusDot(Context context, Handler main, Runnable refresh) {
        this.context = context;
        windows = context.getSystemService(WindowManager.class);
        appOps = context.getSystemService(AppOpsManager.class);
        // AppOps may call from another thread. Re-read all eligibility, not the old event.
        permissionWatcher = (op, pkg) -> {
            if (context.getPackageName().equals(pkg)) main.post(refresh);
        };
    }

    void refresh(boolean connected) {
        try {
            int saved = LocalDataUtil.get().getData(
                    LocalDataKeys.IKEY_LISTENER_STATUS_DOT, ListenerStatusDotMode.OFF);
            boolean enabled = connected && ListenerStatusDotMode.isEnabled(saved);
            watchPermission(enabled);
            int mode = ListenerStatusDotMode.displayed(saved, connected,
                    enabled && windows != null && Settings.canDrawOverlays(context));
            if (mode == ListenerStatusDotMode.OFF) {
                hide();
                return;
            }
            int size = dp(8);
            int margin = dp(8);
            if (dot != null && dot.getParent() != null && size == sizePx && margin == marginPx
                    && attachedMode == mode) {
                return;
            }
            hide();
            View view = new View(context);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            if (mode == ListenerStatusDotMode.DOT) {
                shape.setColor(Color.BLACK);
                shape.setStroke(dp(1), Color.WHITE);
            } else {
                shape.setColor(Color.TRANSPARENT);
            }
            view.setBackground(shape);
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            view.setClickable(false);
            view.setFocusable(false);

            int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE;
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(size, size, type,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.RIGHT;
            params.x = margin;
            params.y = margin;
            // Set window alpha, not View alpha, for Android's obscured-touch checks.
            params.alpha = mode == ListenerStatusDotMode.DOT ? 0.5f : 0f;
            params.setTitle("MagicNotice listener status");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                params.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                params.setFitInsetsTypes(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                params.setFitInsetsIgnoringVisibility(true);
            }
            windows.addView(view, params);
            dot = view;
            sizePx = size;
            marginPx = margin;
            attachedMode = mode;
        } catch (RuntimeException error) {
            hide();
            LogUtils.w("listener_status_dot error=" + error.getClass().getSimpleName());
        }
    }

    private int dp(int value) {
        return Math.max(1, Math.round(value * context.getResources().getDisplayMetrics().density));
    }

    private void watchPermission(boolean enable) {
        if (watching == enable || appOps == null) return;
        try {
            if (enable) {
                appOps.startWatchingMode(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                        context.getPackageName(), permissionWatcher);
            } else {
                appOps.stopWatchingMode(permissionWatcher);
            }
            watching = enable;
        } catch (RuntimeException error) {
            LogUtils.w("listener_status_dot watcher=" + error.getClass().getSimpleName());
        }
    }

    private void hide() {
        if (dot == null) return;
        View old = dot;
        dot = null;
        old.setVisibility(View.GONE);
        try {
            windows.removeViewImmediate(old);
        } catch (RuntimeException error) {
            LogUtils.w("listener_status_dot hide=" + error.getClass().getSimpleName());
        }
    }
}
