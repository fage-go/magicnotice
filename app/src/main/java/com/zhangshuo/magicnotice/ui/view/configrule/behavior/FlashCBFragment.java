package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import androidx.recyclerview.widget.GridLayoutManager;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCbFlashBinding;
import com.zhangshuo.magicnotice.ui.adapter.TxtSelectBehaviorAdapter;
import com.zhangshuo.magicnotice.ui.model.TxtSelectedInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.FlashLightBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.StepSeekBar;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

import java.util.Arrays;
import java.util.List;

public class FlashCBFragment extends BaseFragment<FragmentCbFlashBinding>
        implements IConfigRule {

    private TxtSelectBehaviorAdapter adapter;
    private FlashLightBehaviorModel flashLightBehaviorModel;

    @Override
    protected void initView() {
        binding.rvFlash.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new TxtSelectBehaviorAdapter(getContext());
        binding.rvFlash.setAdapter(adapter);
        adapter.setOnItemSelectCallback(new TxtSelectBehaviorAdapter.OnItemSelectCallback() {
            @Override
            public void onSelected(int position) {
                TxtSelectedInfo item = adapter.getItem(position);
                flashLightBehaviorModel.setName(item.getName());
                flashLightBehaviorModel.setTimePattern(item.getValue());
                flashLightBehaviorModel.exec(null);
            }
        });
        binding.tvFlashCount.getPaint().setFakeBoldText(true);
        binding.tvFlashCount.setText(String.format(StringUtil.getString(R.string.cb_count_tip), flashLightBehaviorModel.getCount()));
        binding.stepSeekCount.setProgress(flashLightBehaviorModel.getCount() - 1);
        binding.stepSeekCount.setMaxProgress(5);
        binding.stepSeekCount.invalidate();
        binding.stepSeekCount.setOnProgressCallback(new StepSeekBar.OnProgressCallback() {
            @Override
            public void onProgress(int maxProgress, int progress) {
                flashLightBehaviorModel.setCount(progress + 1);
                binding.tvFlashCount.setText(String.format(StringUtil.getString(R.string.cb_count_tip), flashLightBehaviorModel.getCount()));
            }
        });
    }

    @Override
    protected void initData() {
        List<TxtSelectedInfo> dataList = Arrays.asList(
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_1), "200;200;200;200", flashLightBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_2), "1000;200", flashLightBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_3), "150;200", flashLightBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_4), "300;200;100;100;50;200", flashLightBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_5), "150;50;150;200", flashLightBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_6), "100;50;100;50;100;200", flashLightBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_7), "100;100;200;100;200;100;200;200", flashLightBehaviorModel.getTimePattern())
        );
        adapter.getDataList().clear();
        adapter.getDataList().addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof FlashLightBehaviorModel) {
            flashLightBehaviorModel = (FlashLightBehaviorModel) object;
        }
        if (flashLightBehaviorModel == null) {
            flashLightBehaviorModel = new FlashLightBehaviorModel();
        }
    }

    @Override
    public Object onResultData() {
        return flashLightBehaviorModel;
    }
}
