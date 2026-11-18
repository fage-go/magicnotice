package com.zhangshuo.magicnotice.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

@Entity
public class ConfigRuleData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String conditionList;
    private String behaviorList;
    private boolean open = false;
    private boolean retain = false;
    private boolean collect = false;
    private boolean policy = false;
    private String tagColor;
    private String cleanTime = "0";
    private String extraConfig;

    public ConfigRuleData() {
    }

    public ConfigRuleData(ConfigRuleModel configRuleModel) {
        if (configRuleModel.getId() > 0) {
            id = configRuleModel.getId();
        }
        name = configRuleModel.getRuleName();
        conditionList = GsonUtils.toString(configRuleModel.getConditionList());
        behaviorList = GsonUtils.toString(configRuleModel.getBehaviorList());
        open = configRuleModel.isOpen();
        retain = configRuleModel.isRetain();
        collect = configRuleModel.isCollect();
        policy = configRuleModel.isPolicy();
        extraConfig = configRuleModel.getExtraConfig();
        tagColor = configRuleModel.getTagColor();
        cleanTime = configRuleModel.getCleanTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConditionList() {
        return conditionList;
    }

    public void setConditionList(String conditionList) {
        this.conditionList = conditionList;
    }

    public String getBehaviorList() {
        return behaviorList;
    }

    public void setBehaviorList(String behaviorList) {
        this.behaviorList = behaviorList;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
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

    public String getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(String extraConfig) {
        this.extraConfig = extraConfig;
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
