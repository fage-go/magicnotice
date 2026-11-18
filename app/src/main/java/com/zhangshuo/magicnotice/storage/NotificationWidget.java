package com.zhangshuo.magicnotice.storage;

import android.appwidget.AppWidgetProvider;

public class NotificationWidget extends AppWidgetProvider {
    public static final String ACTION = "com.zhangshuo.magicnotice.widget.UPDATE_ALL";

//    private void showWidgetTipDialog() {
//        AppWidgetManager appWidgetManager = (AppWidgetManager) getSystemService(Context.APPWIDGET_SERVICE);
//        boolean support = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager.isRequestPinAppWidgetSupported();
//        StringBuilder tipBuilder = new StringBuilder();
//        if (support) {
//            tipBuilder.append("当前支持一键添加桌面小部件功能～\n也");
//        }
//        tipBuilder.append("可以在桌面中，自行添加桌面「 通知栏小部件 」");
//        CommonDialog.newInstance()
//                .setContent(tipBuilder.toString())
//                .setLeftAction(support ? "自行添加" : "我知道了")
//                .setAutoClose(true)
//                .setCommonAction(support ? "一键添加" : null)
//                .setCommonActionListener(support ? new CommonDialog.ActionListener() {
//                    @Override
//                    public void onClick(CommonDialog dialog) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            ComponentName componentName = new ComponentName(getCtx(), NotificationWidget.class);
//                            Intent intent = new Intent();
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getCtx(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                            appWidgetManager.requestPinAppWidget(componentName, null, pendingIntent);
//                        }
//                    }
//                } : null)
//                .show(this);
//    }

}
