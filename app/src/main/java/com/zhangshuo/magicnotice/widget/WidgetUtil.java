package com.zhangshuo.magicnotice.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.widget.model.WidgetInfo;

import java.util.ArrayList;
import java.util.List;

public class WidgetUtil {
    public static final String ACTION = "com.zhangshuo.magicnotice.widget.UPDATE_ALL";
    public static final String ACTION_CLICK = "com.zhangshuo.magicnotice.widget.CLICK";

    private List<WidgetInfo> widgetInfoList = new ArrayList<>();

    @Nullable
    public WidgetInfo getWidgetInfo(int appWidgetId) {
        for (WidgetInfo widgetInfo : widgetInfoList) {
            if (appWidgetId == widgetInfo.getAppWidgetId()) {
                return widgetInfo;
            }
        }
        return null;
    }

    public void addWidgetInfo(int appWidget, WidgetInfo widgetInfo) {
        widgetInfo.setAppWidgetId(appWidget);
        widgetInfoList.add(widgetInfo);
        LocalDataUtil.get().saveData("WidgetInfoList", GsonUtils.toString(widgetInfoList));
    }

    public static boolean showWidget(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppWidgetManager mAppWidgetManager = context.getSystemService(AppWidgetManager.class);
            if (mAppWidgetManager == null) return false;
            return mAppWidgetManager.isRequestPinAppWidgetSupported();
        }
        return false;
    }

    public void addWidget(Context context, WidgetInfo widgetInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppWidgetManager mAppWidgetManager = context.getSystemService(AppWidgetManager.class);

            ComponentName myProvider = new ComponentName(context, SwitchConfigWidgetProvider.class);

            if (mAppWidgetManager.isRequestPinAppWidgetSupported()) {
                Intent pinnedWidgetCallbackIntent = new Intent(context, SwitchConfigWidgetProvider.class);
                pinnedWidgetCallbackIntent.setAction(ACTION);

                Bundle bundle = new Bundle();
                bundle.putString("WidgetInfo", GsonUtils.toString(widgetInfo));
                pinnedWidgetCallbackIntent.putExtras(bundle);

                PendingIntent successCallback = PendingIntent.getBroadcast(context, widgetInfo.getConfigId(),
                        pinnedWidgetCallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mAppWidgetManager.requestPinAppWidget(myProvider, null, successCallback);
            }
        }
    }

    public void refresh(List<ConfigRuleModel> result) {
        for (WidgetInfo widgetInfo : widgetInfoList) {
            for (ConfigRuleModel configRuleModel : result) {
                if (widgetInfo.getConfigId() == configRuleModel.getId()) {

                    if (widgetInfo.isConfigToggle() == configRuleModel.isOpen()
                            && widgetInfo.getConfigName().equalsIgnoreCase(configRuleModel.getRuleName()))
                        break;

                    widgetInfo.setConfigToggle(configRuleModel.isOpen());
                    widgetInfo.setConfigName(configRuleModel.getRuleName());

                    Intent intent = new Intent(AppUtil.get().getApplication(), SwitchConfigWidgetProvider.class);
                    intent.setAction(ACTION_CLICK);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetInfo.getAppWidgetId());
                    AppUtil.get().getApplication().sendBroadcast(intent);
                }
            }
        }
    }

    private void reloadCache() {
        String widgetInfoData = LocalDataUtil.get().getData("WidgetInfoList", "");
        if (TextUtils.isEmpty(widgetInfoData)) return;
        widgetInfoList = GsonUtils.toBeanArray(widgetInfoData, WidgetInfo.class);
    }


    public static WidgetUtil get() {
        return Holder.INSTANCE;
    }

    private WidgetUtil() {
        reloadCache();
    }

    private static final class Holder {
        private static final WidgetUtil INSTANCE = new WidgetUtil();
    }
}
