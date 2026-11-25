package com.zhangshuo.magicnotice.ui.model;

import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.magicnotice.db.model.ConfigRuleData;

import java.util.ArrayList;
import java.util.List;


public class ConfigRuleModel {
    //id
    private int id;
    private boolean backupAppend;
    //名称
    private String ruleName;
    //条件类别
    private List<ConditionsModel> conditionList;
    //触发内容
    private List<BehaviorModel> behaviorList;
    //命中次数
    private int filterCount = 0;
    //是否打开状态
    private boolean open = true;
    //是否保留原始通知
    private boolean retain = false;
    //自动收藏
    private boolean collect = false;
    //支持免打扰
    private boolean policy = false;
    //标签颜色
    private String tagColor = "";
    //清理时间
    private String cleanTime = "0";
    //额外的内容
    private String extraConfig;
    //是否选择
    private boolean selected = false;

    public ConfigRuleModel() {
    }

    public ConfigRuleModel(ConfigRuleData data) {
        this.id = data.getId();
        this.backupAppend = data.isBackupAppend();
        this.ruleName = data.getName();
        this.conditionList = ConditionsModel.convert(data.getConditionList());
        this.behaviorList = BehaviorModel.convert(data.getBehaviorList());
        this.filterCount = 0;
        this.open = data.isOpen();
        this.retain = data.isRetain();
        this.collect = data.isCollect();
        this.policy = data.isPolicy();
        this.tagColor = data.getTagColor();
        this.extraConfig = data.getExtraConfig();
        this.cleanTime = data.getCleanTime();
    }

    public ConfigRuleModel copyModel() {
        ConfigRuleModel configRuleModel = new ConfigRuleModel();
        configRuleModel.id = id;
        configRuleModel.backupAppend = backupAppend;
        configRuleModel.ruleName = ruleName;
        configRuleModel.conditionList = conditionList;
        configRuleModel.behaviorList = behaviorList;
        configRuleModel.filterCount = filterCount;
        configRuleModel.open = open;
        configRuleModel.retain = retain;
        configRuleModel.collect = collect;
        configRuleModel.policy = policy;
        configRuleModel.tagColor = tagColor;
        configRuleModel.extraConfig = extraConfig;
        configRuleModel.selected = selected;
        configRuleModel.cleanTime = cleanTime;
        return configRuleModel;
    }

    public boolean isBackupAppend() { return backupAppend; }

    public int getId() {
        return id;
    }


    public String getRuleName() {
        return ruleName;
    }

    public List<ConditionsModel> getConditionList() {
        if (conditionList == null) {
            conditionList = new ArrayList<>();
        }
        return conditionList;
    }

    public List<BehaviorModel> getBehaviorList() {
        if (behaviorList == null) {
            behaviorList = new ArrayList<>();
        }
        return behaviorList;
    }

    public int getFilterCount() {
        return filterCount;
    }

    public ConfigRuleModel setId(int id) {
        this.id = id;
        return this;
    }

    public ConfigRuleModel setRuleName(String ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    public ConfigRuleModel setConditions(List<ConditionsModel> conditions) {
        this.conditionList = conditions;
        return this;
    }

    public ConfigRuleModel setBehaviorModel(List<BehaviorModel> behaviorModel) {
        this.behaviorList = behaviorModel;
        return this;
    }

    public ConfigRuleModel setFilterCount(int filterCount) {
        this.filterCount = filterCount;
        return this;
    }

    public String toJsonString() {
        return GsonUtils.toString(this);
    }


    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setConditionList(List<ConditionsModel> conditionList) {
        this.conditionList = conditionList;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public boolean isPolicy() {
        return policy;
    }

    public void setPolicy(boolean policy) {
        this.policy = policy;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public String getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(String extraConfig) {
        this.extraConfig = extraConfig;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public String getCleanTime() {
        return cleanTime;
    }

    public void setCleanTime(String cleanTime) {
        this.cleanTime = cleanTime;
    }
}
