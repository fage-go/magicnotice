package com.zhangshuo.magicnotice.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.text.TextUtils;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortcutsUtil {

    public static final String ACTION = "com.zhangshuo.magicnotice.widget.SHORTCUT_CLICK";
    private ShortcutInfo openShortcutsInfo;
    private ShortcutInfo closeShortcutsInfo;
    private List<Integer> configidList = new ArrayList<>();


    public static boolean isShortCuts() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1;
    }

    public static ShortcutsUtil get() {
        return Holder.INSTANCE;
    }


    public boolean haveShortcuts(Integer configId) {
        return configidList.contains(configId);
    }

    public List<Integer> getConfigidList() {
        return configidList;
    }

    public void showShortcuts() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            Context context = AppUtil.get().getApplication();
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            shortcutManager.removeAllDynamicShortcuts();

            Intent intentStart = new Intent(context, ShortcutsActivity.class);
            intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentStart.setAction(Intent.ACTION_VIEW);
            intentStart.putExtra("toggle", "open");

            openShortcutsInfo = new ShortcutInfo.Builder(context, "open")
                    .setShortLabel(StringUtil.getString(R.string.config_more_shortcuts_open))
                    .setLongLabel(StringUtil.getString(R.string.config_more_shortcuts_open_long))
                    .setIcon(Icon.createWithResource(context, R.mipmap.icon_widget_open_black))
                    .setIntent(intentStart)
                    .build();


            Intent intentEnd = new Intent(context, ShortcutsActivity.class);
            intentEnd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentEnd.setAction(Intent.ACTION_VIEW);
            intentEnd.putExtra("toggle", "close");

            closeShortcutsInfo = new ShortcutInfo.Builder(context, "close")
                    .setShortLabel(StringUtil.getString(R.string.config_more_shortcuts_close))
                    .setLongLabel(StringUtil.getString(R.string.config_more_shortcuts_close_long))
                    .setIcon(Icon.createWithResource(context, R.mipmap.icon_widget_close_black))
                    .setIntent(intentEnd)
                    .build();

            shortcutManager.addDynamicShortcuts(Arrays.asList(openShortcutsInfo, closeShortcutsInfo));
        }
    }


    public void addShortcutsInfo(Integer configId) {
        //判断有没有存在的
        configidList.remove(configId);
        configidList.add(configId);
        saveLocalData();
        showShortcuts();
    }

    public void removeShortcutsInfo(Integer configId) {
        configidList.remove(configId);
        saveLocalData();
        showShortcuts();
    }

    private void saveLocalData() {
        LocalDataUtil.get().saveData("shortcuts_configids", GsonUtils.toString(configidList));
    }

    private void reLoadLocalData() {
        String shortcutsConfigids = LocalDataUtil.get().getData("shortcuts_configids", "");
        if (TextUtils.isEmpty(shortcutsConfigids)) return;
        List<Integer> dataList = GsonUtils.toBeanArray(shortcutsConfigids, Integer.class);
        configidList.clear();
        configidList.addAll(dataList);
    }

    private ShortcutsUtil() {
        reLoadLocalData();
    }

    private static final class Holder {
        private static final ShortcutsUtil INSTANCE = new ShortcutsUtil();
    }

}
