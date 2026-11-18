package com.zhangshuo.magicnotice.db.service;

import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.zhangshuo.common.util.AsyncCallUtils;
import com.zhangshuo.common.util.AsyncCallback;
import com.zhangshuo.common.util.FileUtil;
import com.zhangshuo.common.util.LocalDataUtil;
import com.zhangshuo.common.util.StringUtil;
import com.zhangshuo.magicnotice.config.LocalDataKeys;
import com.zhangshuo.magicnotice.db.AppDatabase;
import com.zhangshuo.magicnotice.db.model.ConfigRuleData;
import com.zhangshuo.magicnotice.db.model.CountInfo;
import com.zhangshuo.magicnotice.db.model.NotificationData;
import com.zhangshuo.magicnotice.db.model.Page;
import com.zhangshuo.magicnotice.notice.model.NotificationInfo;
import com.zhangshuo.magicnotice.ui.model.ConfigRuleModel;
import com.zhangshuo.magicnotice.ui.model.TabInfo;
import com.zhangshuo.magicnotice.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class NotificationDataService {

    public static void findNotificationById(int id, AsyncCallback<NotificationData> callback) {
        AsyncCallUtils.asyncCall(new Callable<NotificationData>() {
            @Override
            public NotificationData call() throws Exception {
                return AppDatabase.getInstance()
                        .notificationDataDao()
                        .findData(id);
            }
        }, callback);
    }

    public static void queryNotificationCount(AsyncCallback<CountInfo> callback) {
        AsyncCallUtils.asyncCall(new Callable<CountInfo>() {
            @Override
            public CountInfo call() throws Exception {
                Long count = AppDatabase.getInstance()
                        .notificationDataDao()
                        .queryCount();
                Long appCount = AppDatabase.getInstance()
                        .notificationDataDao()
                        .queryAppCount();
                return new CountInfo(count, appCount);
            }
        }, callback);
    }


    public static void queryNotReadCount(List<TabInfo> tabInfoList, AsyncCallback<List<TabInfo>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<TabInfo>>() {
            @Override
            public List<TabInfo> call() throws Exception {
                for (int i = 0; i < tabInfoList.size(); i++) {
                    TabInfo tabInfo = tabInfoList.get(i);
                    Long notReadNum = 0l;
                    if (tabInfo.getId() == -2) {
                        //全部
                        notReadNum = AppDatabase.getInstance().notificationDataDao()
                                .queryAllDataNotRead();
                    } else if (tabInfo.getId() == -1) {
                        //收藏
                        notReadNum = AppDatabase.getInstance().notificationDataDao()
                                .queryLockDataNoRead();
                    } else {
                        long startDay = -1;
                        List<Integer> ruleIds = tabInfo.getRelationIdList();
                        List<String> pkgNames = tabInfo.getRelationPkgList();
                        Date todayYMD = TimeUtil.getTodayYMD();
                        Calendar startCalendar = Calendar.getInstance(Locale.getDefault());
                        startCalendar.setTime(todayYMD);
                        //0,3,7 -> 今日，近三日，仅一周
                        if (tabInfo.getRelationDateOffset() >= 0) {
                            //今日
                            startCalendar.add(Calendar.DAY_OF_MONTH, 0 - tabInfo.getRelationDateOffset());
                            startDay = startCalendar.getTimeInMillis();
                        }
                        notReadNum = AppDatabase.getInstance().notificationDataDao()
                                .queryRuleDataNotRead(startDay, ruleIds.size(), ruleIds,
                                        pkgNames.size(), pkgNames);
                    }
                    tabInfo.setNotReadCount(notReadNum.intValue());
                }
                return tabInfoList;
            }
        }, callback);
    }

    public static void updateNotificationLock(int id, int lock, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().notificationDataDao()
                        .updateLock(id, lock);
                return true;
            }
        }, callback);
    }

    public static void insertNotificationInfo(NotificationInfo notificationInfo, ConfigRuleModel configRuleModel, AsyncCallback<NotificationData> callback) {
        AsyncCallUtils.asyncSingleCall(new Callable<NotificationData>() {
            @Override
            public NotificationData call() throws Exception {
                NotificationData notificationData = new NotificationData();
                notificationData.setNoticeId(notificationInfo.getId());
                notificationData.setKey(notificationInfo.getKey());
                notificationData.setPackageName(notificationInfo.getPackageName());
                notificationData.setPostTime(notificationInfo.getPostTime());
                notificationData.setTitle(notificationInfo.getTitle());
                notificationData.setContent(notificationInfo.getContent());
                notificationData.setReadType(0);//0为未读
                if (configRuleModel != null) {
                    notificationData.setLock(configRuleModel.isCollect() ? 1 : 0);
                    notificationData.setRuleId(configRuleModel.getId());
                    notificationData.setRuleName(configRuleModel.getRuleName());
                }
                //获取appName
                notificationData.setAppName(AppUtils.getAppName(notificationInfo.getPackageName()));
                //获取icon
                String logoPath = FileUtil.saveLogoToCache(notificationData.getPackageName());
                String iconPath = FileUtil.saveIconToCache(notificationInfo.getIcon(), notificationInfo.getIconColor(), notificationData.getPackageName(), notificationData.getKey());
                String picturePath = FileUtil.saveBitmapToCache(notificationInfo.getPictureBitmap(), null, notificationData.getKey());
                notificationData.setLogoPath(logoPath);
                notificationData.setIconPath(iconPath);
                notificationData.setPicturePath(picturePath);
                Long id = AppDatabase.getInstance().notificationDataDao()
                        .insert(notificationData);
                notificationData.setId(Math.toIntExact(id));
                return notificationData;
            }
        }, callback);
    }


    public static void findNewAllNotificationList(int id, int pageSize, AsyncCallback<Page<NotificationData>> callback) {
        AsyncCallUtils.asyncCall(new Callable<Page<NotificationData>>() {
            @Override
            public Page<NotificationData> call() throws Exception {
                List<NotificationData> newDataList = AppDatabase.getInstance().notificationDataDao()
                        .findNewAllDataList(id, pageSize);
                if (newDataList.size() > 0) {
                    AppDatabase.getInstance().notificationDataDao()
                            .updateAllDataRead(newDataList.get(newDataList.size() - 1).getId());
                }
                Collections.reverse(newDataList);
                Long totalCount = AppDatabase.getInstance().notificationDataDao().queryAllDataCount();
                return new Page(id, pageSize, totalCount, newDataList);
            }
        }, callback);
    }

    public static void findNewLockNotificationList(int id, int pageSize, AsyncCallback<Page<NotificationData>> callback) {
        AsyncCallUtils.asyncCall(new Callable<Page<NotificationData>>() {
            @Override
            public Page<NotificationData> call() throws Exception {
                List<NotificationData> newDataList = AppDatabase.getInstance().notificationDataDao()
                        .findNewLockDataList(id, pageSize);
                if (newDataList.size() > 0) {
                    AppDatabase.getInstance().notificationDataDao()
                            .updateLockDataRead(newDataList.get(newDataList.size() - 1).getId());
                }
                Collections.reverse(newDataList);
                Long totalCount = AppDatabase.getInstance().notificationDataDao().queryLockDataCount();
                return new Page(id, pageSize, totalCount, newDataList);
            }
        }, callback);
    }

    public static void findNewRuleNotificationList(int id, int pageSize, TabInfo tabInfo, AsyncCallback<Page<NotificationData>> callback) {
        AsyncCallUtils.asyncCall(new Callable<Page<NotificationData>>() {
            @Override
            public Page<NotificationData> call() throws Exception {
                long startDay = -1;
                List<Integer> ruleIds = tabInfo.getRelationIdList();
                List<String> pkgNames = tabInfo.getRelationPkgList();
                Date todayYMD = TimeUtil.getTodayYMD();
                Calendar startCalendar = Calendar.getInstance(Locale.getDefault());
                startCalendar.setTime(todayYMD);
                //0,3,7 -> 今日，近三日，仅一周
                if (tabInfo.getRelationDateOffset() >= 0) {
                    //今日
                    startCalendar.add(Calendar.DAY_OF_MONTH, 0 - tabInfo.getRelationDateOffset());
                    startDay = startCalendar.getTimeInMillis();
                }
                List<NotificationData> newDataList = AppDatabase.getInstance().notificationDataDao()
                        .findNewRuleDataList(id, startDay, ruleIds.size(), ruleIds, pkgNames.size(), pkgNames, pageSize);
                if (newDataList.size() > 0) {
                    AppDatabase.getInstance().notificationDataDao()
                            .updateRuleDataRead(startDay, ruleIds.size(), ruleIds, pkgNames.size(), pkgNames, newDataList.get(newDataList.size() - 1).getId());
                }
                Collections.reverse(newDataList);
                Long totalCount = AppDatabase.getInstance().notificationDataDao().queryRuleDataCount(startDay, ruleIds.size(), ruleIds, pageSize, pkgNames);
                return new Page(id, pageSize, totalCount, newDataList);
            }
        }, callback);
    }


    public static void findOldAllNotificationList(int id, int pageSize, AsyncCallback<Page<NotificationData>> callback) {
        AsyncCallUtils.asyncCall(new Callable<Page<NotificationData>>() {
            @Override
            public Page<NotificationData> call() throws Exception {
                List<NotificationData> oldDataList = AppDatabase.getInstance().notificationDataDao()
                        .findOldAllDataList(id, pageSize);
                if (oldDataList.size() > 0) {
                    AppDatabase.getInstance().notificationDataDao()
                            .updateAllDataRead(oldDataList.get(0).getId());
                }
                Long totalCount = AppDatabase.getInstance().notificationDataDao().queryAllDataCount();
                return new Page(id, pageSize, totalCount, oldDataList);
            }
        }, callback);
    }

    public static void findOldLockNotificationList(int id, int pageSize, AsyncCallback<Page<NotificationData>> callback) {
        AsyncCallUtils.asyncCall(new Callable<Page<NotificationData>>() {
            @Override
            public Page<NotificationData> call() throws Exception {
                List<NotificationData> oldDataList = AppDatabase.getInstance().notificationDataDao()
                        .findOldLockDataList(id, pageSize);
                if (oldDataList.size() > 0) {
                    AppDatabase.getInstance().notificationDataDao()
                            .updateLockDataRead(oldDataList.get(0).getId());
                }
                Long totalCount = AppDatabase.getInstance().notificationDataDao().queryLockDataCount();
                return new Page(id, pageSize, totalCount, oldDataList);
            }
        }, callback);
    }

    public static void findOldRuleNotificationList(int id, int pageSize, TabInfo tabInfo, AsyncCallback<Page<NotificationData>> callback) {
        AsyncCallUtils.asyncCall(new Callable<Page<NotificationData>>() {
            @Override
            public Page<NotificationData> call() throws Exception {
                long startDay = -1;
                List<Integer> ruleIds = tabInfo.getRelationIdList();
                List<String> pkgNames = tabInfo.getRelationPkgList();
                Date todayYMD = TimeUtil.getTodayYMD();
                Calendar startCalendar = Calendar.getInstance(Locale.getDefault());
                startCalendar.setTime(todayYMD);
                //0,3,7 -> 今日，近三日，仅一周
                if (tabInfo.getRelationDateOffset() >= 0) {
                    //今日
                    startCalendar.add(Calendar.DAY_OF_MONTH, 0 - tabInfo.getRelationDateOffset());
                    startDay = startCalendar.getTimeInMillis();
                }
                List<NotificationData> oldDataList = AppDatabase.getInstance().notificationDataDao()
                        .findOldRuleDataList(id, startDay, ruleIds.size(), ruleIds, pkgNames.size(), pkgNames, pageSize);
                if (oldDataList.size() > 0) {
                    AppDatabase.getInstance().notificationDataDao()
                            .updateRuleDataRead(startDay, ruleIds.size(), ruleIds, pkgNames.size(), pkgNames, oldDataList.get(0).getId());
                }
                Long totalCount = AppDatabase.getInstance().notificationDataDao().queryRuleDataCount(startDay, ruleIds.size(), ruleIds, pkgNames.size(), pkgNames);
                return new Page(id, pageSize, totalCount, oldDataList);
            }
        }, callback);
    }

    public static void findNotificationList(int ruleId, int page, int pageSize, AsyncCallback<List<NotificationData>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<NotificationData>>() {
            @Override
            public List<NotificationData> call() throws Exception {
                int start = (page - 1) * pageSize;
                return AppDatabase.getInstance().notificationDataDao()
                        .findDataByRuleList(ruleId, start, pageSize);
            }
        }, callback);
    }

    public static void findNotificationBySearch(String keyword, String packageName,
                                                int ruleId, long startDate, long endDate, int page, AsyncCallback<List<NotificationData>> callback) {
        AsyncCallUtils.asyncCall(new Callable<List<NotificationData>>() {
            @Override
            public List<NotificationData> call() throws Exception {
                long queryStartTime = 0;
                long queryEndTime = 0;
                if (startDate > 0) {
                    queryStartTime = TimeUtil.parseYMDTime(startDate);
                }
                if (endDate > 0) {
                    Calendar endCalendar = Calendar.getInstance(Locale.getDefault());
                    endCalendar.setTimeInMillis(TimeUtil.parseYMDTime(endDate));
                    endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    queryEndTime = endCalendar.getTimeInMillis();
                }
                String searchKey = "%%";
                if (!TextUtils.isEmpty(keyword)) {
                    searchKey = "%" + keyword + "%";
                }
                String searchPck = "";
                if (!TextUtils.isEmpty(packageName)) {
                    searchPck = packageName;
                }
                int start = (page - 1) * 20;
                return AppDatabase.getInstance().notificationDataDao()
                        .findDataWhereSearch(searchKey, searchPck, ruleId, queryStartTime, queryEndTime, start, 20);
            }
        }, callback);
    }

    public static void deleteById(int id, AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                AppDatabase.getInstance().notificationDataDao()
                        .deleteById(id);
                return true;
            }
        }, callback);
    }

    public static void deleteWithConditions(List<String> packageNameList,
                                            List<Integer> ruleIdList,
                                            long beforeTime, AsyncCallback<CountInfo> callback) {
        AsyncCallUtils.asyncCall(new Callable<CountInfo>() {
            @Override
            public CountInfo call() throws Exception {
                deleteNotification(packageNameList, ruleIdList, beforeTime);
                Long count = AppDatabase.getInstance()
                        .notificationDataDao()
                        .queryCount();
                Long appCount = AppDatabase.getInstance()
                        .notificationDataDao()
                        .queryAppCount();
                return new CountInfo(count, appCount);
            }
        }, callback);
    }

    public static void deleteWithAutoClean(AsyncCallback<Boolean> callback) {
        AsyncCallUtils.asyncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    //先获取所有的配置
                    List<ConfigRuleData> dataList = AppDatabase.getInstance().configRuleDataDao().findDataList();
                    if (dataList.size() > 0) {
                        for (int i = 0; i < dataList.size(); i++) {
                            ConfigRuleData configRuleData = dataList.get(i);
                            String cleanTime = configRuleData.getCleanTime();
                            int cleanTimeInt = StringUtil.stringToInt(cleanTime);
                            if (cleanTimeInt == 0) continue;
                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            calendar.add(Calendar.DATE, -cleanTimeInt);
                            deleteNotification(new ArrayList<>(), new ArrayList<>(Arrays.asList(configRuleData.getId())), calendar.getTimeInMillis());
                        }
                    }
                    //处理未命中规则的通知删除情况
                    String noHitCleanTimeStr = LocalDataUtil.get().getData(LocalDataKeys.SKEY_NO_HIT_CLEAN_TIME, "0");
                    int noHitCleanTime = StringUtil.stringToInt(noHitCleanTimeStr);
                    if (noHitCleanTime > 0) {
                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                        calendar.add(Calendar.DATE, -noHitCleanTime);
                        deleteNotification(new ArrayList<>(), new ArrayList<>(Arrays.asList(0)), calendar.getTimeInMillis());
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }, callback);
    }

    private static void deleteNotification(List<String> packageNameList,
                                           List<Integer> ruleIdList,
                                           long beforeTime) {

        List<NotificationData> notificationDataList = AppDatabase.getInstance().notificationDataDao().findDataByConditions(
                packageNameList.size(), packageNameList, ruleIdList.size(), ruleIdList, beforeTime
        );
        AppDatabase.getInstance().notificationDataDao()
                .deleteByConditions(packageNameList.size(), packageNameList, ruleIdList.size(), ruleIdList, beforeTime);

        for (int i = 0; i < notificationDataList.size(); i++) {
            NotificationData notificationData = notificationDataList.get(i);
            String iconPath = notificationData.getIconPath();
            String picturePath = notificationData.getPicturePath();
            FileUtils.delete(iconPath);
            FileUtils.delete(picturePath);
        }
    }

}
