package com.zhangshuo.magicnotice.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zhangshuo.magicnotice.db.model.NotificationData;

import java.util.List;


@Dao
public interface NotificationDataDao {

    @Insert
    Long insert(NotificationData notificationData);

    @Query("select count(id) from notificationdata")
    Long queryCount();

    @Query("select count(distinct packageName) from notificationdata")
    Long queryAppCount();

    @Query("select * from notificationdata where id=:id")
    NotificationData findData(int id);

    @Query("update notificationdata set readType=1 where id <:lastId")
    void updateReadType(int lastId);

    @Query("select * from notificationdata where ruleId in (:ruIds) order by id desc limit 1")
    NotificationData queryLastNotification(List<Integer> ruIds);

    //查询全部的通知列表
    @Query("select * from notificationdata where id>:id order by id asc limit :pageSize")
    List<NotificationData> findNewAllDataList(int id, int pageSize);

    //查询全部的通知数量
    @Query("select count(*) from notificationdata")
    Long queryAllDataCount();

    //查询全部的通知未读数量
    @Query("select count(*) from notificationdata where readType=0")
    Long queryAllDataNotRead();

    //更新全部的通知已读功能
    @Query("update notificationdata set readType=1 where id <=:id")
    void updateAllDataRead(int id);

    //查询最新的收藏的通知列表
    @Query("select * from notificationdata where id>:id and lock=1 order by id asc limit :pageSize")
    List<NotificationData> findNewLockDataList(int id, int pageSize);

    //查询收藏的通知数量
    @Query("select count(*) from notificationdata where lock=1")
    Long queryLockDataCount();

    //查询全部的收藏通知未读数量
    @Query("select count(*) from notificationdata where lock=1 and readType=0")
    Long queryLockDataNoRead();

    //更新收藏的通知已读功能
    @Query("update notificationdata set readType=1 where lock=1 and id <=:id")
    void updateLockDataRead(int id);

    //查询指定关联规则的通知列表
    @Query("select * from notificationdata where id>:id " +
            "and (:startDay=-1 or postTime>=:startDay) " +
            "and (:ruIdSize=0 or ruleId in (:ruIds)) " +
            "and (:pkgSize=0 or packageName in (:pkgs)) " +
            "order by id asc limit :pageSize")
    List<NotificationData> findNewRuleDataList(int id, long startDay,
                                               int ruIdSize, List<Integer> ruIds,
                                               int pkgSize, List<String> pkgs, int pageSize);

    //查询指定关联规则的通知数量
    @Query("select count(*) from notificationdata where" +
            "(:startDay=-1 or postTime>=:startDay) " +
            "and (:pkgSize=0 or packageName in (:pkgs)) " +
            "and (:ruIdSize=0 or ruleId in (:ruIds))")
    Long queryRuleDataCount(long startDay, int ruIdSize, List<Integer> ruIds, int pkgSize, List<String> pkgs);

    //查询指定关联规则通知未读数量
    @Query("select count(*) from notificationdata where" +
            " readType=0 " +
            "and (:startDay=-1 or postTime>=:startDay) " +
            "and (:pkgSize=0 or packageName in (:pkgs)) " +
            "and (:ruIdSize=0 or ruleId in (:ruIds))")
    Long queryRuleDataNotRead(long startDay, int ruIdSize, List<Integer> ruIds, int pkgSize, List<String> pkgs);

    //更新指定关联规则通知已读功能
    @Query("update notificationdata set readType=1 where " +
            "(:startDay=-1 or postTime>=:startDay) " +
            "and (:pkgSize=0 or packageName in (:pkgs)) " +
            "and (:ruIdSize=0 or ruleId in (:ruIds)) " +
            "and id <=:id")
    void updateRuleDataRead(long startDay, int ruIdSize, List<Integer> ruIds, int pkgSize, List<String> pkgs, int id);

    //查询更多的全部的通知列表
    @Query("select * from notificationdata where id<:id  order by id desc limit :pageSize ")
    List<NotificationData> findOldAllDataList(int id, int pageSize);

    //查询更多的的收藏的通知列表
    @Query("select * from notificationdata where id<:id and lock=1 order by id desc limit :pageSize")
    List<NotificationData> findOldLockDataList(int id, int pageSize);

    //查询更多指定关联规则的通知列表
    @Query("select * from notificationdata where " +
            "id<:id " +
            "and (:startDay=-1 or postTime>=:startDay) " +
            "and (:ruIdSize=0 or ruleId in (:ruIds)) " +
            "and (:pkgSize=0 or packageName in (:pkgs)) " +
            "order by id desc limit :pageSize")
    List<NotificationData> findOldRuleDataList(int id, long startDay,
                                               int ruIdSize, List<Integer> ruIds,
                                               int pkgSize, List<String> pkgs, int pageSize);


    @Query("select * from notificationdata where ruleId=:ruleId order by id desc limit :start,:pageSize ")
    List<NotificationData> findDataByRuleList(int ruleId, long start, int pageSize);

    @Query("select * from notificationdata  where" +
            " (:pkgSize=0 or packageName in (:pkgs))" +
            " and (:ruIdSize=0 or ruleId in (:ruIds))" +
            " and postTime<=:beforeTime" +
            " and lock=0")
    List<NotificationData> findDataByConditions(
            int pkgSize, List<String> pkgs,
            int ruIdSize, List<Integer> ruIds, long beforeTime);

    @Query("delete from notificationdata where " +
            " (:pkgSize=0 or packageName in (:pkgs))" +
            " and (:ruIdSize=0 or ruleId in (:ruIds))" +
            " and postTime<=:beforeTime" +
            " and lock=0")
    void deleteByConditions(int pkgSize,List<String> pkgs, int ruIdSize, List<Integer> ruIds, long beforeTime);

    @Query("delete from notificationdata where id=:id")
    void deleteById(int id);

    @Query("select * from notificationdata where (:packageName='' or packageName=:packageName) " +
            "and (:startDate=0 or postTime>=:startDate) " +
            "and (:endDate=0 or postTime<=:endDate) " +
            "and (:ruleId=-1 or ruleId=:ruleId) " +
            "and (title like :keyword or content like :keyword or appName like :keyword or ruleName like :keyword) " +
            "order by id desc limit :start,:pageSize")
    List<NotificationData> findDataWhereSearch(String keyword, String packageName, int ruleId, long startDate, long endDate, long start, int pageSize);

    @Query("update notificationdata set lock=:lock where id=:id")
    void updateLock(int id, int lock);
}
