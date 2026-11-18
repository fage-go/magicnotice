package com.zhangshuo.magicnotice.ui.view.configrule.behavior;

import androidx.recyclerview.widget.GridLayoutManager;

import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCbCopyBinding;
import com.zhangshuo.magicnotice.ui.adapter.TxtSelectBehaviorAdapter;
import com.zhangshuo.magicnotice.ui.model.TxtSelectedInfo;
import com.zhangshuo.magicnotice.ui.model.behavior.CopyBehaviorModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

import java.util.Arrays;
import java.util.List;

public class CopyCBFragment extends BaseFragment<FragmentCbCopyBinding>
        implements IConfigRule {
    private CopyBehaviorModel copyBehaviorModel;

    private TxtSelectBehaviorAdapter adapter;

    //(?<![a-zA-Z0-9])([a-zA-Z0-9]{4})(?![a-zA-Z0-9])

    @Override
    protected void initView() {
        binding.rvRules.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new TxtSelectBehaviorAdapter(getContext());
        binding.rvRules.setAdapter(adapter);
        adapter.setOnItemSelectCallback(new TxtSelectBehaviorAdapter.OnItemSelectCallback() {
            @Override
            public void onSelected(int position) {
                TxtSelectedInfo item = adapter.getItem(position);
                copyBehaviorModel.setName(item.getName());
                copyBehaviorModel.setRegular(item.getValue());
            }
        });
    }

    @Override
    protected void initData() {
        List<TxtSelectedInfo> dataList = Arrays.asList(
                TxtSelectedInfo.create(StringUtil.getString(R.string.copy_behavior_num), "([0-9]+)", copyBehaviorModel.getRegular()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.copy_behavior_eng), "([A-z]+)", copyBehaviorModel.getRegular()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.copy_behavior_all), "()", copyBehaviorModel.getRegular()),
                TxtSelectedInfo.create(StringUtil.getString(R.string.copy_behavior_http), "([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://|[wW]{3}.|[wW][aA][pP].|[fF][tT][pP].|[fF][iI][lL][eE].)[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]", copyBehaviorModel.getRegular())
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
        if (object instanceof CopyBehaviorModel) {
            copyBehaviorModel = (CopyBehaviorModel) object;
        }
        if (copyBehaviorModel == null) {
            copyBehaviorModel = new CopyBehaviorModel();
        }
    }

    @Override
    public Object onResultData() {
        return copyBehaviorModel;
    }
}
