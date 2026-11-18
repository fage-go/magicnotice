package com.zhangshuo.magicnotice.ui.activity;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ScreenUtils;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.floatwindow.FloatWindow;
import com.zhangshuo.common.floatwindow.annotation.MoveType;
import com.zhangshuo.common.floatwindow.impl.ViewStateListenerImpl;
import com.zhangshuo.common.floatwindow.interfaces.BaseFloatWindow;
import com.zhangshuo.common.floatwindow.permission.FloatPermissionManager;
import com.zhangshuo.common.model.EventMessage;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.EventBusUtil;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.barrage.BarrageHelper;
import com.zhangshuo.magicnotice.config.EventCodes;
import com.zhangshuo.magicnotice.config.GlobalConfig;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityConfigBarrageBinding;
import com.zhangshuo.magicnotice.db.model.GlobalConfigData;
import com.zhangshuo.magicnotice.db.service.GlobalConfigDataService;
import com.zhangshuo.magicnotice.ui.model.BarrageGlobalConfig;
import com.zhangshuo.magicnotice.ui.view.StepSeekBar;
import com.zhangshuo.common.util.UIUtil;

@Route(path = RoutePaths.APP_ACTIVITY_CONFIG_BARRAGE)
public class ConfigBarrageActivity extends BaseActivity<ActivityConfigBarrageBinding> {

    private BarrageGlobalConfig barrageGlobalConfig;
    private int boundSize;
    private int screenWidth;
    private int screenHeight;
    private static final String WINDOW_TAG = "Barrage_WINDOW";

