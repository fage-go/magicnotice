package com.zhangshuo.magicnotice.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zhangshuo.common.dialog.BaseBottomDialog;
import com.zhangshuo.common.pick.dataset.OptionDataSet;
import com.zhangshuo.common.toast.ToastManager;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.databinding.DialogTabConfigBinding;
import com.zhangshuo.magicnotice.db.service.TabDataService;
import com.zhangshuo.magicnotice.ui.adapter.TabConfigTipAdapter;
import com.zhangshuo.magicnotice.ui.model.KeywordInfo;
import com.zhangshuo.magicnotice.ui.model.OptionModel;
import com.zhangshuo.magicnotice.ui.model.TabConfigTipInfo;
import com.zhangshuo.magicnotice.ui.model.TabInfo;

import java.util.ArrayList;
import java.util.List;

public class TabConfigDialog extends BaseBottomDialog<DialogTabConfigBinding> {
    private TabInfo mTabInfo;
    private OnCallback mOnCallback;
    private int selectOffset = -1;
    private List<Integer> selectRuleIdList = new ArrayList<>();
    private List<String> selectRuleNameList = new ArrayList<>();
    private List<String> selectPkgNameList = new ArrayList<>();
    private List<KeywordInfo> selectKeywordList = new ArrayList<>();

    private SelectRuleDialog selectRuleDialog;
    private TabConfigTipAdapter ruleTipAdapter;
    private SelectOptionsDialog timeOptionsDialog;
    private SelectAppDialog selectAppDialog;
    private TabConfigTipAdapter appTipAdapter;
    private TabConfigTipAdapter keywordAdapter;
    private KeywordInputDialog keywordInputDialog;

    public static void create(Context context, TabInfo tabInfo, OnCallback onCallback) {
        TabConfigDialog tabConfigDialog = new TabConfigDialog();
        tabConfigDialog.mTabInfo = tabInfo;
        tabConfigDialog.mOnCallback = onCallback;
        tabConfigDialog.show(context);
    }

