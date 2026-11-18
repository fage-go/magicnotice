package com.zhangshuo.magicnotice.ui.activity;

import android.graphics.RectF;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.zhangshuo.common.base.BaseActivity;
import com.zhangshuo.common.listener.OnClickCallback;
import com.zhangshuo.common.util.UIUtil;
import com.zhangshuo.magicnotice.config.RoutePaths;
import com.zhangshuo.magicnotice.databinding.ActivityCutoutLightBinding;
import com.zhangshuo.magicnotice.led.LightUtil;
import com.zhangshuo.magicnotice.led.model.CutoutInfo;
import com.zhangshuo.magicnotice.ui.view.RockerView;
import com.zhangshuo.magicnotice.ui.view.ShowLightView;
import com.zhangshuo.magicnotice.ui.view.StepSeekBar;

@Route(path = RoutePaths.APP_ACTIVITY_CUTOUT_LIGHT)
public class CutoutLightActivity extends BaseActivity<ActivityCutoutLightBinding> {
    @Override
    protected void initView() {
        CutoutInfo cutoutInfo = LightUtil.getInstance().getCutoutInfo();
        if (cutoutInfo != null) {
            binding.cutoutShowLight.setRect(new RectF(cutoutInfo.getLeft(), cutoutInfo.getTop(), cutoutInfo.getRight(), cutoutInfo.getBottom()));
            binding.cutoutShowLight.setRadius(cutoutInfo.getRadius());
        }

        binding.cutoutRockerLeft.setDirection(RockerView.DIRECTION_HORIZONTAL);
        binding.cutoutRockerLeft.setOnCallback(new RockerView.OnCallback() {
            @Override
            public void onSelect(boolean select) {
                binding.cutoutShowLight.setDirection(select ? ShowLightView.DIRECTION_LEFT : ShowLightView.DIRECTION_NONE);
            }

            @Override
            public void onMove(float x, float y) {
                binding.cutoutShowLight.onMove(x, y);
            }
        });
        binding.cutoutRockerTop.setDirection(RockerView.DIRECTION_VERTICAL);
        binding.cutoutRockerTop.setOnCallback(new RockerView.OnCallback() {
            @Override
            public void onSelect(boolean select) {
                binding.cutoutShowLight.setDirection(select ? ShowLightView.DIRECTION_TOP : ShowLightView.DIRECTION_NONE);
            }

            @Override
            public void onMove(float x, float y) {
                binding.cutoutShowLight.onMove(x, y);
            }
        });
        binding.cutoutRockerRight.setDirection(RockerView.DIRECTION_HORIZONTAL);
        binding.cutoutRockerRight.setOnCallback(new RockerView.OnCallback() {
            @Override
            public void onSelect(boolean select) {
                binding.cutoutShowLight.setDirection(select ? ShowLightView.DIRECTION_RIGHT : ShowLightView.DIRECTION_NONE);
            }

            @Override
            public void onMove(float x, float y) {
                binding.cutoutShowLight.onMove(x, y);
            }
        });
        binding.cutoutRockerBottom.setDirection(RockerView.DIRECTION_VERTICAL);
        binding.cutoutRockerBottom.setOnCallback(new RockerView.OnCallback() {
            @Override
            public void onSelect(boolean select) {
                binding.cutoutShowLight.setDirection(select ? ShowLightView.DIRECTION_BOTTOM : ShowLightView.DIRECTION_NONE);
            }

            @Override
            public void onMove(float x, float y) {
                binding.cutoutShowLight.onMove(x, y);
            }
        });
        int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
        int maxProgress = statusBarHeight / 4 + 1;
        binding.cutoutSsbRaidus.setMaxProgress(maxProgress);
        if (cutoutInfo != null)
            binding.cutoutSsbRaidus.setProgress(cutoutInfo.getRadius());
        binding.cutoutSsbRaidus.setOnProgressCallback(new StepSeekBar.OnProgressCallback() {
            @Override
            public void onProgress(int maxProgress, int progress) {
                binding.cutoutShowLight.setRadius(progress * UIUtil.dp2Px(2));
            }
        });
        binding.btnCancel.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                LightUtil.getInstance().updateCutoutInfo(new CutoutInfo());
                finishAct();
            }
        });
        binding.btnConfirm.setOnClickListener(new OnClickCallback() {
            @Override
            public void click(View v) {
                CutoutInfo cutoutInfo = new CutoutInfo();
                cutoutInfo.setLeft((int) binding.cutoutShowLight.getRect().left);
                cutoutInfo.setTop((int) binding.cutoutShowLight.getRect().top);
                cutoutInfo.setRight((int) binding.cutoutShowLight.getRect().right);
                cutoutInfo.setBottom((int) binding.cutoutShowLight.getRect().bottom);
                cutoutInfo.setRadius((int) binding.cutoutShowLight.getRadius());

                LightUtil.getInstance().updateCutoutInfo(cutoutInfo);
                finishAct();
            }
        });
    }

    @Override
    protected void initData() {
    }
}
