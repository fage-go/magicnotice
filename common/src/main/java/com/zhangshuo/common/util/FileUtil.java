package com.zhangshuo.common.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.R;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {

    public static File getDownloadFile() {
        File file = new File(SDCardUtils.getSDCardPathByEnvironment(), "Download");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsoluteFile();
    }

    public static File getSaveFile() {
        File file = new File(SDCardUtils.getSDCardPathByEnvironment(), StringUtil.getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsoluteFile();
    }


    public static File getIconCacheFile() {
        File cacheFile = new File(AppUtil.get().getApplication().getExternalCacheDir(), "/Icon/");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile;
    }

    public static String saveLogoToCache(String packageName) {
        if (TextUtils.isEmpty(packageName)) return "";
        String appVersionName = AppUtils.getAppVersionName(packageName);
        String fileName = packageName + appVersionName + ".png";
        File file = new File(getIconCacheFile(), fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        Drawable drawable = AppUtils.getAppIcon(packageName);
        if (drawable != null) {
            return saveDrawableToCache(drawable, fileName);
        }
        return "";
    }

    public static String saveIconToCache(@Nullable Icon icon, int iconColor, String packageName, String key) {
        String iconPath = "";
        try {
            Drawable drawable = null;
            if (icon != null) {
                drawable = icon.loadDrawable(AppUtil.get().getApplication());
            }
            if (drawable != null) {
                iconPath = saveDrawableToCache(drawable, iconColor, "", key);
            } else {
                iconPath = saveLogoToCache(packageName);
            }

        } catch (Exception ignore) {

        }
        return iconPath;
    }

    public static String saveDrawableToCache(@Nullable Drawable drawable, String fileName) {
        return saveDrawableToCache(drawable, 0, fileName, "");
    }

    public static String saveDrawableToCache(@Nullable Drawable drawable, int iconColor, String fileName, String key) {
        if (drawable == null) return "";
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        ColorFilter filter = null;
        if (iconColor != 0) {
            filter = new PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
        }
        if (drawable instanceof BitmapDrawable) {
            Paint paint = new Paint();
            if (filter != null)
                paint.setColorFilter(filter);
            canvas.drawBitmap(((BitmapDrawable) drawable).getBitmap(), 0, 0, paint);
        } else {
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            if (filter != null)
                drawable.setColorFilter(filter);
            drawable.draw(canvas);
        }
        return saveBitmapToCache(bitmap, fileName, key);
    }

    public static String saveBitmapToCache(@Nullable Bitmap bitmap, String fileName, String key) {
        if (bitmap == null) return "";
        String md5Key = "";
        if (!TextUtils.isEmpty(key)) {
            md5Key = MD5Utils.md5(key);
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = md5Key + getBitmapMD5Fast(bitmap) + ".png";
        } else {
            fileName = md5Key + fileName;
        }
        File file = new File(getIconCacheFile(), fileName);
        try {
            if (file.exists()) {
                //如果存在一样的图，就不用删除后写入了
                return file.getAbsolutePath();
            }
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private static String getBitmapMD5Fast(Bitmap bitmap) {
        String md5 = System.currentTimeMillis() + "";
        try {
            //快速获取图片的bitmap。分割成20*20的方格
            int size = 21;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int widthCellNum = width / size;
            int heightCellNum = height / size;
            StringBuilder stringBuilder = new StringBuilder();
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    int centerX = row * widthCellNum + widthCellNum / 2;
                    int centerY = column * heightCellNum + heightCellNum / 2;
                    centerX = Math.min(centerX, width);
                    centerY = Math.min(centerY, height);
                    int pixel = bitmap.getPixel(centerX, centerY);
                    stringBuilder.append(pixel);
                }
            }
            md5 = MD5Utils.md5(stringBuilder.toString());
        } catch (Exception e) {

        }
        return md5;
    }

    public static String getDCIMFile(String fileName) {
        File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), StringUtil.getString(R.string.app_name));
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile.getAbsolutePath() + File.separator + fileName;
    }

    public static boolean saveBitmapToAlbum(@Nullable Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
