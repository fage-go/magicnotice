package com.zhangshuo.magicnotice.util;

import android.app.AppOpsManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.provider.Settings;

import com.blankj.utilcode.util.RomUtils;

import java.lang.reflect.Method;

/**
 * 后台打开能力的检测
 */
public class OpenPowerCheckUtil {

    /**
     * 0 是不需要判断
     * 1 是有权限
     * 2 是无权限
     *
     * @param context
     * @return
     */
    public static int isAllowed(Context context) {
        if (RomUtils.isXiaomi()) {
            return isXiaomiBgStartPermissionAllowed(context) ? 1 : 2;
        }

        if (RomUtils.isVivo()) {
            return isVivoBgStartPermissionAllowed(context) ? 1 : 2;
        }

        if (RomUtils.isOneplus() || RomUtils.isOppo()) {
            return Settings.canDrawOverlays(context) ? 1 : 2;
        }
        return 0;
    }


    private static boolean isXiaomiBgStartPermissionAllowed(Context context) {
        try {
            AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int op = 10021;
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]{int.class, int.class, String.class});
            Integer result = (Integer) method.invoke(ops, op, Binder.getCallingUid(), context.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isVivoBgStartPermissionAllowed(Context context) {
        Uri uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{context.getPackageName()};
        int state = 1;
        try {
            Cursor query = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
            query.moveToFirst();
            state = query.getInt(query.getColumnIndex("currentstate"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return state == 0;
    }
}