    @Override
    public void initView() {
        ruleTipAdapter = new TabConfigTipAdapter(getContext());
        binding.rvRule.setLayoutManager(new FlexboxLayoutManager(getContext()));
        binding.rvRule.setAdapter(ruleTipAdapter);

        appTipAdapter = new TabConfigTipAdapter(getContext());
        binding.rvApp.setLayoutManager(new FlexboxLayoutManager(getContext()));
        binding.rvApp.setAdapter(appTipAdapter);

        keywordAdapter = new TabConfigTipAdapter(getContext());
        binding.rvKeyword.setLayoutManager(new FlexboxLayoutManager(getContext()));
        binding.rvKeyword.setAdapter(keywordAdapter);

        binding.tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        if (mTabInfo != null && mTabInfo.getId() > 0) {
            binding.btnDelete.setVisibility(View.VISIBLE);
        } else {
            binding.btnDelete.setVisibility(View.GONE);
        }
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTabInfo != null && mTabInfo.getId() > 0) {
                    TabDataService.delete(mTabInfo, new AsyncCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            close();
                            if (mOnCallback != null) {
                                mOnCallback.onDelete(mTabInfo);
                            }
                        }
                    });
                }
            }
        });
        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tabName = binding.etName.getText().toString();
                if (TextUtils.isEmpty(tabName)) {
                    ToastManager.get().noticeShortMessage(StringUtil.getString(R.string.tab_config_no_input_tip));
                    return;
                }
                TabInfo tabInfo = null;
                if (mTabInfo != null) {
                    tabInfo = mTabInfo.copyNew();
                } else {
                    tabInfo = new TabInfo();
                    tabInfo.setOpenFlag(1);
                }
                //需要保存对应的
                tabInfo.setShowKeyWord(getShowKeyWord());
                if (tabInfo.getId() < 0) {
                    //如果是全部或者收藏的，则保存到本地
                    String key = LocalDataKeys.SKEY_TAB_KEYWORD_CONFIG + "_" + tabInfo.getId();
                    LocalDataUtil.get().saveData(key, GsonUtils.toString(tabInfo));
                    close();
                    if (mOnCallback != null) {
                        mOnCallback.onConfirm(tabInfo);
                    }
                    return;
                }

                //判断是否有关联id或者应用
                tabInfo.setTabName(binding.etName.getText().toString());
                tabInfo.setRelationDateOffset(selectOffset);

                tabInfo.setRelationIds(getRuleIds());
                tabInfo.setRuleNameList(selectRuleNameList);
                tabInfo.setRelationPkgs(getAppPkgs());
                TabInfo finalTabInfo = tabInfo;
                TabDataService.insertOrUpdate(tabInfo, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            close();
                            if (mOnCallback != null) {
                                mOnCallback.onConfirm(finalTabInfo);
                            }
                        } else {
                            ToastManager.get().showShortToast(R.string.tab_config_group_no_name);
                        }
                    }
                });
            }
        });
        binding.clTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectTimeDialog();
            }
        });
        binding.clRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectRuleDialog();
            }
        });
        binding.clApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAppDialog();
            }
        });
        binding.clKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeywordInputDialog();
            }
        });

        setTabInfoData();
    }

    private String getShowKeyWord() {
//        StringBuilder stringBuilder = new StringBuilder();
//        if (selectKeywordList != null) {
//            for (String keyword : selectKeywordList) {
//                stringBuilder.append(keyword)
//                        .append("、");
//            }
//        }
//        String keyword = "";
//        if (stringBuilder.length() > 0) {
//            keyword = stringBuilder.substring(0, stringBuilder.length() - 1);
//        }
        return GsonUtils.toString(selectKeywordList);
    }


    private String getRuleIds() {
        StringBuilder stringBuilder = new StringBuilder();
        if (selectRuleIdList != null)
            for (int i = 0; i < selectRuleIdList.size(); i++) {
                stringBuilder.append(selectRuleIdList.get(i));
                stringBuilder.append(",");
            }
        String ids = "";
        if (stringBuilder.length() > 0) {
            ids = stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return ids;
    }

    private String getAppPkgs() {
        StringBuilder stringBuilder = new StringBuilder();
        if (selectPkgNameList != null)
            for (int i = 0; i < selectPkgNameList.size(); i++) {
                stringBuilder.append(selectPkgNameList.get(i));
                stringBuilder.append(",");
            }
        String ids = "";
        if (stringBuilder.length() > 0) {
            ids = stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return ids;
    }

    private void setTabInfoData() {
        if (mTabInfo != null) {
            binding.etName.setText(mTabInfo.getTabName());
            selectOffset = mTabInfo.getRelationDateOffset();
            selectRuleIdList = mTabInfo.getRelationIdList();
            selectPkgNameList = mTabInfo.getRelationPkgList();
            if (TextUtils.isEmpty(mTabInfo.getRelationIds())
                    || mTabInfo.getId() <= 0) {
                binding.rvRule.setVisibility(View.GONE);
            } else {
                binding.rvRule.setVisibility(View.VISIBLE);
                ruleTipAdapter.addData(buildRuleTipList(mTabInfo.getRuleNameList()), true);
            }
            if (TextUtils.isEmpty(mTabInfo.getRelationPkgs())
                    || mTabInfo.getId() <= 0) {
                binding.rvApp.setVisibility(View.GONE);
            } else {
                binding.rvApp.setVisibility(View.VISIBLE);
                appTipAdapter.addData(buildAppTipList(mTabInfo.getRelationPkgList()), true);
            }

            if (TextUtils.isEmpty(mTabInfo.getShowKeyWord())) {
                binding.rvKeyword.setVisibility(View.GONE);
                binding.tvKeywordTip.setText(R.string.tab_config_dialog_keyword_tip);
            } else {
                binding.rvKeyword.setVisibility(View.VISIBLE);
                //转化成KeywordInfo,需要进行兼容，所以要判断是不是真的JSON
                selectKeywordList.clear();
                selectKeywordList.addAll(mTabInfo.getKeywordInfoList());
                binding.tvKeywordTip.setText(String.format(StringUtil.getString(R.string.tab_config_dialog_keyword_num), selectKeywordList.size()));
                List<TabConfigTipInfo> tipInfoList = new ArrayList<>();
                for (KeywordInfo keywordInfo : selectKeywordList) {
                    tipInfoList.add(new TabConfigTipInfo(keywordInfo.getWord(), "", keywordInfo.getColor()));
                }
                keywordAdapter.addData(tipInfoList, true);
            }

            if (mTabInfo.getId() <= 0) {
                binding.etName.setEnabled(false);
                binding.tvTimeTip.setText(R.string.tab_config_dialog_all);
                binding.clTime.setClickable(false);
                binding.ivTimeMore.setVisibility(View.GONE);

                binding.tvRuleTip.setText(R.string.tab_config_dialog_default);
                binding.clRule.setClickable(false);
                binding.ivRuleMore.setVisibility(View.GONE);

                binding.tvAppTip.setText(R.string.tab_config_dialog_all);
                binding.clApp.setClickable(false);
                binding.ivAppMore.setVisibility(View.GONE);
            } else {
                binding.etName.setEnabled(true);

                binding.tvTimeTip.setText(getTipDate(mTabInfo.getRelationDateOffset()));
                binding.clTime.setClickable(true);
                binding.ivTimeMore.setVisibility(View.VISIBLE);

                binding.tvRuleTip.setText(R.string.tab_config_select_tip);
                binding.clRule.setClickable(true);
                binding.ivRuleMore.setVisibility(View.VISIBLE);

                binding.tvAppTip.setText(R.string.tab_config_select_tip);
                binding.clApp.setClickable(true);
                binding.ivAppMore.setVisibility(View.VISIBLE);
            }
        }
    }

    public List<TabConfigTipInfo> buildRuleTipList(List<String> dataList) {
        List<TabConfigTipInfo> tipInfoList = new ArrayList<>();
        if (dataList == null) return tipInfoList;
        for (String ruleName : dataList) {
            tipInfoList.add(new TabConfigTipInfo(ruleName));
        }
        return tipInfoList;
    }

    public List<TabConfigTipInfo> buildAppTipList(List<String> pkgNameList) {
        List<TabConfigTipInfo> tipInfoList = new ArrayList<>();
        if (pkgNameList == null) return tipInfoList;
        for (String pkg : pkgNameList) {
            tipInfoList.add(new TabConfigTipInfo(AppUtils.getAppName(pkg), pkg));
        }
        return tipInfoList;
    }

    public static String getTipDate(int offset) {
        if (offset == -1) {
            return StringUtil.getString(R.string.tab_config_dialog_all);
        } else if (offset == 0) {
            return StringUtil.getString(R.string.tab_config_dialog_today);
        } else if (offset == 3) {
            return StringUtil.getString(R.string.tab_config_dialog_three);
        } else if (offset == 7) {
            return StringUtil.getString(R.string.tab_config_dialog_week);
        }
        return StringUtil.getString(R.string.tab_config_dialog_all);
    }

    private void showSelectTimeDialog() {
        if (timeOptionsDialog == null) {
            List<OptionDataSet> optionDataSetList = new ArrayList<>();
            optionDataSetList.add(new OptionModel(R.string.tab_config_dialog_all, "-1"));
            optionDataSetList.add(new OptionModel(R.string.tab_config_dialog_today, "0"));
            optionDataSetList.add(new OptionModel(R.string.tab_config_dialog_three, "3"));
            optionDataSetList.add(new OptionModel(R.string.tab_config_dialog_week, "7"));
            timeOptionsDialog = new SelectOptionsDialog(optionDataSetList);
            timeOptionsDialog.setOnCallback(new SelectOptionsDialog.OnCallback() {
                @Override
                public void onResult(int index, OptionDataSet optionDataSet) {
                    String value = optionDataSet.getValue();
                    selectOffset = StringUtil.stringToInt(value);
                    binding.tvTimeTip.setText(getTipDate(selectOffset));
                }
            });
        }
        timeOptionsDialog.setSelectValue(String.valueOf(selectOffset));
        timeOptionsDialog.show(getContext());
    }

    private void showSelectRuleDialog() {
        if (selectRuleDialog == null) {
            selectRuleDialog = new SelectRuleDialog(false);
            selectRuleDialog.setOnCallback(new SelectRuleDialog.OnCallback() {
                @Override
                public void onResult(List<Integer> ruleIdList, List<String> ruleNameList) {
                    selectRuleIdList = ruleIdList;
                    selectRuleNameList = ruleNameList;
                    binding.rvRule.setVisibility(ruleIdList.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.tvRuleTip.setText(ruleIdList.isEmpty() ? StringUtil.getString(R.string.tab_config_select_tip)
                            : String.format(StringUtil.getString(R.string.tab_config_dialog_have_select), ruleIdList.size()));
                    ruleTipAdapter.addData(buildRuleTipList(selectRuleNameList), true);
                }
            });
        }
        selectRuleDialog.setSelectedIdList(selectRuleIdList);
        selectRuleDialog.show(getContext());
    }

    private void showSelectAppDialog() {
        if (selectAppDialog == null) {
            selectAppDialog = new SelectAppDialog(false);
            selectAppDialog.setOnCallback(new SelectAppDialog.OnCallback() {
                @Override
                public void onResult(List<String> pkgList, List<String> nameList) {
                    binding.rvApp.setVisibility(pkgList.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.tvAppTip.setText(pkgList.isEmpty() ? StringUtil.getString(R.string.tab_config_select_tip)
                            : String.format(StringUtil.getString(R.string.tab_config_dialog_have_select), pkgList.size()));
                    selectPkgNameList = pkgList;
                    appTipAdapter.addData(buildAppTipList(pkgList), true);
                }
            });
        }
        selectAppDialog.setSelectedPackageNameList(selectPkgNameList);
        selectAppDialog.show(getContext());
    }

    private void showKeywordInputDialog() {
        if (keywordInputDialog == null) {
            keywordInputDialog = new KeywordInputDialog();
            keywordInputDialog.setOnCallback(new KeywordInputDialog.OnCallback() {
                @Override
                public void onResult(List<KeywordInfo> haveInputList) {
                    selectKeywordList = haveInputList;
                    if (haveInputList.isEmpty()) {
                        binding.tvKeywordTip.setText(R.string.tab_config_dialog_keyword_tip);
                        binding.rvKeyword.setVisibility(View.GONE);
                    } else {
                        binding.tvKeywordTip.setText(String.format(StringUtil.getString(R.string.tab_config_dialog_keyword_num),
                                haveInputList.size()));
                        binding.rvKeyword.setVisibility(View.VISIBLE);
                        List<TabConfigTipInfo> tipInfoList = new ArrayList<>();
                        for (KeywordInfo keywordInfo : selectKeywordList) {
                            tipInfoList.add(new TabConfigTipInfo(keywordInfo.getWord(), "", keywordInfo.getColor()));
                        }
                        keywordAdapter.addData(tipInfoList, true);
                    }
                }
            });
        }
        keywordInputDialog.setHaveInputList(selectKeywordList);
        keywordInputDialog.show(getContext());
    }

    public interface OnCallback {
        void onConfirm(TabInfo tabInfo);

        void onDelete(TabInfo tabInfo);
    }
}
