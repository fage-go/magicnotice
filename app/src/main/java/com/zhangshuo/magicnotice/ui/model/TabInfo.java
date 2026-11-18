package com.zhangshuo.magicnotice.ui.model;

import android.text.TextUtils;

import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.db.model.TabData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabInfo implements Serializable {
    private int id;
    private String tabName;
    private int relationDateOffset;
    private String relationIds;
    private String relationPkgs;
    private int openFlag;
    private int receiveFlag;
    private List<String> ruleNameList;
    private int notReadCount = 0;
    private String showKeyWord;
    private List<KeywordInfo> keywordInfoList = new ArrayList<>();

    public TabInfo() {
    }

    public boolean isSameRelation(TabInfo tab) {
        boolean isSameId = (!TextUtils.isEmpty(relationIds) && relationIds.equals(tab.getRelationIds()));
        boolean isSamePkgs = (!TextUtils.isEmpty(relationPkgs) && relationPkgs.equals(tab.getRelationPkgs()));
        boolean isSameOffset = relationDateOffset == tab.getRelationDateOffset();
        boolean isSameShowKeyWord = (!TextUtils.isEmpty(showKeyWord) && showKeyWord.equals(tab.getShowKeyWord()));
        return isSameId && isSamePkgs && isSameOffset && isSameShowKeyWord;
    }

    public TabInfo copyNew() {
        TabInfo tabInfo = new TabInfo();
        tabInfo.setId(id);
        tabInfo.setTabName(tabName);
        tabInfo.setRelationDateOffset(relationDateOffset);
        tabInfo.setRelationIds(relationIds);
        tabInfo.setRelationPkgs(relationPkgs);
        tabInfo.setOpenFlag(openFlag);
        tabInfo.setRuleNameList(ruleNameList);
        tabInfo.setNotReadCount(notReadCount);
        tabInfo.setShowKeyWord(showKeyWord);
        tabInfo.setKeywordInfoList(keywordInfoList);
        return tabInfo;
    }

    public TabInfo(TabData tabData) {
        this.id = tabData.getId();
        this.tabName = tabData.getName();
        this.relationIds = tabData.getRelationIds();
        this.openFlag = tabData.getOpenFlag();
        this.relationDateOffset = tabData.getRelationDateOffset();
        this.relationPkgs = tabData.getRelationPkgs();
        this.receiveFlag = tabData.getReceiveFlag();
        this.showKeyWord = tabData.getShowKeyWord();
        this.keywordInfoList.clear();
        this.keywordInfoList.addAll(convertKeywordInfoList(showKeyWord));
    }

    public TabData getTabData() {
        TabData tabData = new TabData();
        tabData.setId(id);
        tabData.setName(tabName);
        tabData.setOpenFlag(getOpenFlag());
        tabData.setRelationDateOffset(relationDateOffset);
        tabData.setRelationIds(relationIds);
        tabData.setRelationPkgs(relationPkgs);
        tabData.setReceiveFlag(receiveFlag);
        tabData.setShowKeyWord(showKeyWord);
        return tabData;
    }

    public List<KeywordInfo> convertKeywordInfoList(String keyword) {
        List<KeywordInfo> tmpList = new ArrayList<>();
        try {
            tmpList = GsonUtils.toBeanArray(keyword, KeywordInfo.class);
        } catch (Exception e) {
            //转化错误的情况下
            String[] splitKey = keyword.split("、");
            for (String tip : splitKey) {
                tmpList.add(new KeywordInfo(tip, "#0081FF"));
            }
        }
        if (tmpList == null) {
            tmpList = new ArrayList<>();
        }
        return tmpList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getRelationIds() {
        return relationIds == null ? "" : relationIds;
    }

    public void setRelationIds(String relationIds) {
        this.relationIds = relationIds;
    }


    public List<String> getRuleNameList() {
        return ruleNameList;
    }

    public void setRuleNameList(List<String> ruleNameList) {
        this.ruleNameList = ruleNameList;
    }

    public List<Integer> getRelationIdList() {
        if (TextUtils.isEmpty(relationIds)) return new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        for (String id : relationIds.split(",")) {
            idList.add(StringUtil.stringToInt(id));
        }
        return idList;
    }

    public List<String> getRelationPkgList() {
        if (TextUtils.isEmpty(relationPkgs)) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(relationPkgs.split(",")));
    }

    public int getOpenFlag() {
        return openFlag;
    }

    public void setOpenFlag(int openFlag) {
        this.openFlag = openFlag;
    }


    public int getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount) {
        this.notReadCount = notReadCount;
    }

    public int getRelationDateOffset() {
        return relationDateOffset;
    }

    public void setRelationDateOffset(int relationDateOffset) {
        this.relationDateOffset = relationDateOffset;
    }

    public String getRelationPkgs() {
        return relationPkgs == null ? "" : relationPkgs;
    }

    public void setRelationPkgs(String relationPkgs) {
        this.relationPkgs = relationPkgs;
    }


    public int getReceiveFlag() {
        return receiveFlag;
    }

    public void setReceiveFlag(int receiveFlag) {
        this.receiveFlag = receiveFlag;
    }

    public String getShowKeyWord() {
        return showKeyWord;
    }

    public void setShowKeyWord(String showKeyWord) {
        this.showKeyWord = showKeyWord;
    }


    public List<KeywordInfo> getKeywordInfoList() {
        return keywordInfoList;
    }

    public void setKeywordInfoList(List<KeywordInfo> keywordInfoList) {
        this.keywordInfoList = keywordInfoList;
    }
}
