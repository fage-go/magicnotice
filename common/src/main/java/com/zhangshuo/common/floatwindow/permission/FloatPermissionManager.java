/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.zhangshuo.common.floatwindow.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.zhangshuo.common.R;
import com.zhangshuo.common.dialog.CommonDialog;
import com.zhangshuo.common.floatwindow.permission.rom.MeizuUtils;
import com.zhangshuo.common.floatwindow.permission.rom.RomUtils;
import com.zhangshuo.common.floatwindow.permission.rom.VivoUtils;
import com.zhangshuo.common.util.StringUtil;


/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:悬浮窗权限适配，很全的适配http://blog.csdn.net/self_study/article/details/52859790
 */
public class FloatPermissionManager {

    private static final String TAG = "FloatPermissionManager";

    private static final int REQUEST_CODE = 8899;

    private static volatile FloatPermissionManager instance;


    public static FloatPermissionManager getInstance() {
        if (instance == null) {
            synchronized (FloatPermissionManager.class) {
                if (instance == null) {
                    instance = new FloatPermissionManager();
                }
            }
        }
        return instance;
    }

    /**
     * 是否开启悬浮窗权限 如果没有开启弹出弹窗并开启
     */
    public boolean applyFloatWindow(Context context, boolean showConfirm) {
        if (checkPermission(context)) {
            return true;
        } else {
            if (showConfirm) {
                showConfirmDialog(context, confirm -> {
                    doApplyPermission(context);
                });
            } else {
                doApplyPermission(context);
            }
            return false;
        }
    }

    public boolean checkPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (RomUtils.checkIsVivoRom()) {
            return vivoPermissionCheck(context);
        }
        if (RomUtils.checkIsMeizuRom()) {
            return meizuPermissionCheck(context);
        }
        return commonROMPermissionCheck(context);
    }

    private boolean meizuPermissionCheck(Context context) {
        return MeizuUtils.checkFloatWindowPermission(context);
    }


    private boolean vivoPermissionCheck(Context context) {
        return VivoUtils.checkFloatWindowPermission(context);
    }

    private boolean commonROMPermissionCheck(Context context) {
        Boolean result = true;
        try {
            result = Settings.canDrawOverlays(context);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return result;
    }

    public void doApplyPermission(Context context) {
        if (RomUtils.checkIsVivoRom()) {
            VivoUtils.applyPermission(context);
        } else if (RomUtils.checkIsMeizuRom()) {
            MeizuUtils.applyPermission(context);
        } else {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
                } else {
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void showConfirmDialog(Context context, OnConfirmResult result) {
        CommonDialog.newInstance()
                .setContent(StringUtil.getString(R.string.float_apply_tip))
                .setLeftAction(StringUtil.getString(R.string.float_apply_cancel))
                .setAutoClose(true)
                .setCommonAction(StringUtil.getString(R.string.float_apply_go_set))
                .setLeftActionListener(dialog -> {
                    result.confirmResult(false);
                })
                .setCommonActionListener(dialog -> {
                    result.confirmResult(true);
                })
                .show(context);
    }

    public interface OnConfirmResult {
        void confirmResult(boolean confirm);
    }
}