    @Override
    protected void initView() {
        setTitleBar(getString(R.string.barrage_global_title));
        binding.barrageStepNum.setMaxProgress(5);
        binding.flPScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLandOrientation()) {
                    ToastManager.get().noticeShortMessage(R.string.config_barrage_screen_v);
                } else {
                    showWindowBarrage();
                }
            }
        });
        binding.flHScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLandOrientation()) {
                    showWindowBarrage();
                } else {
                    ToastManager.get().noticeShortMessage(R.string.config_barrage_screen_h);
                }
            }
        });
    }

    @Override
    protected void initData() {
        boundSize = UIUtil.dp2Px(200);
        screenWidth = ScreenUtils.getScreenWidth();
        screenHeight = ScreenUtils.getScreenHeight();
        GlobalConfigDataService.getGlobalConfig(GlobalConfig.KEY_BARRAGE, new AsyncCallback<GlobalConfigData>() {
            @Override
            public void onSuccess(GlobalConfigData result) {
                if (result != null) {
                    String value = result.getValue();
                    barrageGlobalConfig = GsonUtils.toBean(value, BarrageGlobalConfig.class);
                }
                if (barrageGlobalConfig == null) {
                    barrageGlobalConfig = BarrageGlobalConfig.getDefault();
                }
                initConfigShow();
            }

            @Override
            public void onError(Throwable t) {
                barrageGlobalConfig = BarrageGlobalConfig.getDefault();
                initConfigShow();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideWindowBarrage();
    }

    private void saveConfig() {
        if (barrageGlobalConfig == null) {
            barrageGlobalConfig = BarrageGlobalConfig.getDefault();
        }
        GlobalConfigDataService.saveOrUpdate(GlobalConfig.KEY_BARRAGE, GsonUtils.toString(barrageGlobalConfig), new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                BarrageHelper.get().setBarrageGlobalConfig(barrageGlobalConfig);
                EventBusUtil.sendEvent(new EventMessage<>(EventCodes.REFRESH_BARRAGE_GLOBAL));
                finishAct();
            }
        });
    }

    private void showWindowBarrage() {
        boolean result = FloatPermissionManager.getInstance().applyFloatWindow(this, true);
        if (!result) return;
        BaseFloatWindow baseFloatWindow = FloatWindow.get(WINDOW_TAG);
        if (baseFloatWindow != null && baseFloatWindow.isShowing()) {
            return;
        }
        if (barrageGlobalConfig == null) {
            barrageGlobalConfig = BarrageGlobalConfig.getDefault();
        }
        int height = (UIUtil.dp2Px(40) + UIUtil.dp2Px(8)) * barrageGlobalConfig.getMaxLines() - UIUtil.dp2Px(8);
        boolean isLand = isLandOrientation();
        View view = LayoutInflater.from(getCtx()).inflate(R.layout.window_view_barrage, null);
        ImageView icSave = view.findViewById(R.id.iv_save);
        icSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideWindowBarrage();
            }
        });
        FloatWindow.with()
                .setY(isLand ? barrageGlobalConfig.getHTop() : barrageGlobalConfig.getPTop())
                .setX(0)
                .setTag(WINDOW_TAG)
                .setMoveType(MoveType.SLIDE)
                .setWidth(-1)
                .setHeight(height)
                .setView(view)
                .setViewStateListener(new ViewStateListenerImpl() {
                    @Override
                    public void onPositionUpdate(int x, int y) {
                        super.onPositionUpdate(x, y);
                        if (isLand) {
                            barrageGlobalConfig.setHTop(y);
                        } else {
                            barrageGlobalConfig.setPTop(y);
                        }
                        setBarrageMargin();
                    }

                    @Override
                    public void onDismiss() {
                        super.onDismiss();
                        saveConfig();
                    }
                })
                .build();

        FloatWindow.get(WINDOW_TAG).show();
    }

    private void hideWindowBarrage() {
        BaseFloatWindow baseFloatWindow = FloatWindow.get(WINDOW_TAG);
        if (baseFloatWindow != null) {
            FloatWindow.destroy(WINDOW_TAG);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.getLayoutDirection() == Configuration.ORIENTATION_PORTRAIT
                || newConfig.getLayoutDirection() == Configuration.ORIENTATION_UNDEFINED
                || newConfig.getLayoutDirection() == Configuration.ORIENTATION_LANDSCAPE) {
            boolean isLand = isLandOrientation();
            hideWindowBarrage();
            binding.llBarrageShow.setOrientation(isLand ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        }
    }

    private boolean isLandOrientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void initConfigShow() {
        if (barrageGlobalConfig == null)
            return;
        binding.barrageStepNum.setProgress(barrageGlobalConfig.getMaxLines() - 1);
        binding.barrageStepNum.invalidate();
        binding.barrageStepNum.setOnProgressCallback(new StepSeekBar.OnProgressCallback() {
            @Override
            public void onProgress(int maxProgress, int progress) {
                if (barrageGlobalConfig != null) {
                    barrageGlobalConfig.setMaxLines(progress + 1);
                    setBarrageSize();
                }
            }
        });
        //获取当前的屏幕尺寸

        float whRate = screenWidth * 1f / screenHeight;

        ViewGroup.LayoutParams pLP = binding.flPScreen.getLayoutParams();
        pLP.width = (int) (boundSize * whRate);
        pLP.height = boundSize;
        binding.flPScreen.setLayoutParams(pLP);

        ViewGroup.LayoutParams hLP = binding.flHScreen.getLayoutParams();
        hLP.width = boundSize;
        hLP.height = (int) (boundSize * whRate);
        binding.flHScreen.setLayoutParams(hLP);
        setBarrageSize();
        setBarrageMargin();
    }

    private void setBarrageSize() {
        //等比缩放弹幕尺寸
        int lineHeight = (int) (UIUtil.dp2Px(40) * (boundSize * 1f / screenHeight));
        int lineSpaceHeight = (int) (UIUtil.dp2Px(8) * (boundSize * 1f / screenHeight));
        FrameLayout.LayoutParams pBLP = (FrameLayout.LayoutParams) binding.llPBarrage.getLayoutParams();
        pBLP.width = -1;
        pBLP.height = (lineHeight + lineSpaceHeight) * barrageGlobalConfig.getMaxLines() - lineSpaceHeight;
        binding.llPBarrage.setLayoutParams(pBLP);

        FrameLayout.LayoutParams hBLP = (FrameLayout.LayoutParams) binding.llHBarrage.getLayoutParams();
        hBLP.width = -1;
        hBLP.height = (lineHeight + lineSpaceHeight) * barrageGlobalConfig.getMaxLines() - lineSpaceHeight;
        binding.llHBarrage.setLayoutParams(hBLP);
    }

    private void setBarrageMargin() {
        FrameLayout.LayoutParams flPLp = (FrameLayout.LayoutParams) binding.llPBarrage.getLayoutParams();
        flPLp.topMargin = (int) (barrageGlobalConfig.getPTop() * (boundSize * 1f / screenHeight));
        binding.llPBarrage.setLayoutParams(flPLp);

        FrameLayout.LayoutParams flHLp = (FrameLayout.LayoutParams) binding.llHBarrage.getLayoutParams();
        flHLp.topMargin = (int) (barrageGlobalConfig.getHTop() * (boundSize * 1f / screenHeight));
        binding.llHBarrage.setLayoutParams(flHLp);
    }
}
