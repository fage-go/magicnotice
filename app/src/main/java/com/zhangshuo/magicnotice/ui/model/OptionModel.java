package com.zhangshuo.magicnotice.ui.model;

import android.text.TextUtils;

import com.zhangshuo.common.pick.dataset.OptionDataSet;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;

import java.util.ArrayList;
import java.util.List;

public class OptionModel implements OptionDataSet {
    private String options;
    private String value;
    private boolean isSelected;

    public OptionModel(String value) {
        this.options = getNameByValue(value);
        this.value = value;
    }

    public OptionModel(String options, String value) {
        this.options = options;
        this.value = value;
    }

    public OptionModel(int optionsResId, String value) {
        this.options = StringUtil.getString(optionsResId);
        this.value = value;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public List<? extends OptionDataSet> getSubs() {
        return null;
    }

    @Override
    public CharSequence getCharSequence() {
        return options;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static List<OptionDataSet> getModelList() {
        List<OptionDataSet> optionsList = new ArrayList<>();
        optionsList.add(new OptionModel("0"));
        optionsList.add(new OptionModel("1"));
        optionsList.add(new OptionModel("3"));
        optionsList.add(new OptionModel("7"));
        optionsList.add(new OptionModel("15"));
        optionsList.add(new OptionModel("30"));
        optionsList.add(new OptionModel("90"));
        optionsList.add(new OptionModel("180"));
        optionsList.add(new OptionModel("365"));
        return optionsList;
    }

    public static String getNameByValue(String value) {
        if (TextUtils.isEmpty(value) || "0".equals(value)) {
            return StringUtil.getString(R.string.options_tip_no_set);
        }
        switch (value) {
            case "1":
                return StringUtil.getString(R.string.options_tip_day_one);
            case "3":
                return StringUtil.getString(R.string.options_tip_day_three);
            case "7":
                return StringUtil.getString(R.string.options_tip_day_five);
            case "15":
                return StringUtil.getString(R.string.options_tip_day_half_month);
            case "30":
                return StringUtil.getString(R.string.options_tip_day_one_month);
            case "90":
                return StringUtil.getString(R.string.options_tip_day_three_month);
            case "180":
                return StringUtil.getString(R.string.options_tip_day_half_year);
            case "365":
                return StringUtil.getString(R.string.options_tip_day_one_year);
        }
        return StringUtil.getString(R.string.options_tip_no_set);
    }
}
