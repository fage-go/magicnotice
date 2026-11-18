package com.zhangshuo.magicnotice.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.db.service.ConfigRuleDataService;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.widget.model.WidgetInfo;

/**
 * Implementation of App Widget functionality.
 */
public class SwitchConfigWidgetProvider extends AppWidgetProvider {


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.config_switch_widget);
        WidgetInfo widgetInfo = WidgetUtil.get().getWidgetInfo(appWidgetId);
        if (widgetInfo != null) {
            views.setTextViewText(R.id.appwidget_text, widgetInfo.getConfigName());
            views.setViewVisibility(R.id.appwidget_toggle, View.VISIBLE);
            views.setImageViewResource(R.id.appwidget_toggle, widgetInfo.isConfigToggle() ? R.mipmap.icon_widget_open : R.mipmap.icon_widget_close);
        } else {
            views.setTextViewText(R.id.appwidget_text, context.getString(R.string.appwidget_text));
            views.setViewVisibility(R.id.appwidget_toggle, android.view.View.GONE);
        }
        Intent intent = new Intent(context, SwitchConfigWidgetProvider.class);
        intent.setAction(WidgetUtil.ACTION_CLICK);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1000+appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.appwidget_rl, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


        if (WidgetUtil.ACTION.equalsIgnoreCase(intent.getAction())) {
            int appId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            String widgetInfoStr = intent.getStringExtra("WidgetInfo");
            if (TextUtils.isEmpty(widgetInfoStr)) return;
            WidgetInfo widgetInfo = GsonUtils.convert(widgetInfoStr, WidgetInfo.class);
            WidgetUtil.get().addWidgetInfo(appId,widgetInfo);
            updateAppWidget(context, AppWidgetManager.getInstance(context), appId);
        } else if (WidgetUtil.ACTION_CLICK.equalsIgnoreCase(intent.getAction())) {
            int appId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            WidgetInfo widgetInfo = WidgetUtil.get().getWidgetInfo(appId);
            if (widgetInfo == null) return;
            widgetInfo.setConfigToggle(!widgetInfo.isConfigToggle());
            updateAppWidget(context, AppWidgetManager.getInstance(context), appId);
            ConfigRuleDataService.findById(widgetInfo.getConfigId(), new AsyncCallback<ConfigRuleModel>() {
                @Override
                public void onSuccess(ConfigRuleModel result) {
                    if (result == null) return;
                    result.setOpen(widgetInfo.isConfigToggle());
                    ConfigRuleDataService.insertOrUpdate(result, new AsyncCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            EventBusUtil.sendEvent(new EventMessage(EventCodes.UPDATE_RULE));
                        }
                    });
                }
            });
        }
    }
}