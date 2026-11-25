package com.zhangshuo.magicnotice.db.service;

import android.text.TextUtils;

import com.zhangshuo.magicnotice.backup.RuleBackup;

import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.R;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.db.AppDatabase;
import com.zhangshuo.magicnotice.db.dao.ConfigRuleDataDao;
import com.zhangshuo.magicnotice.notice.factory.RuleOrdering;
import com.zhangshuo.magicnotice.db.model.ConfigRuleData;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class ConfigRuleDataService {


    public static void findById(int id, AsyncCallback<ConfigRuleModel> callback) {
        AsyncCallUtils.asyncCall(new Callable<ConfigRuleModel>() {
            @Override
            public ConfigRuleModel call() throws Exception {
                ConfigRuleData configRuleData = AppDatabase.getInstance().configRuleDataDao()
                        .findById(id);
                return new ConfigRuleModel(configRuleData);
            }
        }, callback);
    }

    public static void insertOrUpdate(ConfigRuleModel configRuleModel, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ConfigRuleData configRuleData = new ConfigRuleData(configRuleModel);
                if (configRuleData.getId() > 0) {
                    AppDatabase.getInstance()
                            .configRuleDataDao()
                            .update(configRuleData);
                } else {
                    AppDatabase.getInstance()
                            .configRuleDataDao()
                            .insert(configRuleData);
                }
                return true;
            }
        }, callback);
    }

    public static void insertOrUpdate(ConfigRuleModel[] configRuleModelArray, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (int i = 0; i < configRuleModelArray.length; i++) {
                    ConfigRuleModel configRuleModel = configRuleModelArray[i];
                    ConfigRuleData configRuleData = new ConfigRuleData(configRuleModel);
                    if (configRuleData.getId() > 0) {
                        AppDatabase.getInstance()
                                .configRuleDataDao()
                                .update(configRuleData);
                    } else {
                        long insertId = AppDatabase.getInstance()
                                .configRuleDataDao()
                                .insert(configRuleData);
                        configRuleModel.setId((int) insertId);
                    }
                }
                return true;
            }
        }, callback);
    }

    public static void findConfigRuleList(AsyncCallback<List<ConfigRuleModel>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<ConfigRuleModel>>() {
            @Override
            public List<ConfigRuleModel> call() throws Exception {
                List<ConfigRuleData> dataList = AppDatabase.getInstance().configRuleDataDao().findDataList();
                List<ConfigRuleModel> configRuleModelList = new ArrayList<>();
                if (dataList != null) {
                    for (int i = 0; i < dataList.size(); i++) {
                        ConfigRuleData configRuleData = dataList.get(i);
                        configRuleModelList.add(new ConfigRuleModel(configRuleData));
                    }
                    sortConfigRule(configRuleModelList);
                }
                return configRuleModelList;
            }
        }, callback);
    }

    public static void findConfigRuleList(String keyword, AsyncCallback<List<ConfigRuleModel>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<ConfigRuleModel>>() {
            @Override
            public List<ConfigRuleModel> call() throws Exception {
                String searchKey = "%%";
                if (!TextUtils.isEmpty(keyword)) {
                    searchKey = "%" + keyword + "%";
                }
                List<ConfigRuleData> dataList = AppDatabase.getInstance().configRuleDataDao().findDataList(searchKey);
                List<ConfigRuleModel> configRuleModelList = new ArrayList<>();
                if (dataList != null) {
                    for (int i = 0; i < dataList.size(); i++) {
                        ConfigRuleData configRuleData = dataList.get(i);
                        configRuleModelList.add(new ConfigRuleModel(configRuleData));
                    }
                    sortConfigRule(configRuleModelList);
                }
                return configRuleModelList;
            }
        }, callback);
    }

    public static void findConfigRuleInfoList(AsyncCallback<List<ConfigRuleModel>> callback) {
        findDisplayedRules(true, callback);
    }

    /** The visible/current rule order without UI-only hit counts. */
    public static void findConfigRuleEvaluationList(AsyncCallback<List<ConfigRuleModel>> callback) {
        findDisplayedRules(false, callback);
    }

    private static void findDisplayedRules(boolean includeCounts, AsyncCallback<List<ConfigRuleModel>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<ConfigRuleModel>>() {
            @Override public List<ConfigRuleModel> call() {
                ConfigRuleDataDao dao = AppDatabase.getInstance().configRuleDataDao();
                List<ConfigRuleData> data = dao.findDataList();
                List<ConfigRuleModel> rules = new ArrayList<>();
                if (data != null) {
                    for (ConfigRuleData entry : data) {
                        ConfigRuleModel model = new ConfigRuleModel(entry);
                        if (includeCounts) model.setFilterCount(dao.queryNotificationByRuleId(entry.getId()));
                        rules.add(model);
                    }
                }
                return RuleOrdering.displayed(rules, ConfigRuleModel::isOpen, ConfigRuleModel::getId,
                        ConfigRuleModel::isBackupAppend,
                        LocalDataUtil.get().getData(LocalDataKeys.SKEY_SORT_IDS, ""));
            }
        }, callback);
    }

    /** Export selected rules in the same order as the visible rule list. */
    public static byte[] exportRuleBackup(List<Integer> selectedIds) throws RuleBackup.Invalid {
        ConfigRuleDataDao dao = AppDatabase.getInstance().configRuleDataDao();
        List<ConfigRuleData> rules = RuleOrdering.displayed(dao.findDataList(), ConfigRuleData::isOpen,
                ConfigRuleData::getId, ConfigRuleData::isBackupAppend,
                LocalDataUtil.get().getData(LocalDataKeys.SKEY_SORT_IDS, ""));
        Set<Integer> wanted = new HashSet<>(selectedIds);
        List<RuleBackup.Rule> entries = new ArrayList<>();
        for (ConfigRuleData row : rules) {
            if (wanted.remove(row.getId())) {
                entries.add(new RuleBackup.Rule(row.getName(), row.getConditionList(), row.getBehaviorList(),
                        row.isOpen(), row.isRetain(), row.isCollect(), row.isPolicy(),
                        row.getTagColor(), row.getCleanTime()));
            }
        }
        // A concurrent delete must not silently produce a partial backup.
        if (!wanted.isEmpty()) throw new IllegalStateException("Selected rule no longer exists");
        return RuleBackup.encode(entries);
    }

    /** Builds new stored rows only, so neither rules nor any notification can be overwritten. */
    public static void importRuleBackup(List<RuleBackup.Rule> rules) {
        List<ConfigRuleData> entries = new ArrayList<>(rules.size());
        for (RuleBackup.Rule rule : rules) {
            ConfigRuleData entry = new ConfigRuleData();
            entry.setName(rule.name);
            entry.setConditionList(rule.conditions);
            entry.setBehaviorList(rule.behaviors);
            entry.setOpen(false);
            entry.setRetain(rule.retain);
            entry.setCollect(rule.collect);
            entry.setPolicy(rule.policy);
            entry.setTagColor(rule.tagColor);
            entry.setCleanTime(rule.cleanTime);
            entry.setBackupAppend(true);
            entries.add(entry);
        }
        AppDatabase.getInstance().configRuleDataDao().insertBackup(entries);
    }

    public static void countNotificationByRuleId(int ruleId, AsyncCallback<Integer> callback) {
        AsyncCallUtils.asyncCall(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return AppDatabase.getInstance()
                        .configRuleDataDao()
                        .queryNotificationByRuleId(ruleId);
            }
        }, callback);
    }

    public static void sortConfigRule(List<ConfigRuleModel> modelList) {
        String data = LocalDataUtil.get().getData(LocalDataKeys.SKEY_SORT_IDS, "");
        if (!TextUtils.isEmpty(data)) {
            String[] idsArray = data.split(";");
            int lastIndex = 0;
            for (int i = 0; i < idsArray.length; i++) {
                try {
                    int id = Integer.parseInt(idsArray[i]);
                    for (int j = 0; j < modelList.size(); j++) {
                        ConfigRuleModel configRuleModel = modelList.get(j);
                        if (configRuleModel.getId() == id) {
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

    public static void deleteConfig(int id, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().configRuleDataDao().deleteById(id);
                return true;
            }
        }, callback);
    }

    public static void findRuleDataById(List<Integer> idList, AsyncCallback<List<ConfigRuleData>> callable) {
        AsyncCallUtils.asyncCall(new Callable<List<ConfigRuleData>>() {
            @Override
            public List<ConfigRuleData> call() throws Exception {
                List<ConfigRuleData> dataList = AppDatabase.getInstance().configRuleDataDao().findDataListById(idList);
                if (idList.contains(0)) {
                    ConfigRuleData configRuleData = new ConfigRuleData();
                    configRuleData.setName(StringUtil.getString(R.string.no_hit_name));
                    configRuleData.setId(0);
                    dataList.add(configRuleData);
                }
                return dataList;
            }
        }, callable);
    }
}
