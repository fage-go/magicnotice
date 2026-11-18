package com.zhangshuo.common.floatwindow.permission.rom;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;


/**
 */
public class VivoUtils {

    public static boolean checkFloatWindowPermission(Context context) {
        try {
            String packageName = context.getPackageName();
            Uri uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
            String selection = "pkgname = ?";
            String[] selectionArgs = new String[]{packageName};
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri, null, selection, selectionArgs, null);
            if (cursor != null) {
                cursor.getColumnNames();
                if (cursor.moveToFirst()) {
                    int currentmode = cursor.getInt(cursor.getColumnIndex("currentlmode"));
                    cursor.close();
                    return currentmode == 0;
                } else {
                    cursor.close();
                    return getFloatPermissionStatus2(context);
                }

            } else {
                return getFloatPermissionStatus2(context);
            }
        } catch (Throwable err) {
            err.printStackTrace();
        }
        return false;
    }

    public static void applyPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.putExtra("packagename", context.getPackageName());
            intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"));
            PackageManager packageManager = context.getPackageManager();
            if (packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
                context.startActivity(intent);
            } else {
                intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
                context.startActivity(intent);
            }
        } catch (Throwable err) {
            err.printStackTrace();
        }
    }

    private static boolean getFloatPermissionStatus2(Context context) {
        String packageName = context.getPackageName();
        Uri uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = context
                .getContentResolver()
                .query(uri2, null, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int currentmode = cursor.getInt(cursor.getColumnIndex("currentmode"));
                cursor.close();
                return currentmode == 0;
            } else {
                cursor.close();
                return false;
            }
        }
        return false;

    }


}
