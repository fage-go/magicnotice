package com.zhangshuo.magicnotice.barrage;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.AppUtil;
import com.zhangshuo.common.floatwindow.annotation.MoveType;
import com.zhangshuo.common.floatwindow.permission.FloatPermissionManager;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.ball.FloatBallHelper;
import com.zhangshuo.magicnotice.config.AppStatusConfig;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.GlobalConfig;
import com.zhangshuo.magicnotice.db.model.GlobalConfigData;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.service.GlobalConfigDataService;
import com.zhangshuo.magicnotice.db.service.NotificationDataService;
import com.zhangshuo.magicnotice.notice.NotificationHelper;
import com.zhangshuo.magicnotice.ui.model.BarrageGlobalConfig;
import com.zhangshuo.magicnotice.ui.model.behavior.BarrageBehaviorModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class BarrageHelper {
    //注意，同一个key的通知，进行规整，避免出现大量的弹幕内容
    private static final int MAX_PENDING_BARRAGES = 64;
    private List<BarrageInfo> barrageInfoList = new CopyOnWriteArrayList<>();
    private ArrayMap<Integer, BarrageWindow> barrageWindowMap = new ArrayMap<>();
    private BarrageGlobalConfig barrageGlobalConfig;
    private int barrageLineHeight = 40;
    private int offsetRight = 40;
    private Handler handler;


    public void addBarrageInfo(NotificationData notificationData, BarrageBehaviorModel behaviorModel) {
        String notificationKey = notificationData.getKey();
        //移除一样的key
        int sameKeyCount = 0;
        for (int i = 0; i < barrageInfoList.size(); i++) {
            if (notificationKey.equals(barrageInfoList.get(i).getNotificationData().getKey())) {
                sameKeyCount = barrageInfoList.remove(i).getCount();
                break;
            }
        }
        //添加到尾巴处
        BarrageInfo barrageInfo = new BarrageInfo(notificationData, behaviorModel);
        barrageInfo.setCount(sameKeyCount + 1);
        if (barrageInfoList.size() >= MAX_PENDING_BARRAGES) {
            barrageInfoList.remove(0);
            LogUtils.i("delivery phase=overlay_queue_capped");
        }
        barrageInfoList.add(barrageInfo);
        barrageLineHeight = UIUtil.dp2Px(behaviorModel.getTxtSizeSP() + 16);
        checkBarrageStatus();
    }

    public void removeBarrageInfo(String key) {
        if (TextUtils.isEmpty(key)) return;
        for (int i = 0; i < barrageInfoList.size(); i++) {
            BarrageInfo barrageInfo = barrageInfoList.get(i);
            String tmpKey = barrageInfo.getNotificationData().getKey();
            if (key.equals(tmpKey)) {
                barrageInfoList.remove(i);
                i--;
            }
        }
    }

    public void checkBarrageStatus() {
        boolean floatEnable = FloatPermissionManager.getInstance().checkPermission(AppUtil.get().getApplication());
        if (!floatEnable) {
            //没有开启
            EventBusUtil.sendEvent(new EventMessage<>(EventCodes.PERMISSION_FLOAT_CLOSED));
            return;
        }
        if (barrageInfoList.isEmpty()) return;
        checkBarrageView();
    }

    private void checkBarrageView() {
        boolean screenPresent = AppStatusConfig.get().isScreenUnPresent();
        LogUtils.i("checkBarrageView >> count: " + barrageInfoList.size() + " present: " + screenPresent);
        if (barrageInfoList.isEmpty() || !screenPresent) {
            //如果为空，或者当前锁屏，则不处理
            return;
        }
        //列表不为空
        while (!barrageInfoList.isEmpty()) {
            BarrageInfo barrageInfo = barrageInfoList.get(0);
            BarrageWindow barrageWindow = buildNewWindow();
            if (barrageWindow == null) {
                //没有可以放入的window
                break;
            }
            barrageInfoList.remove(0);
            barrageWindow.preConfigWindow(barrageInfo.getBehaviorModel());
            BarrageView barrageView = (BarrageView) barrageWindow.getView();
            barrageView.setBarrageInfo(barrageInfo);
            barrageWindow.show();
        }
    }


    public BarrageWindow buildNewWindow() {
        for (Map.Entry<Integer, BarrageWindow> entry : barrageWindowMap.entrySet()) {
            BarrageWindow barrageWindow = null;
            BarrageWindow.Builder builder = null;
            if (entry.getValue() != null && !entry.getValue().isShowing()) {
                //如果没有再展示了
                barrageWindow = entry.getValue();
                builder = barrageWindow.getBuilder();
            } else if (entry.getValue() == null) {
                String viewTag = "BarrageView_" + entry.getKey();
                builder = BarrageWindow.create()
                        .setTag(viewTag)
                        .setView(buildNewBarrageView(viewTag))
                        .setOnOptCallback(new BarrageWindow.OnOptCallback() {
                            @Override
                            public void onSingleClick(View view) {
                                if (view instanceof BarrageView) {
                                    try {
                                        BarrageView barrageView = (BarrageView) view;
                                        BarrageInfo barrageInfo = barrageView.getBarrageInfo();
                                        NotificationData notificationData = barrageInfo.getNotificationData();
                                        BarrageBehaviorModel behaviorModel = barrageInfo.getBehaviorModel();
                                        clickBarrage(view, behaviorModel.getClickType(), notificationData, behaviorModel);
                                        dismissBarrageWindow(((BarrageView) view).getViewTag());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onDoubleClick(View view) {
                                if (view instanceof BarrageView) {
                                    try {
                                        BarrageView barrageView = (BarrageView) view;
                                        BarrageInfo barrageInfo = barrageView.getBarrageInfo();
                                        NotificationData notificationData = barrageInfo.getNotificationData();
                                        BarrageBehaviorModel behaviorModel = barrageInfo.getBehaviorModel();
                                        clickBarrage(view, behaviorModel.getDoubleClickType(), notificationData, behaviorModel);
                                        dismissBarrageWindow(((BarrageView) view).getViewTag());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onMoveEnterAnimEnd(View view) {
                                LogUtils.i("checkBarrageView>>> onMoveExitAnimEnd");
                                BarrageView barrageView = (BarrageView) view;
                                barrageView.startCustomAnimate();
                            }

                            @Override
                            public void onMoveExitAnimEnd(View view) {
                                checkBarrageView();
                            }
                        })
                        .setX(getEndViewX())
                        .setY((UIUtil.dp2Px(8) + barrageLineHeight) * entry.getKey());
                barrageWindow = builder.build();
                barrageWindowMap.setValueAt(entry.getKey(), barrageWindow);
            }
            if (barrageWindow != null && builder != null) {
                if (builder.mView instanceof BarrageView) {
                    ((BarrageView) builder.mView).setMaxWidth(getMaxViewSize());
                }
                builder.setMoveType(MoveType.FIXED)
                        .setWidth(-2)
                        .setHeight(-2);
                return barrageWindow;
            }
        }
        return null;
    }

    /**
     * 点击Barrage
     *
     * @param view
     * @param clickType
     */
    private void clickBarrage(View view, int clickType, NotificationData notificationData, BarrageBehaviorModel behaviorModel) {
        if (clickType == BarrageBehaviorModel.TYPE_CLICK_MSG
                || clickType == BarrageBehaviorModel.TYPE_CLICK_REPLAY_FREE) {
            openNotification(view, clickType);
        } else if (clickType == BarrageBehaviorModel.TYPE_CLICK_COLLECT) {
            //收藏通知
            NotificationDataService.updateNotificationLock(notificationData.getId(), 1, null);
            Toast.makeText(view.getContext(), R.string.toast_collect_suc, Toast.LENGTH_SHORT).show();
        } else if (clickType == BarrageBehaviorModel.TYPE_CLICK_MSG_BALL) {
            //消息球点击
            FloatBallHelper.get().addFloatBall(notificationData, behaviorModel);
        }
    }

    private void openNotification(View view, int clickType) {
        BarrageView barrageView = (BarrageView) view;
        BarrageInfo barrageInfo = barrageView.getBarrageInfo();
        if (barrageInfo == null || barrageInfo.getNotificationData() == null) {
            return;
        }
        NotificationHelper.get().openApp(barrageInfo.getNotificationData(), clickType);
    }

    private void dismissBarrageWindow(String viewTag) {
        for (Map.Entry<Integer, BarrageWindow> entry : barrageWindowMap.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue().getViewTag().equals(viewTag)) {
                    entry.getValue().dismiss();
                    break;
                }
            }
        }
    }

    private BarrageView buildNewBarrageView(String viewTag) {
        BarrageView barrageView = new BarrageView(AppUtil.get().getApplication(), viewTag);
        barrageView.setOnAnimCallback(new BarrageView.OnAnimCallback() {
            @Override
            public void onStart(BarrageView view) {

            }

            @Override
            public void onEnd(BarrageView view) {
                LogUtils.i("checkBarrageView>>> BarrageView MoveEnd");
                String viewTag = view.getViewTag();
                dismissBarrageWindow(viewTag);
            }
        });
        return barrageView;
    }


    private void initBarrageWindowPosition() {
        GlobalConfigDataService.getGlobalConfig(GlobalConfig.KEY_BARRAGE, new AsyncCallback<GlobalConfigData>() {
            @Override
            public void onSuccess(GlobalConfigData result) {
                BarrageGlobalConfig tmpGlobalConfig = null;
                if (result != null) {
                    String value = result.getValue();
                    tmpGlobalConfig = GsonUtils.toBean(value, BarrageGlobalConfig.class);
                }
                if (tmpGlobalConfig == null) {
                    tmpGlobalConfig = BarrageGlobalConfig.getDefault();
                }
                setBarrageGlobalConfig(tmpGlobalConfig);
                checkBarrageStatus();
            }

            @Override
            public void onError(Throwable t) {
                BarrageGlobalConfig tmpGlobalConfig = BarrageGlobalConfig.getDefault();
                setBarrageGlobalConfig(tmpGlobalConfig);
                checkBarrageStatus();
            }
        });
    }

    public BarrageGlobalConfig getBarrageGlobalConfig() {
        if (barrageGlobalConfig == null) {
            barrageGlobalConfig = BarrageGlobalConfig.getDefault();
        }
        return barrageGlobalConfig;
    }

    public void setBarrageGlobalConfig(BarrageGlobalConfig barrageGlobalConfig) {
        this.barrageGlobalConfig = barrageGlobalConfig;
        barrageWindowMap.clear();
        for (int i = 0; i < this.barrageGlobalConfig.getMaxLines(); i++) {
            barrageWindowMap.put(i, null);
        }
    }

    public int getMaxViewSize() {
        return ScreenUtils.getScreenWidth() - offsetRight;
    }

    public int getEndViewX() {
        return offsetRight / 4;
    }

    public Handler getHandler() {
        return handler;
    }

    public static BarrageHelper get() {
        return Holder.instance;
    }

    public void init() {

    }

    private BarrageHelper() {
        barrageLineHeight = UIUtil.dp2Px(40);
        offsetRight = UIUtil.dp2Px(40);
        initBarrageWindowPosition();
        handler = new Handler(Looper.getMainLooper());
    }

    private static final class Holder {
        private static final BarrageHelper instance = new BarrageHelper();
    }
}
