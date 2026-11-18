package com.zhangshuo.magicnotice.ui.view.configrule.conditions;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zhangshuo.common.base.BaseFragment;
import com.zhangshuo.common.base.BaseRAdapter;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.databinding.FragmentCrTxtBinding;
import com.zhangshuo.magicnotice.ui.adapter.TagAdapter;
import com.zhangshuo.magicnotice.ui.dialog.TxtConditionsSelectPopup;
import com.zhangshuo.magicnotice.ui.model.conditions.TxtConditionsModel;
import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;
import com.zhangshuo.common.util.UIUtil;

import java.util.Arrays;

public class TxtCRFragment extends BaseFragment<FragmentCrTxtBinding>
        implements IConfigRule {

    private TxtConditionsModel txtConditionsModel;
    private TagAdapter tagAdapter;
    private TxtConditionsSelectPopup selectPopup;
    private String judgeCode = "A-*";

    @Override
    protected void initView() {
        binding.tvTagAdd.getPaint().setFakeBoldText(true);
        binding.tvConditions.getPaint().setFakeBoldText(true);
        binding.rvTag.setLayoutManager(new FlexboxLayoutManager(getContext(), FlexDirection.ROW, FlexWrap.WRAP));
        tagAdapter = new TagAdapter(getContext());
        binding.rvTag.setAdapter(tagAdapter);
        selectPopup = new TxtConditionsSelectPopup(getContext(), new TxtConditionsSelectPopup.OnCallback() {
            @Override
            public void onSelect(String name, String code) {
                binding.tvConditions.setText(name);
                judgeCode = code;
            }
        });
        tagAdapter.setOnItemClickListener(new BaseRAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                tagAdapter.removeItem(position);
                afterItemChanged();
            }
        });
        binding.tvTagAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSelectPopup();
                String text = binding.etTag.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    tagAdapter.addData(Arrays.asList(judgeCode + ":" + text));
                    afterItemChanged();
                } else {
                    ToastManager.get().noticeShortMessage(getContext().getString(R.string.txt_condition_not_dist));
                }
                binding.etTag.setText("");
            }
        });
        binding.tvConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPopup.isShowing()) {
                    hideSelectPopup();
                } else {
                    showSelectPopup();
                }
            }
        });
    }

    @Override
    protected void initData() {
        binding.filterConfig.setConfig(txtConditionsModel.isFilterType(),
                R.string.c_filter_tip_txt_y, R.string.c_filter_tip_txt_n);
        if (!StringUtil.isEmpty(txtConditionsModel.getTxtList())) {
            tagAdapter.addData(txtConditionsModel.getTxtList());
            afterItemChanged();
        }
    }

    private void afterItemChanged() {
        if (txtConditionsModel != null) {
            txtConditionsModel.setTxtList(tagAdapter.getDataList());
        }
    }


    private void showSelectPopup() {
        if (selectPopup == null) return;
        if (!selectPopup.isShowing()) {
            selectPopup.haveSelect(judgeCode);
            selectPopup.showAsDropDown(binding.llConditions, 0, -UIUtil.dp2Px(150), Gravity.LEFT | Gravity.TOP);
            binding.ivConditions.animate().rotation(270).start();
        }
    }

    private void hideSelectPopup() {
        if (selectPopup.isShowing()) {
            selectPopup.dismiss();
            binding.ivConditions.animate().rotation(90).start();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideSelectPopup();
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void onInitData(Object object) {
        if (object instanceof TxtConditionsModel) {
            txtConditionsModel = (TxtConditionsModel) object;
        }
        if (txtConditionsModel == null) {
            txtConditionsModel = new TxtConditionsModel();
        }
    }

    @Override
    public Object onResultData() {
        txtConditionsModel.setFilterType(binding.filterConfig.isChecked());
        return txtConditionsModel;
    }
}
