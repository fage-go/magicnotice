package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import androidx.recyclerview.widget.GridLayoutManager;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.util.LogUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCbShockBinding;
import com.zhangshuo.magicnotice.ui.adapter.TxtSelectBehaviorAdapter;
import com.zhangshuo.magicnotice.ui.model.TxtSelectedInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.ShockBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.StepSeekBar;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

import java.util.Arrays;
import java.util.List;

public class ShockCBFragment extends BaseFragment<FragmentCbShockBinding>
        implements IConfigRule {
    private TxtSelectBehaviorAdapter adapter;

    private ShockBehaviorModel shockBehaviorModel;

    @Override
    protected void initView() {
        binding.rvShock.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new TxtSelectBehaviorAdapter(getContext());
        binding.rvShock.setAdapter(adapter);
        adapter.setOnItemSelectCallback(new TxtSelectBehaviorAdapter.OnItemSelectCallback() {
            @Override
            public void onSelected(int position) {
                TxtSelectedInfo item = adapter.getItem(position);
                shockBehaviorModel.setName(item.getName());
                shockBehaviorModel.setTimePattern(item.getValue());
                shockBehaviorModel.exec(null);
            }
        });

        binding.tvShockCount.getPaint().setFakeBoldText(true);
        binding.tvShockCount.setText(String.format(StringUtil.getString(R.string.cb_count_tip), shockBehaviorModel.getCount()));
        binding.stepSeekCount.setProgress(shockBehaviorModel.getCount() - 1);
        binding.stepSeekCount.setMaxProgress(5);
        binding.stepSeekCount.invalidate();
        binding.stepSeekCount.setOnProgressCallback(new StepSeekBar.OnProgressCallback() {
            @Override
            public void onProgress(int maxProgress, int progress) {
                shockBehaviorModel.setCount(progress + 1);
                binding.tvShockCount.setText(String.format(StringUtil.getString(R.string.cb_count_tip), shockBehaviorModel.getCount()));
                LogUtils.i("onProgress >> " + maxProgress + " >> " + progress);
            }
        });
    }

    @Override
    protected void initData() {
        List<TxtSelectedInfo> dataList = Arrays.asList(
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_1), "0;300;100;300", shockBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_2), "0;2000", shockBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_3), "0;100", shockBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_4), "0;300;200;100;50;50", shockBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_5), "0;150;50;150", shockBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_6), "0;100;50;100;50;100;50;100", shockBehaviorModel.getTimePattern()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.shock_behavior_7), "0;100;100;100;200;200;100;200", shockBehaviorModel.getTimePattern())
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
        if (object instanceof ShockBehaviorModel) {
            shockBehaviorModel = (ShockBehaviorModel) object;
        }
        if (shockBehaviorModel == null) {
            shockBehaviorModel = new ShockBehaviorModel();
        }
    }

    @Override
    public Object onResultData() {
        return shockBehaviorModel;
    }
}
