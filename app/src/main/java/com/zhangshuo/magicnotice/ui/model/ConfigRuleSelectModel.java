package com.zhangshuo.magicnotice.ui.model;

import com.zhangshuo.magicnotice.ui.view.configrule.IConfigRule;

public class ConfigRuleSelectModel {
    private int type;
    private String title;
    private IConfigRule configRule;
    private boolean selected = false;

    public ConfigRuleSelectModel(int type, String title, IConfigRule configRule) {
        this.type = type;
        this.title = title;
        this.configRule = configRule;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public IConfigRule getConfigRule() {
        return configRule;
    }
}
