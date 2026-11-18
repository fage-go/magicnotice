package com.zhangshuo.magicnotice.db.service;

import android.text.TextUtils;

import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.GsonUtils;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.db.AppDatabase;
import com.zhangshuo.magicnotice.db.model.ConfigRuleData;
import com.zhangshuo.magicnotice.db.model.TabData;
import com.zhangshuo.magicnotice.ui.model.TabInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class TabDataService {

    public static void insertOrUpdate(TabInfo tabInfo, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                TabData tabData = tabInfo.getTabData();
                if (tabData.getId() > 0) {
                    AppDatabase.getInstance().tabDataDao().update(tabData);
                    return true;
                } else {
                    //不能名称一样
                    List<TabData> tabDataByName = AppDatabase.getInstance().tabDataDao().findTabDataByName(tabData.getName());
                    if (tabDataByName != null && tabDataByName.size() > 0) {
                        return false;
                    } else {
                        AppDatabase.getInstance().tabDataDao().insert(tabData);
                        return true;
                    }
                }
            }
        }, callback);
    }

    public static void insertList(List<TabInfo> tabInfoList, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (TabInfo tabInfo : tabInfoList) {
                    TabData tabData = tabInfo.getTabData();
                    AppDatabase.getInstance().tabDataDao().insert(tabData);
                }
                return true;
            }
        }, callback);
    }

    public static void delete(TabInfo tabInfo, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().tabDataDao().deleteById(tabInfo.getId());
                return true;
            }
        }, callback);
    }

    public static void findTabById(int id, AsyncCallback<TabInfo> callback) {
        AsyncCallUtils.asyncCall(new Callable<TabInfo>() {
            @Override
            public TabInfo call() throws Exception {
                TabData tabDataById = AppDatabase.getInstance().tabDataDao().findTabDataById(id);
                return convertTabInfo(tabDataById);
            }
        }, callback);
    }

    public static void findAllTab(AsyncCallback<List<TabInfo>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<TabInfo>>() {
            @Override
            public List<TabInfo> call() throws Exception {
                List<TabInfo> tabInfoList = getDefaultList();
                List<TabData> tabDataList = AppDatabase.getInstance().tabDataDao().findAllTabData();
                for (int i = 0; i < tabDataList.size(); i++) {
                    TabInfo tabInfo = convertTabInfo(tabDataList.get(i));
                    tabInfoList.add(tabInfo);
                }
                sortTab(tabInfoList);
                return tabInfoList;
            }
        }, callback);
    }

    private static TabInfo convertTabInfo(TabData tabData) {
        TabInfo tabInfo = new TabInfo(tabData);
        if (!TextUtils.isEmpty(tabInfo.getRelationIds())) {
            List<ConfigRuleData> relationList = AppDatabase.getInstance().configRuleDataDao().findDataListById(
                    tabInfo.getRelationIdList()
            );
            List<String> ruleNameList = new ArrayList<>();
            for (ConfigRuleData configRuleData : relationList) {
                ruleNameList.add(configRuleData.getName());
            }
            if (tabInfo.getRelationIds().contains("0")) {
                ruleNameList.add(StringUtil.getString(R.string.no_hit_name));
            }
            tabInfo.setRuleNameList(ruleNameList);
        }
        return tabInfo;
    }

    public static void findActTab(AsyncCallback<List<TabInfo>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<TabInfo>>() {
            @Override
            public List<TabInfo> call() throws Exception {
                List<TabInfo> tabInfoList = getDefaultList();
                for (int i = 0; i < tabInfoList.size(); i++) {
                    TabInfo tabInfo = tabInfoList.get(i);
                    if (tabInfo.getOpenFlag() != 1) {
                        tabInfoList.remove(i);
                        i--;
                    }
                }
                List<TabData> tabDataList = AppDatabase.getInstance().tabDataDao().findActTabData();
                for (int i = 0; i < tabDataList.size(); i++) {
                    TabInfo tabInfo = new TabInfo(tabDataList.get(i));
                    tabInfoList.add(tabInfo);
                }
                sortTab(tabInfoList);
                return tabInfoList;
            }
        }, callback);
    }

    private static List<TabInfo> getDefaultList() {
        String dataFlag = LocalDataUtil.get().getData(LocalDataKeys.SKEY_TAB_OPEN_FLAG, "-3;-2;-1;");
        List<TabInfo> tabInfoList = new ArrayList<>();
        //构建默认的
        TabInfo tabInfoAll = new TabInfo();
        tabInfoAll.setId(-2);
        tabInfoAll.setTabName(StringUtil.getString(R.string.tab_default_name_all));
        tabInfoAll.setOpenFlag(dataFlag.contains("-2") ? 1 : 0);
        convertDefaultTabInfo(tabInfoAll);
        tabInfoList.add(tabInfoAll);
        TabInfo tabInfoFocus = new TabInfo();
        tabInfoFocus.setId(-1);
        tabInfoFocus.setTabName(StringUtil.getString(R.string.tab_default_name_collect));
        tabInfoFocus.setOpenFlag(dataFlag.contains("-1") ? 1 : 0);
        convertDefaultTabInfo(tabInfoFocus);
        tabInfoList.add(tabInfoFocus);
        return tabInfoList;
    }

    private static void convertDefaultTabInfo(TabInfo tabInfo) {
        String key = LocalDataKeys.SKEY_TAB_KEYWORD_CONFIG + "_" + tabInfo.getId();
        String tabInfoData = LocalDataUtil.get().getData(key, "");
        if (!TextUtils.isEmpty(tabInfoData)) {
            try {
                TabInfo configTabInfo = GsonUtils.toBean(tabInfoData, TabInfo.class);
                tabInfo.setShowKeyWord(configTabInfo.getShowKeyWord());
                tabInfo.setKeywordInfoList(tabInfo.convertKeywordInfoList(configTabInfo.getShowKeyWord()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sortTab(List<TabInfo> modelList) {
        String data = LocalDataUtil.get().getData(LocalDataKeys.SKEY_SORT_TAB_IDS, "");
        if (!TextUtils.isEmpty(data)) {
            String[] idsArray = data.split(";");
            int lastIndex = 0;
            for (int i = 0; i < idsArray.length; i++) {
                try {
                    int id = Integer.parseInt(idsArray[i]);
                    for (int j = 0; j < modelList.size(); j++) {
                        TabInfo tabInfo = modelList.get(j);
                        if (tabInfo.getId() == id) {
                            Collections.swap(modelList, lastIndex, j);
                            lastIndex++;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
