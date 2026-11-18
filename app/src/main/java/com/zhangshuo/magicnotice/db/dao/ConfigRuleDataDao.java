package com.zhangshuo.magicnotice.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zhangshuo.magicnotice.db.model.ConfigRuleData;

import java.util.List;

@Dao
public interface ConfigRuleDataDao {

    @Insert
    long insert(ConfigRuleData configRuleData);

    @Update
    void update(ConfigRuleData configRuleData);


    @Query("select * from configruledata where id=:id")
    ConfigRuleData findById(int id);

    @Query("delete from configruledata where id=:id")
    void deleteById(int id);

    @Query("select count(id) from configruledata")
    int queryCount();

    @Query("select * from configruledata order by id desc")
    List<ConfigRuleData> findDataList();

    @Query("select count(id) from notificationdata where ruleId=:ruleId")
    int queryNotificationByRuleId(int ruleId);

    @Query("select * from configruledata where name like :name order by id desc")
    List<ConfigRuleData> findDataList(String name);

    @Query("select * from configruledata where id in (:ids)")
    List<ConfigRuleData> findDataListById(List<Integer> ids);
}
